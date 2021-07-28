package xyz.starmun.pruneaddonftbchunks.contracts;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;

import java.util.function.BooleanSupplier;

public interface IServerChunkCacheExtensions {
    DistanceManager pa$getDistanceManager();
}
