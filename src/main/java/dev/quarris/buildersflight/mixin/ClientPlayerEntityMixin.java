package dev.quarris.buildersflight.mixin;

import com.mojang.authlib.GameProfile;
import dev.quarris.buildersflight.Registry;
import dev.quarris.buildersflight.network.FlightStatePacket;
import dev.quarris.buildersflight.network.PacketHandler;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.MovementInput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow
    public MovementInput movementInput;

    public boolean isFlying;
    private boolean isFlyingSet;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = {"livingTick"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isRidingHorse()Z")
    )
    public void updateFlightEffectMotion(CallbackInfo ci) {
        if (!this.isFlyingSet) {
            this.setFlying(this.getPersistentData().getBoolean("flight"));
            this.isFlyingSet = true;
        }
        if (this.getActivePotionEffect(Registry.FLIGHT.get()) == null) {
            if (this.isFlying) {
                this.setFlying(false);
                this.syncFlightStateToServer();
            }
            return;
        }

        if (!this.isFlying) {
            if (this.movementInput.jump) {
                this.setFlying(true);
                this.syncFlightStateToServer();
            }
        } else if (this.isOnGround()) {
            this.setFlying(false);
            this.syncFlightStateToServer();
        }

        if (this.isFlying) {
            if (this.movementInput.jump && Math.abs(this.getMotion().y) < 0.06) {
                this.setMotion(this.getMotion().add(0, 0.2, 0));
            }
            if (this.movementInput.sneaking) {
                this.setMotion(this.getMotion().add(0, -0.07, 0));
                this.movementInput.sneaking = false;
            }
            if (this.getMotion().y < 0) {
                this.setMotion(this.getMotion().mul(1, 0.6, 1));
            } else {
                this.setMotion(this.getMotion().mul(1, 0.9, 1));
            }
        }
    }

    private void syncFlightStateToServer() {
        PacketHandler.sendToServer(new FlightStatePacket(this.isFlying));
    }

    private void setFlying(boolean isFlying) {
        this.isFlying = isFlying;
        this.setNoGravity(isFlying);
    }
}
