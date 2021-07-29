package xyz.starmun.pruneaddonftbchunks.contracts;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;

import java.util.function.BooleanSupplier;

public interface IDistanceManagerExtensions {
    void pa$markChunkToBeUpdated(ChunkHolder chunkHolder);
}