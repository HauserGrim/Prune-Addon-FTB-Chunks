package xyz.starmun.pruneaddonftbchunks.services;

import com.mojang.datafixers.util.Either;
import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.XZ;
import me.shedaniel.architectury.utils.GameInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;
import xyz.starmun.pruneaddonftbchunks.contracts.*;
import xyz.starmun.pruneaddonftbchunks.data.DataFileType;
import xyz.starmun.pruneaddonftbchunks.data.LevelDataDirectory;
import xyz.starmun.pruneaddonftbchunks.data.PrunePacket;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PruneManager {

    public PruneManager() {

    }

    private static PruneManager pruneManager = null;

    public static PruneManager getPruneManager() {
        return pruneManager == null ? pruneManager = new PruneManager() : pruneManager;
    }

    public static boolean prune(@Nullable ServerLevel level, @Nullable DataFileType subDirectory, @Nullable boolean doNotBackup) {
        ResourceKey<Level> levelKey = level == null ? Level.OVERWORLD : level.dimension();
        String levelDataPath = LevelDataDirectory.getDirectoryFromDimensionKey(levelKey, subDirectory == DataFileType.REGION_FILES ? "region" : "poi");
        String backupPath = doNotBackup ? null : levelDataPath + "pruned/" + subDirectory + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + "/";
        Collection<String> namesOfRegionFilesWithClaimedChunks = PruneManager.getPruneManager().getNamesOfRegionFilesWithClaimedChunks(levelKey);
        return PruneManager.getPruneManager().pruneRegionFiles(levelDataPath, backupPath, namesOfRegionFilesWithClaimedChunks, !doNotBackup, level);
    }

    public static boolean manuallyPruneClaimAdjacentChunks(ResourceKey<Level> levelKey) {
        MinecraftServer server = GameInstance.getServer();
        ServerLevel serverLevel = server.getLevel(levelKey);
        PruneAddonFTBChunks.LOGGER.info("Saving previous changes.");
        serverLevel.save(null, true, false);
        PruneAddonFTBChunks.LOGGER.info("Done saving previous changes.");

        try {
            ChunkMap chunkMap = serverLevel.getChunkSource().chunkMap;
            List<XZ> claimedRegions = PruneManager.getPruneManager().getAllClaimedRegions(levelKey);
            List<ClaimedChunk> claimedChunks = PruneManager.getPruneManager().getAllClaimedChunksForLevel(levelKey);
            IChunkMapExtensions chunkMapExtensions = ((IChunkMapExtensions) chunkMap);

            int oldLevel = 0;
            for (XZ region : claimedRegions) {
                for (int x = region.x; x < region.x + 32; x++) {
                    for (int z = region.z; z < region.z + 32; z++) {
                        ChunkPos chunkPos = new ChunkPos(x, z);

                        CompoundTag tag = chunkMap.read(chunkPos);
                        ChunkHolder holder = chunkMapExtensions.pa$getVisibleChunkIfPresent(chunkPos.toLong());
                        if (holder != null) {
                            //Server unload
                            chunkMapExtensions.pa$updateChunkScheduling(chunkPos.toLong(), ChunkMap.MAX_CHUNK_DISTANCE + 1, holder, oldLevel);
                            chunkMapExtensions.pa$processUnloads(() -> true);
                            oldLevel = holder.getTicketLevel();
                        }
                        if (claimedChunks.stream().anyMatch(chunk -> chunk.pos.x == chunkPos.x && chunk.pos.z == chunkPos.z)) {
                            claimedChunks.removeIf(chunk -> chunk.pos.x == chunkPos.x && chunk.pos.z == chunkPos.z);
                        } else if (tag != null) {
                            tag.remove("Level");
                            CompoundTag levelTag = new CompoundTag();
                            levelTag.putString("Status", "");
                            levelTag.putInt("xPos", x);
                            levelTag.putInt("zPos", z);
                            tag.put("Level", levelTag);
                            PruneAddonFTBChunks.LOGGER.info("Pruning chunk" + x + ", " + z);
                            chunkMap.write(chunkPos, tag);
                        }
                        if (holder != null) {
                            //Server Reload
                            ChunkHolder newHolder = chunkMapExtensions.pa$updateChunkScheduling(chunkPos.toLong(), oldLevel, null, ChunkMap.MAX_CHUNK_DISTANCE + 1);
                            chunkMap.schedule(newHolder, ChunkStatus.FULL);
                            ((IDistanceManagerExtensions) ((IServerChunkCacheExtensions) serverLevel.getChunkSource()).pa$getDistanceManager()).pa$markChunkToBeUpdated(newHolder);
                        }
                    }
                }
            }
            GameInstance.getServer().getPlayerList().getPlayers().forEach(player -> {
                player.connection.send(new ClientboundDisconnectPacket(new TextComponent("Done pruning, rejoin now.")));
            });

        } catch (Throwable ex) {
            PruneAddonFTBChunks.LOGGER.error("failed", ex);
            return false;
        }
        return true;
    }

    public List<XZ> getAllClaimedRegions(ResourceKey<Level> level) {
        return FTBChunksAPI.getManager().getAllClaimedChunks().stream()
                .filter(map -> map.getPos().dimension.equals(level))
                .map(map -> XZ.of((map.pos.x >> 5) << 5, (map.pos.z >> 5) << 5))
                .distinct().collect(Collectors.toList());
    }

    public List<ClaimedChunk> getAllClaimedChunksForLevel(ResourceKey<Level> level) {
        return FTBChunksAPI.getManager().getAllClaimedChunks().stream().filter(map -> map.getPos().dimension.equals(level))
                .collect(Collectors.toList());
    }

    public Collection<String> getNamesOfRegionFilesWithClaimedChunks(ResourceKey<Level> level) {
        HashSet<String> namesOfRegionFilesWithClaimedChunks = new HashSet<>();

        for (ClaimedChunk claimedChunk : this.getAllClaimedChunksForLevel(level)) {
            XZ region = XZ.regionFromChunk(claimedChunk.getPos().getChunkPos());
            namesOfRegionFilesWithClaimedChunks.add("r." + region.x + "." + region.z + ".mca");
        }
        return new ArrayList<>(namesOfRegionFilesWithClaimedChunks);
    }

    public boolean pruneRegionFiles(String fromPath, @Nullable String toPath, Collection<String> filterFileNames, boolean doBackup, ServerLevel level) {

        try (java.util.stream.Stream<Path> stream = Files.list(Paths.get(fromPath))) {

            PruneAddonFTBChunks.LOGGER.info("Pruning from: " + fromPath);

            Set<String> regionFileNames = stream.filter(path -> !Files.isDirectory(path))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> "mca".equals(FilenameUtils.getExtension(name)))
                    .collect(Collectors.toSet());

            regionFileNames.removeAll(filterFileNames);

            if (doBackup) {
                if (toPath == null) {
                    throw new FileNotFoundException("Backup path is null.");
                }
                Files.createDirectories(Paths.get(toPath));
                for (String filename : regionFileNames) {
                    Files.move(Paths.get(fromPath + filename), Paths.get(toPath + filename), StandardCopyOption.REPLACE_EXISTING);
                    PruneAddonFTBChunks.LOGGER.info("Moved file: " + filename + " to " + toPath + filename);
                }

            } else {
                for (String filename : regionFileNames) {
                    Files.delete(Paths.get(fromPath + filename));
                    PruneAddonFTBChunks.LOGGER.info("Removed file: " + filename);
                }
            }
        } catch (Throwable ex) {
            PruneAddonFTBChunks.LOGGER.error("Failed to prune files " + ex);
            return false;
        }
        return true;
    }
}
