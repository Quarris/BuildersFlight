package dev.quarris.buildersflight.content;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

/**
 * A brewing recipe which automatically adds Splash and Lingering variants
 */
public class CompoundBrewingRecipe implements IBrewingRecipe {

    public final Potion input;
    public final Ingredient ingredient;
    public final Potion output;

    public CompoundBrewingRecipe(Potion input, Ingredient ingredient, Potion output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean isInput(ItemStack input) {
        Potion inputPotion = PotionUtils.getPotionFromItem(input);
        return inputPotion.equals(this.input);
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return this.isReagant(ingredient);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!this.isInput(input) || !this.isIngredient(ingredient))
            return ItemStack.EMPTY;

        if (this.isReagant(ingredient)) {
            if (isInputSplash(input)) {
                return PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), this.output);
            }
            if (isInputLingering(input)) {
                return PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), this.output);
            }
            return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), this.output);
        }

        return ItemStack.EMPTY;
    }

    private static boolean isInputSplash(ItemStack input) {
        return input.getItem() == Items.SPLASH_POTION;
    }

    private static boolean isInputLingering(ItemStack input) {
        return input.getItem() == Items.LINGERING_POTION;
    }

    private boolean isReagant(ItemStack ingredient) {
        return this.ingredient.test(ingredient);
    }
}
