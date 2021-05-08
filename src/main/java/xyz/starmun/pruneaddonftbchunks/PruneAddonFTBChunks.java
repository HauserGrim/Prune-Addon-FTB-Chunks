package xyz.starmun.pruneaddonftbchunks;

import com.feed_the_beast.mods.ftbchunks.FTBChunks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PruneAddonFTBChunks.MODID)
public class PruneAddonFTBChunks   {
	public static final String MODID="pruneaddonftbchunks";
	public static final Logger LOGGER = LogManager.getLogger("Prune Addon for FTB Chunks");

	public PruneAddonFTBChunks(){
		MinecraftForge.EVENT_BUS.register(this);
	}
}
