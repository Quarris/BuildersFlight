package dev.quarris.buildersflight.mixin;

import com.mojang.authlib.GameProfile;
import dev.quarris.buildersflight.content.IFlighter;
import dev.quarris.buildersflight.network.FlightStatePacket;
import dev.quarris.buildersflight.network.PacketHandler;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemoteClientPlayerEntity.class)
public abstract class RemoteClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IFlighter {

    public RemoteClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void updateInitialFlightState(CallbackInfo ci) {
        if (this.firstUpdate) {
            PacketHandler.sendToServer(new FlightStatePacket(this.getUniqueID()));
        }
    }

    @Override
    public void updateAnimation() {

    }
}
