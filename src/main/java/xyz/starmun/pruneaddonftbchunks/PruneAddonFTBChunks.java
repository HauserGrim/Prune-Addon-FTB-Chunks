package xyz.starmun.pruneaddonftbchunks;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

	@OnlyIn(Dist.CLIENT)
	public static boolean releaseMouse() {
		Minecraft.getInstance().mouseHandler.releaseMouse();
		return true;
	}

}
