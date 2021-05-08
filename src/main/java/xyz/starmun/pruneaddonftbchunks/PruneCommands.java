package xyz.starmun.pruneaddonftbchunks;

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

        event.getDispatcher().register(Commands.literal("prune")
            //.requires(source -> source.hasPermission(2))
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

    private static int prune(CommandSourceStack source, @Nullable ServerLevel level, String subDirectory, boolean doNotBackup) {
        ResourceKey<Level> levelKey = level == null ? Level.OVERWORLD : level.dimension();
        String levelDataPath = LevelDataDirectory.getDirectoryFromDimensionKey(levelKey, subDirectory);
        String backupPath = doNotBackup ? null : levelDataPath + "pruned/" + subDirectory + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + "/";

        Collection<String> namesOfRegionFilesWithClaimedChunks = PruneManager.getPruneManager().getNamesOfRegionFilesWithClaimedChunks(levelKey);
        if (PruneManager.getPruneManager().pruneRegionFiles(levelDataPath, backupPath, namesOfRegionFilesWithClaimedChunks, !doNotBackup)) {
            source.sendSuccess(new TextComponent(doNotBackup ? "Pruned successfully!" : "Completed successfully!, pruned files backed up to: " + backupPath), false);
            return 1;
        }
        source.sendFailure(new TextComponent("Failed to prune, check the log for details."));
        return 1;
    }
}
