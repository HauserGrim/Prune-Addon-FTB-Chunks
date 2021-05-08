package xyz.starmun.pruneaddonftbchunks.data;

import com.feed_the_beast.mods.ftbchunks.api.FTBChunksAPI;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

public class LevelDataDirectory {
    private static final String levelDataDirectory  = FTBChunksAPI.INSTANCE.getManager().getMinecraftServer().getWorldPath(LevelResource.LEVEL_DATA_FILE).getParent().toString();
    public static String getDirectoryFromDimensionKey(ResourceKey<Level> levelKey, String subDirectory) {
        if (Level.OVERWORLD.equals(levelKey)) {

            return levelDataDirectory  + "/"+ subDirectory+ "/";
        } else if (Level.NETHER.equals(levelKey)) {
            return levelDataDirectory + "/DIM-1/"+ subDirectory+ "/";
        } else if (Level.END.equals(levelKey)) {
            return levelDataDirectory + "/DIM1/"+ subDirectory+ "/";
        } else {
            return levelDataDirectory + "/dimensions/"
              + levelKey.location().getNamespace() + "/" + levelKey.location().getPath() + "/"+ subDirectory+ "/";
        }
    }
}