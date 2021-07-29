package xyz.starmun.pruneaddonftbchunks.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;

@Mod(PruneAddonFTBChunks.MOD_ID)
public class PruneAddonFTBChunksForge {
    public PruneAddonFTBChunksForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(PruneAddonFTBChunks.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        PruneAddonFTBChunks.init();
    }
}
