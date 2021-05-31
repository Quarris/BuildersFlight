package dev.quarris.buildersflight;

import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;

public class PlayerModelThing {

    public static <T extends LivingEntity> void setFlightRotationAngles(AgeableModel<T> m, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entityIn.getPersistentData().contains("flight") && entityIn.getPersistentData().getBoolean("flight")) {
            BipedModel<T> model = (BipedModel<T>) m;
            //resetAngles(model, entityIn);
            Vector3d motion = entityIn.getMotion();

            double horMotion = motion.x * motion.z;

            // -0.11 - 0.21

            double armRot = Math.toRadians(25);
            double legRot = Math.toRadians(5);
            model.bipedRightArm.rotateAngleZ = (float) armRot;
            model.bipedLeftArm.rotateAngleZ = (float) -armRot;

            if (Math.abs(horMotion) < 0.00001) {
                //model.bipedBody.rotationPointZ = 0;
            } else {
                //model.bipedBody.rotationPointZ = horMotion > 0 ? -3 : 3;
            }

            model.bipedRightLeg.rotationPointZ = 0f;
            model.bipedRightLeg.rotateAngleZ = (float) legRot;
            model.bipedLeftLeg.rotationPointZ = 0f;
            model.bipedLeftLeg.rotateAngleZ = (float) -legRot;

            //animateIdleModel(model.bipedRightArm, model.bipedLeftArm, ageInTicks, 0.06F, 0.09F, 0.9f);
            //animateIdleModel(model.bipedRightLeg, model.bipedLeftLeg, ageInTicks, 0.1F, 0.07F, 0.9f);
        }
    }

    private static ModelRenderer getArmForSide(BipedModel model, HandSide side) {
        return side == HandSide.LEFT ? model.bipedLeftArm : model.bipedRightArm;
    }

    private static HandSide getMainHand(LivingEntity entityIn) {
        HandSide handside = entityIn.getPrimaryHand();
        return entityIn.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }
}
