package xyz.starmun.pruneaddonftbchunks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.starmun.pruneaddonftbchunks.PruneAddonFTBChunks;
import xyz.starmun.pruneaddonftbchunks.data.PrunePacket;

@Mixin(ClientPacketListener.class)
public class PruneAddonClientPacketListenerMixin {
    @Shadow private ClientLevel.ClientLevelData levelData;
    @Shadow private ClientLevel level;
    @Shadow private Minecraft minecraft;
    @Shadow private int serverChunkRadius;
    @Unique

    @Inject(method = "handleRespawn", at=@At(value = "FIELD",target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;started:Z"))
    public void handleRespawn(ClientboundRespawnPacket packetIn, CallbackInfo ci){

        if(!(packetIn instanceof PrunePacket) && ! ((PrunePacket)packetIn).isPruning) {
            return;
        }
        ClientLevel oldLevel =this.level;

        Scoreboard scoreboard = this.level.getScoreboard();// 984
        boolean flag = packetIn.isDebug();// 985
        boolean flag1 = packetIn.isFlat();// 986
        ClientLevel.ClientLevelData clientworld$clientworldinfo = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), flag1);// 987
        this.levelData = clientworld$clientworldinfo;// 988
        this.level = new ClientLevel(((ClientPacketListener) (Object)this), clientworld$clientworldinfo, packetIn.getDimension(), packetIn.getDimensionType(), this.serverChunkRadius, this.minecraft::getProfiler, this.minecraft.levelRenderer, flag, packetIn.getSeed());// 989
        this.level.setScoreboard(scoreboard);// 990
        this.minecraft.setLevel(this.level);// 991
        this.minecraft.setScreen(new ReceivingLevelScreen());// 992
        try {
            oldLevel.close();

        }catch (Exception ex){
            PruneAddonFTBChunks.LOGGER.error(ex);
        }
    }
}
