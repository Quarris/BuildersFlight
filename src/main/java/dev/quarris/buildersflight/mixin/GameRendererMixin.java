package dev.quarris.buildersflight.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    private Minecraft mc;


    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;applyBobbing(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", shift = At.Shift.AFTER))
    public void applyFlightBobbing(float partialTicks, long finishTimeNano, MatrixStack matrixStackIn, CallbackInfo ci) {
        if (this.mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON)
            return;

        if (!(this.mc.getRenderViewEntity() instanceof PlayerEntity))
            return;

        PlayerEntity playerentity = (PlayerEntity) this.mc.getRenderViewEntity();
        if (!(playerentity.getPersistentData().contains("flight") && playerentity.getPersistentData().getBoolean("flight")))
            return;

        float f1 = (float) Math.toRadians(playerentity.ticksExisted);
        float f2 = 1/16f;
        matrixStackIn.translate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.2F, MathHelper.cos(f1 * (float) Math.PI) * f2, 0.0D);
    }

}
