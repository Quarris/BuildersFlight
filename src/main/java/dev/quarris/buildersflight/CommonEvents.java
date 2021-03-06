package dev.quarris.buildersflight;

import dev.quarris.buildersflight.content.IFlighter;
import dev.quarris.buildersflight.network.FlightStatePacket;
import dev.quarris.buildersflight.network.PacketHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildersFlight.ID)
public class CommonEvents {

    @SubscribeEvent
    public static void addShulkerHeartDrop(LivingDropsEvent event) {
        if (event.getEntityLiving().getType() == EntityType.SHULKER) {
            LivingEntity entity = event.getEntityLiving();
            if (entity.getRNG().nextFloat() < 0.88) {
                event.getDrops().add(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(Registry.SHULKER_HEART_ITEM.get())));
            }
        }
    }

    /* Removed in favour of hard dropping the item on death
    @SubscribeEvent
    public static void addShulkerHeartDrop(LootTableLoadEvent event) {
        if (event.getName().equals(new ResourceLocation("entities/shulker"))) {
            LootPool pool = LootPool.builder()
                    .rolls(new ConstantRange(1))
                    .addEntry(TableLootEntry.builder(BuildersFlight.res("entities/shulker_heart_drop")))
                    .build();

            event.getTable().addPool(pool);
        }
    }
     */

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        boolean flying = false;
        if (event.getPlayer() instanceof IFlighter) {
            flying = ((IFlighter) event.getPlayer()).isFlying();
        }
        PacketHandler.sendToAllAround(new FlightStatePacket(event.getPlayer().getUniqueID(), flying), (ServerPlayerEntity) event.getPlayer(), 64);
    }
}
