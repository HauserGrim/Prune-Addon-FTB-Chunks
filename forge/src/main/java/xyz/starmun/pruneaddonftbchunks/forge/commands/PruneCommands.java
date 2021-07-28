package xyz.starmun.pruneaddonftbchunks.forge.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.EnumArgument;
import org.jetbrains.annotations.Nullable;
import xyz.starmun.pruneaddonftbchunks.data.DataFileType;
import xyz.starmun.pruneaddonftbchunks.services.BackupManager;
import xyz.starmun.pruneaddonftbchunks.services.PruneManager;

@Mod.EventBusSubscriber
public class PruneCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {

        event.getDispatcher().register(Commands.literal("prune").requires(source -> {
            return source.hasPermission(2); //
        }).executes(context -> {
            return prune(context.getSource(), false, null, null, false); //
        }).then(Commands.literal("-deep").then(Commands.argument("deep", BoolArgumentType.bool()).executes(context ->{
            return prune(context.getSource(), BoolArgumentType.getBool(context, "deep"), null, null, false); //
        }).then(Commands.literal("-file_type").then(Commands.argument("files", EnumArgument.enumArgument(DataFileType.class)).executes(context -> {
            return prune(context.getSource(), BoolArgumentType.getBool(context, "deep"), null, context.getArgument("files", DataFileType.class), false); //
        }).then(Commands.literal("-dimension").then(Commands.argument("dim", DimensionArgument.dimension()).executes(context -> {
            return prune(context.getSource(), BoolArgumentType.getBool(context, "deep"), DimensionArgument.getDimension(context, "dim"), context.getArgument("files", DataFileType.class), false); //
        }).then(Commands.literal("-do_not_backup").then(Commands.argument("doNotBackup", BoolArgumentType.bool()).executes(context -> {
            return prune(context.getSource(), BoolArgumentType.getBool(context, "deep"), DimensionArgument.getDimension(context, "dim"), context.getArgument("files", DataFileType.class), BoolArgumentType.getBool(context, "doNotBackup")); //
        }))))))))));
    }

    private static int prune(CommandSourceStack source, boolean deep, @Nullable ServerLevel level, @Nullable DataFileType subDirectory, boolean doNotBackup) {

        source.sendSuccess(new TextComponent("Starting Prune"), true);
        ResourceKey<Level> levelKey = level == null ? Level.OVERWORLD : level.dimension();

        if (subDirectory == null) {
            source.sendSuccess(new TextComponent("Pruning region files."), true);
            if (PruneManager.prune(level, DataFileType.REGION_FILES, doNotBackup)) {
                source.sendSuccess(new TextComponent("Pruned " + DataFileType.REGION_FILES + " successfully!"), false);
            } else {
                source.sendFailure(new TextComponent("Failed to prune region" + ", check the log for details."));
            }
            source.sendSuccess(new TextComponent("Pruning poi files."), true);

            if (PruneManager.prune(level, DataFileType.POI_FILES, doNotBackup)) {
                source.sendSuccess(new TextComponent("Pruned " + DataFileType.POI_FILES + " successfully!"), false);
            } else {
                source.sendFailure(new TextComponent("Failed to prune poi, check the log for details."));
            }
        } else {
            if (PruneManager.prune(level, subDirectory, doNotBackup)) {
                source.sendSuccess(new TextComponent("Pruned " + subDirectory + " successfully!"), false);
            } else {
                source.sendFailure(new TextComponent("Failed to prune " + subDirectory + ", check the log for details."));
            }
        }

        if (deep) {
            source.sendSuccess(new TextComponent("Starting deep pruning of claim adjacent chunks."), true);
            if(!doNotBackup) {
                source.sendSuccess(new TextComponent("Starting backup."), true);
                if(!BackupManager.backupClaimContainingFiles(level,DataFileType.REGION_FILES)){
                    source.sendFailure(new TextComponent("Backup Failed, Aborting."));
                    return 1;
                }
                else {
                    source.sendSuccess(new TextComponent("Starting deep prune."), true);
                }
            }
            if (PruneManager.manuallyPruneClaimAdjacentChunks(levelKey)) {
                source.sendSuccess(new TextComponent("Deep prune complete."), true);
            } else {
                source.sendSuccess(new TextComponent("An error occurred while deep pruning, check the log for details."), true);
            }
        }
        return 1;
    }
}
