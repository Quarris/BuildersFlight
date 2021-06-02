package dev.quarris.buildersflight.content;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IFlighter {

    void setFlying(boolean flying);

    boolean isFlying();

    void setTargetRotations(float arm, float leg);

    float getCurrentArmRotation();

    float getPrevArmRotation();

    float getCurrentLegRotation();

    float getPrevLegRotation();

    void updateAnimation();

    @OnlyIn(Dist.CLIENT)
    default float getLerpedArmRotation(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.getPrevArmRotation(), this.getCurrentArmRotation());
    }

    @OnlyIn(Dist.CLIENT)
    default float getLerpedLegRotation(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.getPrevLegRotation(), this.getCurrentLegRotation());
    }

}
