package xyz.starmun.pruneaddonftbchunks.data;

import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class PrunePacket extends ClientboundRespawnPacket {
    public boolean isPruning;
    public PrunePacket(DimensionType arg, ResourceKey<Level> arg2, long l, GameType arg3, GameType arg4, boolean bl, boolean bl2, boolean bl3,boolean isPruning) {
        super(arg,arg2,l,arg3,arg4,bl,bl2,bl3);
        this.isPruning = isPruning;
    }
}
