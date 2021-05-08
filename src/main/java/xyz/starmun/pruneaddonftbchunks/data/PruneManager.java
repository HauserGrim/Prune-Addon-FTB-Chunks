package xyz.starmun.pruneaddonftbchunks.data;

import com.feed_the_beast.mods.ftbchunks.api.ClaimedChunk;
import com.feed_the_beast.mods.ftbchunks.api.FTBChunksAPI;
import com.feed_the_beast.mods.ftbchunks.impl.XZ;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class PruneManager {

    public PruneManager() {

    }
    private static PruneManager pruneManager = null;
    public static PruneManager getPruneManager(){
        return pruneManager==null? pruneManager = new PruneManager():pruneManager;
    }

    public Collection<ClaimedChunk> getAllClaimedChunksForLevel(ResourceKey<Level> level) {
        return FTBChunksAPI.INSTANCE.getManager().getAllClaimedChunks().stream().filter(map->map.getPos().dimension.equals(level))
          .collect(Collectors.toList());
    }
    public Collection<String> getNamesOfRegionFilesWithClaimedChunks(ResourceKey<Level> level){
        java.util.HashSet<String> namesOfRegionFilesWithClaimedChunks = new java.util.HashSet<>();

        for (ClaimedChunk claimedChunk : this.getAllClaimedChunksForLevel(level)) {
            XZ region = XZ.regionFromChunk(claimedChunk.getPos().getChunkPos());
            namesOfRegionFilesWithClaimedChunks.add("r."+ region.x +"."+ region.z+ ".mca");
        }
        return new ArrayList<>(namesOfRegionFilesWithClaimedChunks);
    }

    public boolean pruneRegionFiles(String fromPath, @Nullable String toPath, Collection<String> filterFileNames, boolean doBackup){

        try(java.util.stream.Stream<Path> stream =  Files.list(Paths.get(fromPath))){

            PruneAddonFTBChunks.LOGGER.info("Pruning from: " + fromPath);

            Set<String> regionFileNames = stream.filter(path -> !Files.isDirectory(path))
              .map(Path::getFileName)
              .map(Path::toString)
              .filter(name-> "mca".equals(FilenameUtils.getExtension(name)))
              .collect(Collectors.toSet());

            regionFileNames.removeAll(filterFileNames);

            if(doBackup){
                if (toPath==null){
                    throw new FileNotFoundException("Backup path is null.");
                }
                Files.createDirectories(Paths.get(toPath));
                for(String filename : regionFileNames){
                    Files.move(Paths.get(fromPath + filename), Paths.get(toPath + filename), StandardCopyOption.REPLACE_EXISTING);
                    PruneAddonFTBChunks.LOGGER.info("Moved file: "+filename+ " to " + toPath + filename);
                }
            }
            else{
                for(String filename : regionFileNames){
                    Files.delete(Paths.get(fromPath + filename));
                    PruneAddonFTBChunks.LOGGER.info("Removed file: "+filename);
                }
            }
        }
        catch(Throwable ex){
            PruneAddonFTBChunks.LOGGER.error("Failed to prune files "+ex);
            return false;
        }
        return true;
    }
}
