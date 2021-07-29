package xyz.starmun.pruneaddonftbchunks.fabric;

import net.fabricmc.api.ModInitializer;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;

public class PruneAddonFTBChunksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PruneAddonFTBChunks.init();
    }
}
