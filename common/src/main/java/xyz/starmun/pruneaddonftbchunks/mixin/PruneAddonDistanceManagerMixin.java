package xyz.starmun.pruneaddonftbchunks.mixin;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.starmun.pruneaddonftbchunks.contracts.IDistanceManagerExtensions;
import xyz.starmun.pruneaddonftbchunks.contracts.IServerChunkCacheExtensions;

import java.util.Set;

@Mixin(DistanceManager.class)
public class PruneAddonDistanceManagerMixin implements IDistanceManagerExtensions {

    @Shadow @Final private Set<ChunkHolder> chunksToUpdateFutures;

    @Override
    public void pa$markChunkToBeUpdated(ChunkHolder chunkHolder) {
        chunksToUpdateFutures.add(chunkHolder);
    }
}
