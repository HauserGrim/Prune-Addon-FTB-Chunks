package xyz.starmun.pruneaddonftbchunks.data;

import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.XZ;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.storage.LevelData;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;

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
        //return PruneManager.prune(level, subDirectory == DataFileType.REGION_FILES ? "region" : "poi", doNotBackup);
    }

    public static boolean manuallyPruneClaimAdjacentChunks(ResourceKey<Level> levelKey) {
        ServerLevel playerLevel = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
        try {
            HashSet<ServerPlayer> serverPlayers = new HashSet<>();
            ChunkMap chunkMap = playerLevel.getChunkSource().chunkMap;
            List<XZ> claimedRegions = PruneManager.getPruneManager().getAllClaimedRegions(levelKey);
            List<ClaimedChunk> claimedChunks = PruneManager.getPruneManager().getAllClaimedChunksForLevel(levelKey);
            int oldLevel = 0;
            for (XZ region : claimedRegions) {
                for (int x = region.x; x < region.x + 32; x++) {
                    for (int z = region.z; z < region.z + 32; z++) {
                        ChunkPos chunkPos = new ChunkPos(x, z);

                        CompoundTag tag = chunkMap.read(chunkPos);
                        ChunkHolder holder = chunkMap.getVisibleChunkIfPresent(chunkPos.toLong());

                        if (holder != null) {
                            oldLevel = holder.getTicketLevel();

                            chunkMap.getPlayers(chunkPos, false).forEach(player -> serverPlayers.add(player));
                            //Server unload
                            chunkMap.updateChunkScheduling(chunkPos.toLong(), ChunkMap.MAX_CHUNK_DISTANCE + 1, holder, oldLevel);
                            chunkMap.processUnloads(() -> true);
                        }
                        if(claimedChunks.stream().anyMatch(chunk->chunk.pos.x == chunkPos.x && chunk.pos.z == chunkPos.z)){
                            claimedChunks.removeIf(chunk->chunk.pos.x == chunkPos.x && chunk.pos.z == chunkPos.z);
                        }else {
                            tag.remove("Level");
                            CompoundTag levelTag = new CompoundTag();
                            levelTag.putString("Status", "empty");
                            levelTag.putInt("xPos", x);
                            levelTag.putInt("zPos", z);
                            levelTag.putBoolean("isLightOn", true);
                            tag.put("Level", levelTag);
                            PruneAddonFTBChunks.LOGGER.info("Pruning chunk" + x + ", " + z);
                            chunkMap.write(chunkPos, tag);
                        }
                        if (holder != null) {

                            //Server Reload
                            ChunkHolder newHolder = chunkMap.updateChunkScheduling(chunkPos.toLong(), oldLevel, null, ChunkMap.MAX_CHUNK_DISTANCE + 1);
                            chunkMap.schedule(newHolder, ChunkStatus.FULL);
                            playerLevel.getChunkSource().distanceManager.chunksToUpdateFutures.add(newHolder);
                        }
                    }
                }
            }
            serverPlayers.forEach(player -> {
                ServerLevel serverworld = player.getLevel();// 1328
                LevelData iworldinfo = playerLevel.getLevelData();// 1329
                player.connection.send(new PrunePacket(playerLevel.dimensionType(), playerLevel.dimension(), BiomeManager.obfuscateSeed(playerLevel.getSeed()), player.gameMode.getGameModeForPlayer(), player.gameMode.getPreviousGameModeForPlayer(), playerLevel.isDebug(), playerLevel.isFlat(), true, true));// 1330
                player.connection.send(new ClientboundChangeDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));// 1331
                player.server.getPlayerList().sendPlayerPermissionLevel(player);// 1332
                serverworld.removePlayer(player, true);// 1333
                player.revive();// 1334
                player.moveTo(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);// 1335
                player.setLevel(playerLevel);// 1336
                playerLevel.addDuringCommandTeleport(player);// 1337
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);// 1339
                player.gameMode.setLevel(playerLevel);// 1340
                player.server.getPlayerList().sendLevelInfo(player, playerLevel);// 1341
                player.server.getPlayerList().sendAllPlayerInfo(player);// 1342
                BasicEventHooks.firePlayerChangedDimensionEvent(player, serverworld.dimension(), playerLevel.dimension());// 1343
            });
        } catch (Throwable ex) {
            PruneAddonFTBChunks.LOGGER.info(ex);
            return  false;
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
        java.util.HashSet<String> namesOfRegionFilesWithClaimedChunks = new java.util.HashSet<>();

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
