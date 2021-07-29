package xyz.starmun.pruneaddonftbchunks.contracts;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;

import java.util.function.BooleanSupplier;

public interface IChunkMapExtensions {
    ChunkHolder pa$getVisibleChunkIfPresent(long position);

    ChunkHolder pa$updateChunkScheduling(long position, int newLevel, ChunkHolder chunkHolder, int oldLevel);

    void pa$processUnloads(BooleanSupplier booleanSupplier);
}
