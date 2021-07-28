package xyz.starmun.pruneaddonftbchunks;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PruneAddonFTBChunks   {
	public static final String MOD_ID ="pruneaddonftbchunks";
	public static final Logger LOGGER = LogManager.getLogger("Prune Addon for FTB Chunks");

	public static boolean releaseMouse(){
		 Minecraft.getInstance().mouseHandler.releaseMouse();
		 return true;
	}
	public static void init() {

	}
}
