package xyz.starmun.pruneaddonftbchunks.services;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;
import xyz.starmun.pruneaddonftbchunks.data.DataFileType;
import xyz.starmun.pruneaddonftbchunks.data.LevelDataDirectory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class BackupManager {
    public static boolean backupClaimContainingFiles(ServerLevel level, DataFileType subDirectory) {
        try {
            ResourceKey<Level> levelKey = level == null ? Level.OVERWORLD : level.dimension();
            String levelDataPath = LevelDataDirectory.getDirectoryFromDimensionKey(levelKey, subDirectory == DataFileType.REGION_FILES ? "region" : "poi");
            String backupPath = levelDataPath + "pruned/" + subDirectory + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + "/";
            Collection<String> namesOfRegionFilesWithClaimedChunks = PruneManager.getPruneManager().getNamesOfRegionFilesWithClaimedChunks(levelKey);
            for (String filename : namesOfRegionFilesWithClaimedChunks) {
                Files.copy(Paths.get(levelDataPath + filename), Paths.get(backupPath + filename), StandardCopyOption.REPLACE_EXISTING);
                PruneAddonFTBChunks.LOGGER.info("Copied file: " + filename + " to " + backupPath + filename);
            }
        } catch (Exception ex) {
            PruneAddonFTBChunks.LOGGER.error("Failed to backup claim containing files", ex);
            return false;
        }
        return true;
    }
}