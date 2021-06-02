package dev.quarris.buildersflight.mixin;

import com.mojang.authlib.GameProfile;
import dev.quarris.buildersflight.Registry;
import dev.quarris.buildersflight.content.IFlighter;
import dev.quarris.buildersflight.network.FlightStatePacket;
import dev.quarris.buildersflight.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.MovementInput;

import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IFlighter {

    private static final double JUMP_STRENGTH = 0.2;
    private static final double JUMP_THRESHOLD = 0.06;
    private static final double DESCEND_SPEED = 0.08;
    private static final double DESCEND_FRICTION = 0.6;
    private static final double ASCEND_FRICTION = 0.9;

    @Shadow
    public MovementInput movementInput;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = {"livingTick"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;isRidingHorse()Z")
    )
    public void updateFlightEffectMotion(CallbackInfo ci) {
        if (this.getActivePotionEffect(Registry.FLIGHT.get()) == null) {
            if (this.isFlying()) {
                this.setFlying(false);
                this.syncFlightStateToServer();
            }
            return;
        }

        if (!this.isFlying()) {
            if (this.movementInput.jump) {
                this.setFlying(true);
                this.syncFlightStateToServer();
            }
        } else if (this.isOnGround()) {
            this.setFlying(false);
            this.syncFlightStateToServer();
        }

        if (this.isFlying()) {
            if (this.movementInput.jump && Math.abs(this.getMotion().y) < JUMP_THRESHOLD) {
                this.setMotion(this.getMotion().add(0, JUMP_STRENGTH, 0));
            }
            if (this.movementInput.sneaking) {
                this.setMotion(this.getMotion().add(0, -DESCEND_SPEED, 0));
                this.movementInput.sneaking = false;
            }
            if (this.getMotion().y < 0) {
                this.setMotion(this.getMotion().mul(1, DESCEND_FRICTION, 1));
            } else {
                this.setMotion(this.getMotion().mul(1, ASCEND_FRICTION, 1));
            }
        }
    }

    private void syncFlightStateToServer() {
        PacketHandler.sendToServer(new FlightStatePacket(this.getUniqueID(), this.isFlying()));
    }
}
