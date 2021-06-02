package dev.quarris.buildersflight.mixin;

import dev.quarris.buildersflight.content.IFlighter;
import dev.quarris.buildersflight.network.FlightTargetRotationPacket;
import dev.quarris.buildersflight.network.PacketHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IFlighter {

    @Shadow
    public abstract ITextComponent getDisplayName();

    private float targetArmRotation;
    private float prevArmRotation;
    private float currentArmRotation;
    private float targetLegRotation;
    private float prevLegRotation;
    private float currentLegRotation;
    public boolean isFlying;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tick()V", shift = At.Shift.AFTER))
    public void tick(CallbackInfo ci) {
        if (!this.isFlying) {
            this.prevArmRotation = 0;
            this.currentArmRotation = 0;
            this.prevLegRotation = 0;
            this.currentLegRotation = 0;
            return;
        }

        this.updateAnimation();

        this.prevLegRotation = this.currentLegRotation;
        this.prevArmRotation = this.currentArmRotation;

        this.currentArmRotation = MathHelper.approach(this.currentArmRotation, this.targetArmRotation, (this.targetArmRotation - this.currentArmRotation) / 6);
        this.currentLegRotation = MathHelper.approach(this.currentLegRotation, this.targetLegRotation, (this.targetLegRotation - this.currentLegRotation) / 10);
    }

    @Override
    public void updateAnimation() {
        Vector3d motion = this.getMotion();
        this.targetArmRotation = 70;
        this.targetLegRotation = 5;

        if (motion.y < -0.05) {
            this.targetArmRotation = 160;
        } else if (motion.y > 0.08) {
            this.targetArmRotation = 0;
            this.targetLegRotation = 0;
        }

        if (this.world.isRemote) {
            PacketHandler.sendToServer(new FlightTargetRotationPacket(this.getUniqueID(), this.targetArmRotation, this.targetLegRotation));
        }
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource cause, CallbackInfo ci) {
        this.setFlying(false);
    }

    @Inject(method = "writeAdditional", at = @At("TAIL"))
    public void writeAdditional(CompoundNBT compound, CallbackInfo ci) {
        compound.putBoolean("IsFlying", this.isFlying);
    }

    @Inject(method = "readAdditional", at = @At("TAIL"))
    public void readAdditional(CompoundNBT compound, CallbackInfo ci) {
        this.setFlying(compound.getBoolean("IsFlying"));
    }

    @Override
    public void setTargetRotations(float arm, float leg) {
        this.targetArmRotation = arm;
        this.targetLegRotation = leg;
    }

    @Override
    public void setFlying(boolean flying) {
        this.isFlying = flying;
        this.setNoGravity(isFlying);
    }

    @Override
    public boolean isFlying() {
        return this.isFlying;
    }

    @Override
    public float getCurrentArmRotation() {
        return this.currentArmRotation;
    }

    @Override
    public float getPrevArmRotation() {
        return this.prevArmRotation;
    }

    @Override
    public float getCurrentLegRotation() {
        return this.currentLegRotation;
    }

    @Override
    public float getPrevLegRotation() {
        return this.prevLegRotation;
    }
}
