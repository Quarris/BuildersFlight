package dev.quarris.buildersflight.mixin;

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

    @Shadow
    protected abstract HandSide getMainHand(T entityIn);

    @Shadow
    protected abstract ModelRenderer getArmForSide(HandSide side);

    @Inject(method = "setRotationAngles", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPrimaryHand()Lnet/minecraft/util/HandSide;"))
    public void setFlightRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
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

        double armRot = 25;
        double legRot = 5;
        this.targetArmRotation = 20;
        this.targetLegRotation = 5;

        if (motion.y < -0.05) {
            this.targetArmRotation = 160;
        } else if (motion.y > 0.09) {
            this.targetArmRotation = 0;
            this.targetLegRotation = 0;
        }

        this.prevLegRotation = this.currentLegRotation;
        this.prevArmRotation = this.currentArmRotation;

        this.currentArmRotation = MathHelper.approach(this.currentArmRotation, this.targetArmRotation, (this.targetArmRotation - this.currentArmRotation) / 10);
        this.currentLegRotation = MathHelper.approach(this.currentLegRotation, this.targetLegRotation, (this.targetLegRotation - this.currentLegRotation) / 10);

        float lerpedArmRot = MathHelper.lerp(partialTicks, this.prevArmRotation, this.currentArmRotation);
        float lerpedLegRot = MathHelper.lerp(partialTicks, this.prevLegRotation, this.currentLegRotation);
            /*
            // If the player is moving vertically
            if (Math.abs(motion.y) > 0.0001) {
                if (motion.y < 0) { // If we are moving downwards
                    armRot += Math.min(130, MathHelper.lerp(motion.y / -0.11, 0, 130));
                } else { // If we are moving upwards
                    armRot -= Math.min(25, MathHelper.lerp(motion.y / 0.19, 0, 25));
                    legRot -= Math.min(3, MathHelper.lerp(motion.y / 0.19, 0, 3));
                }
            }
             */

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


        animateIdleModel(this.bipedRightArm, this.bipedLeftArm, ageInTicks, 0.06F, 0.08F, 0.7f);
        animateIdleModel(this.bipedRightLeg, this.bipedLeftLeg, ageInTicks, 0.1F, 0.07F, 0.9f);
    }

    private void animateArmSwing(T entity) {
        if (!(this.swingProgress <= 0.0F)) {
            HandSide handside = this.getMainHand(entity);
            ModelRenderer modelrenderer = this.getArmForSide(handside);
            float f = this.swingProgress;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                this.bipedBody.rotateAngleY *= -1.0F;
            }

            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f = 1.0F - this.swingProgress;
            f = f * f;
            f = f * f;
            f = 1.0F - f;
            float f1 = MathHelper.sin(f * (float) Math.PI);
            float f2 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f1 * 1.2D + (double) f2));
            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
        }
    }

    // Passively swings the models
    // Default speed = 0.09, strength = 0.05
    private static void animateIdleModel(ModelRenderer right, ModelRenderer left, float ageInTicks, float speed, float strength, float xMult) {
        right.rotateAngleZ += MathHelper.cos(ageInTicks * speed) * strength + strength;
        left.rotateAngleZ -= MathHelper.cos(ageInTicks * speed) * strength + strength;
        right.rotateAngleX += MathHelper.sin(ageInTicks * speed * xMult) * strength;
        left.rotateAngleX -= MathHelper.sin(ageInTicks * speed * xMult) * strength;
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
