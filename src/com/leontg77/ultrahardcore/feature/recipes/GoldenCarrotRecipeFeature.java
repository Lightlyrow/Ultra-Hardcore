package com.leontg77.ultrahardcore.feature.recipes;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Glistering Melon Recipe feature class.
 * 
 * @author LeonTG77
 */
public class GoldenCarrotRecipeFeature extends Feature implements Listener {

	public GoldenCarrotRecipeFeature() {
		super("Glistering Melon Recipe", "Makes glistering melons require gold ingots rather than nuggets to be crafted.");
		
		// register the new recipe
		final ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.GOLDEN_CARROT, 1))
			.shape("AAA", "ABA", "AAA")
			.setIngredient('A', Material.GOLD_INGOT)
			.setIngredient('B', Material.CARROT_ITEM);

		Bukkit.addRecipe(recipe);	
	}


    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        final Recipe recipe = event.getRecipe();

        if (recipe.getResult().getType() != Material.GOLDEN_CARROT) {
        	return;
        }

        if (hasRecipeGotMaterial(recipe, Material.GOLD_NUGGET)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
    
    /**
     * Check if the recipe has the given material in it
     *
     * @param recipe the recipe to check
     * @param type the material to look for
     * @return true if found, false if not
     */
    private boolean hasRecipeGotMaterial(Recipe recipe, Material type) {
        Collection<ItemStack> ingredients = null;

        if (recipe instanceof ShapedRecipe) {
            ingredients = ((ShapedRecipe) recipe).getIngredientMap().values();
        } else if(recipe instanceof ShapelessRecipe) {
            ingredients = ((ShapelessRecipe) recipe).getIngredientList();
        }

        if (null == ingredients) {
        	return false;
        }

        for (ItemStack stack : ingredients) {
            if (stack.getType() == type) {
            	return true;
            }
        }

        return false;
    }
}