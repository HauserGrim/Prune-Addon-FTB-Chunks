package xyz.starmun.pruneaddonftbchunks;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.starmun.pruneaddonftbchunks.commands.PruneCommands;

public class PruneAddonFTBChunks   {
	public static final String MOD_ID ="pruneaddonftbchunks";
	public static final Logger LOGGER = LogManager.getLogger("Prune Addon for FTB Chunks");

	public static boolean releaseMouse(){
		 Minecraft.getInstance().mouseHandler.releaseMouse();
		 return true;
	}
	public static void init() {
		// TODO
		//ArgumentTypeRegistry.registerArgumentType(null, EnumArgument.class, new EnumArgument.Serializer());
		//ArgumentTypes.register("pafc:enum", EnumArgument.class, (ArgumentSerializer) new EnumArgument.Serializer());
		CommandRegistrationEvent.EVENT.register(PruneCommands::register);
	}
}
