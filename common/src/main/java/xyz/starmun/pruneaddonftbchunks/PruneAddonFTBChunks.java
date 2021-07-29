package xyz.starmun.pruneaddonftbchunks;

import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.starmun.pruneaddonftbchunks.commands.PruneCommands;
import xyz.starmun.pruneaddonftbchunks.commands.arguments.EnumArgument;
import xyz.starmun.pruneaddonftbchunks.data.DataFileType;

public class PruneAddonFTBChunks   {
	public static final String MOD_ID ="pruneaddonftbchunks";
	public static final Logger LOGGER = LogManager.getLogger("Prune Addon for FTB Chunks");

	public static boolean releaseMouse(){
		 Minecraft.getInstance().mouseHandler.releaseMouse();
		 return true;
	}
	public static void init() {
		ArgumentTypes.register("pafc:enum", EnumArgument.class, (ArgumentSerializer) new EnumArgument.Serializer());
		CommandRegistrationEvent.EVENT.register(PruneCommands::register);
	}
}
