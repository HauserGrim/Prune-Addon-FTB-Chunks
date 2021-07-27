package xyz.starmun.pruneaddonftbchunks;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import xyz.starmun.pruneaddonftbchunks.data.LevelDataDirectory;
import xyz.starmun.pruneaddonftbchunks.data.PruneManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Mod.EventBusSubscriber
public class PruneCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {

        event.getDispatcher()
            .register(Commands.literal("prune").requires(source -> source.hasPermission(2))
            .executes(context -> prune(context.getSource()))
            .then(Commands.literal("region_files")
                .executes(context -> prune(context.getSource(), null, "region", false))
                .then(Commands.literal("dimension")
                    .then(Commands.argument("dim", DimensionArgument.dimension())
                        .executes(context -> prune(context.getSource(), DimensionArgument.getDimension(context, "dim"), "region", false))
                        .then(Commands.literal("do_not_backup")
                            .then(Commands.argument("doNotBackup", BoolArgumentType.bool())
                                .executes(context -> prune(context.getSource(), DimensionArgument.getDimension(context, "dim"), "region", BoolArgumentType.getBool(context, "doNotBackup")))
                            )
                        )
                    )
                )
            )
            .then(Commands.literal("poi_files")
                .executes(context -> prune(context.getSource(), null, "poi", false))
                .then(Commands.literal("dimension")
                    .then(Commands.argument("dim", DimensionArgument.dimension())
                        .executes(context -> prune(context.getSource(), DimensionArgument.getDimension(context, "dim"), "poi", false))
                        .then(Commands.literal("do_not_backup")
                            .then(Commands.argument("doNotBackup", BoolArgumentType.bool())
                                .executes(context -> prune(context.getSource(), DimensionArgument.getDimension(context, "dim"), "poi", BoolArgumentType.getBool(context, "doNotBackup")))
                            )
                        )
                    )
                )
            )
        );
    }

    private static int prune(CommandSourceStack source){
        prune(source,null,"region",false);
        prune(source,null,"poi",false);
        return 1;
    }

    private static int prune(CommandSourceStack source, @Nullable ServerLevel level,@Nullable String subDirectory, boolean doNotBackup) {

       if (PruneManager.prune(level, subDirectory,doNotBackup)) {
            source.sendSuccess(new TextComponent("Pruned "+subDirectory+" successfully!" ), false);

           return 1;
        }
        source.sendFailure(new TextComponent("Failed to prune, check the log for details."));
        return 1;
    }
}
