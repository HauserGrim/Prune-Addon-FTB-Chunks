package xyz.starmun.pruneaddonftbchunks.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ChunkMap;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;

public class PruneAddonFTBChunksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PruneAddonFTBChunks.init();
    }

    private void test(ChunkMap map){
    }

}
