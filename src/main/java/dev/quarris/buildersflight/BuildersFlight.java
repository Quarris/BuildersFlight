package dev.quarris.buildersflight;

import dev.quarris.buildersflight.content.CompoundBrewingRecipe;
import dev.quarris.buildersflight.network.PacketHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.INBT;
import net.minecraft.potion.Potions;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BuildersFlight.ID)
public class BuildersFlight {

    public static final String ID = "buildersflight";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final boolean isDemo = true;

    public BuildersFlight() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        Registry.init();
        PacketHandler.init();
    }

    public static ResourceLocation res(String res) {
        return new ResourceLocation(ID, res);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        BrewingRecipeRegistry.addRecipe(new CompoundBrewingRecipe(Potions.AWKWARD, Ingredient.fromItems(Registry.SHULKER_HEART_ITEM.get()), Registry.LEVITATION_POTION.get()));

        BrewingRecipeRegistry.addRecipe(new CompoundBrewingRecipe(Potions.SLOW_FALLING, Ingredient.fromItems(Registry.SHULKER_HEART_ITEM.get()), Registry.FLIGHT_POTION.get()));
    }
}
