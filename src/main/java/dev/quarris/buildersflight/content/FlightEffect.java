package dev.quarris.buildersflight.content;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class FlightEffect extends Effect {

    public FlightEffect(EffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity entity, AttributeModifierManager attributeMapIn, int amplifier) {
        super.applyAttributesModifiersToEntity(entity, attributeMapIn, amplifier);
        if (entity instanceof PlayerEntity) {
            entity.setNoGravity(true);
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(LivingEntity entity, AttributeModifierManager attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entity, attributeMapIn, amplifier);
        if (entity instanceof PlayerEntity) {
            entity.setNoGravity(false);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(LivingEntity entity, int amplifier) {
        if (!(entity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) entity;
        player.fallDistance = 0;
    }
}
