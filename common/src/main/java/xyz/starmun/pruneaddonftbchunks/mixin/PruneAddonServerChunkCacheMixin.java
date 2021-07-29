package xyz.starmun.pruneaddonftbchunks.mixin;

import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.starmun.pruneaddonftbchunks.contracts.IServerChunkCacheExtensions;

@Mixin(ServerChunkCache.class)
public class PruneAddonServerChunkCacheMixin implements IServerChunkCacheExtensions {
    @Shadow @Final private DistanceManager distanceManager;

    public DistanceManager pa$getDistanceManager(){
        return distanceManager;
    }
}
