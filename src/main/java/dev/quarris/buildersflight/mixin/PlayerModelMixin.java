package dev.quarris.buildersflight.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends AgeableModel<T> implements IHasArm, IHasHead {

    @Shadow
    public ModelRenderer bipedHead;
    @Shadow
    public ModelRenderer bipedHeadwear;
    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;
    @Shadow
    public ModelRenderer bipedRightLeg;
    @Shadow
    public ModelRenderer bipedLeftLeg;
    @Shadow
    public ModelRenderer bipedBody;

    private float targetArmRotation;
    private float prevArmRotation;
    private float currentArmRotation;
    private float targetLegRotation;
    private float prevLegRotation;
    private float currentLegRotation;

    @Inject(method = "setRotationAngles", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPrimaryHand()Lnet/minecraft/util/HandSide;"))
    public void setFlightRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        // Rendering arms in first person calls this method with 0 ageInTicks
        // This removes the arm rotations whe viewing in 1st person
        if (ageInTicks == 0)
            return;

        if (!(entityIn.getPersistentData().contains("flight") && entityIn.getPersistentData().getBoolean("flight"))) {
            this.prevArmRotation = 0;
            this.currentArmRotation = 0;
            this.prevLegRotation = 0;
            this.currentLegRotation = 0;
            return;
        }
        resetAngles();
        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        Vector3d motion = entityIn.getMotion();

        double horMotion = motion.x * motion.z;

        // -0.11 - 0.21

        this.targetArmRotation = 70;
        this.targetLegRotation = 5;

        if (motion.y < -0.05) {
            this.targetArmRotation = 160;
        } else if (motion.y > 0.09) {
            this.targetArmRotation = 0;
            this.targetLegRotation = 0;
        } else {
            this.bipedRightArm.rotateAngleX = -(float) Math.toRadians(15);
            this.bipedLeftArm.rotateAngleX = -(float) Math.toRadians(15);
        }

        this.prevLegRotation = this.currentLegRotation;
        this.prevArmRotation = this.currentArmRotation;

        this.currentArmRotation = MathHelper.approach(this.currentArmRotation, this.targetArmRotation, (this.targetArmRotation - this.currentArmRotation) / 10);
        this.currentLegRotation = MathHelper.approach(this.currentLegRotation, this.targetLegRotation, (this.targetLegRotation - this.currentLegRotation) / 10);

        float lerpedArmRot = MathHelper.lerp(partialTicks, this.prevArmRotation, this.currentArmRotation);
        float lerpedLegRot = MathHelper.lerp(partialTicks, this.prevLegRotation, this.currentLegRotation);

        float armRads = (float) Math.toRadians(lerpedArmRot);
        this.bipedRightArm.rotateAngleZ = armRads;
        this.bipedLeftArm.rotateAngleZ = -armRads;

        float legRads = (float) Math.toRadians(lerpedLegRot);
        this.bipedRightLeg.rotateAngleZ = legRads;
        this.bipedLeftLeg.rotateAngleZ = -legRads;
        if (Math.abs(horMotion) < 0.00001) {
            //model.bipedBody.rotationPointZ = 0;
        } else {
            //model.bipedBody.rotationPointZ = horMotion > 0 ? -3 : 3;
        }

        animateIdleModel(this.bipedRightArm, this.bipedLeftArm, ageInTicks, 0.06F, 0.06F, 2f);
        animateIdleModel(this.bipedRightLeg, this.bipedLeftLeg, ageInTicks, 0.1F, 0.07F, 0.9f);
        if ((motion.y > -0.05) && (motion.y < 0.09)) {
        }
    }

    // Passively swings the models
    // Default speed = 0.09, strength = 0.05
    private static void animateIdleModel(ModelRenderer right, ModelRenderer left, float ageInTicks, float speed, float strength, float xMult) {
        right.rotateAngleZ += MathHelper.cos(ageInTicks * speed) * strength + strength;
        left.rotateAngleZ -= MathHelper.cos(ageInTicks * speed) * strength + strength;
        right.rotateAngleX += MathHelper.sin(ageInTicks * speed * xMult) * strength * xMult;
        left.rotateAngleX -= MathHelper.sin(ageInTicks * speed * xMult) * strength * xMult;
    }

    private void resetAngles() {
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

        this.bipedLeftArm.rotateAngleX = 0;
        this.bipedLeftArm.rotateAngleY = 0;
        this.bipedLeftArm.rotateAngleZ = 0;

        this.bipedRightArm.rotateAngleX = 0;
        this.bipedRightArm.rotateAngleY = 0;
        this.bipedRightArm.rotateAngleZ = 0;

        this.bipedBody.rotateAngleX = 0;
        this.bipedBody.rotateAngleY = 0;
        this.bipedBody.rotateAngleZ = 0;

        //model.bipedHead.rotateAngleX = 0;
        //model.bipedHead.rotateAngleY = 0;
        //model.bipedHead.rotateAngleZ = 0;

        this.bipedHeadwear.rotateAngleX = 0;
        this.bipedHeadwear.rotateAngleY = 0;
        this.bipedHeadwear.rotateAngleZ = 0;

        this.bipedLeftLeg.rotateAngleX = 0;
        this.bipedLeftLeg.rotateAngleY = 0;
        this.bipedLeftLeg.rotateAngleZ = 0;

        this.bipedRightLeg.rotateAngleX = 0;
        this.bipedRightLeg.rotateAngleY = 0;
        this.bipedRightLeg.rotateAngleZ = 0;
    }
}
