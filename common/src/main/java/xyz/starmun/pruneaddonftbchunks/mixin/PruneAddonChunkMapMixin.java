package xyz.starmun.pruneaddonftbchunks.mixin;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.starmun.pruneaddonftbchunks.contracts.IChunkMapExtensions;
import xyz.starmun.pruneaddonftbchunks.contracts.IDistanceManagerExtensions;

import java.util.Set;
import java.util.function.BooleanSupplier;

@Mixin(ChunkMap.class)
public abstract class PruneAddonChunkMapMixin implements IChunkMapExtensions {
    @Shadow @Nullable protected abstract ChunkHolder getVisibleChunkIfPresent(long l);

    @Shadow @Nullable protected abstract ChunkHolder updateChunkScheduling(long l, int i, @Nullable ChunkHolder chunkHolder, int j);

    @Shadow protected abstract void processUnloads(BooleanSupplier booleanSupplier);

    @Unique
    public ChunkHolder pa$getVisibleChunkIfPresent(long position){
        return this.getVisibleChunkIfPresent(position);
    }
    @Unique
    public ChunkHolder pa$updateChunkScheduling(long position, int newLevel, ChunkHolder chunkHolder, int oldLevel){
        return updateChunkScheduling(position,newLevel,chunkHolder,oldLevel);
    }
    @Unique
    public void pa$processUnloads(BooleanSupplier booleanSupplier){
        processUnloads(booleanSupplier);
    }
}
