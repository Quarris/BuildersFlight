package dev.quarris.buildersflight.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.quarris.buildersflight.content.IFlighter;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingRenderer.class)
public class PlayerRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/matrix/MatrixStack;push()V",
            shift = At.Shift.AFTER))
    public void renderBobbing(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
        if (!(entityIn instanceof IFlighter) || !((IFlighter) entityIn).isFlying())
            return;

        matrixStackIn.translate(0, 1/16f * Math.sin(Math.toRadians(entityIn.ticksExisted) * 5), 0);
    }

}
