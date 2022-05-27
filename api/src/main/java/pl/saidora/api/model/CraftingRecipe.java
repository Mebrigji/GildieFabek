package pl.saidora.api.model;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface CraftingRecipe {

    Map<Integer, ItemStack> getMatrix();

    ItemStack getResult();

}
