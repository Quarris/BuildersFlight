package dev.quarris.buildersflight;

import dev.quarris.buildersflight.content.FlightEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class Registry {

    public static void init() {
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BuildersFlight.ID);
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, BuildersFlight.ID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, BuildersFlight.ID);

    public static final RegistryObject<Effect> FLIGHT = EFFECTS.register("flight", () -> new FlightEffect(EffectType.BENEFICIAL, 0xffa761e8));
    public static final RegistryObject<Potion> LEVITATION_POTION = potion("levitation", "levitation", Effects.LEVITATION, 0, 90 * 20);
    public static final RegistryObject<Potion> LONG_LEVITATION_POTION = potion("long_levitation", "levitation", Effects.LEVITATION, 0, 180 * 20);
    public static final RegistryObject<Potion> FLIGHT_POTION = potion("flight", "flight", FLIGHT::get, 0, 90 * 20);
    public static final RegistryObject<Potion> LONG_FLIGHT_POTION = potion("long_flight", "flight", FLIGHT::get, 0, 180 * 20);

    public static final RegistryObject<Item> SHULKER_HEART_ITEM = item("shulker_heart", () -> new Item(new Item.Properties().group(ItemGroup.BREWING)));

    private static RegistryObject<Item> item(String name, Supplier<Item> sup) {
        return ITEMS.register(name, sup);
    }

    private static RegistryObject<Potion> potion(String regName, String locName, Effect effect, int amplifier, int duration) {
        return POTIONS.register(regName, () -> new Potion(locName, new EffectInstance(effect, duration, amplifier)));
    }

    private static RegistryObject<Potion> potion(String regName, String locName, Supplier<Effect> effect, int amplifier, int duration) {
        return POTIONS.register(regName, () -> new Potion(locName, new EffectInstance(effect.get(), duration, amplifier)));
    }
}
