package com.leontg77.ultrahardcore.feature;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ToggleableFeature extends Feature {
	private boolean enabled = false;
	
	private ItemStack toggleItem;
	private int invSlot;
	
	public ToggleableFeature(String name, String description, ItemStack toggleItem, int invSlot) {
		super(name, description);
		
		this.toggleItem = toggleItem;
		this.invSlot = invSlot;
	}
	
	public ItemStack getToggleItem() {
		final ItemStack item = toggleItem;
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName((isEnabled() ? ChatColor.GREEN : ChatColor.RED) + getName());
		meta.setLore(Arrays.asList(" ", "§8» §7" + getDescription(), " "));
		item.setItemMeta(meta);
		return item;
	}
	
	public int getInventorySlot() {
		return invSlot;
	}

	/**
	 * Called when the feature disables.
	 */
	public void onDisable() {}
	
	/**
	 * Called when the feature enables.
	 */
	public void onEnable() {}
	
	/**
	 * Enable the feature.
	 * 
	 * @return True if successful, false otherwise.
	 */
	public boolean enable() {
		if (isEnabled()) {
			return false;
		}
		
		settings.getConfig().set("feature." + getName().toLowerCase() + ".enabled", true);
		settings.saveConfig();
		
		enabled = true;
		onEnable();
		return true;
	}
	
	/**
	 * Disable the feature.
	 * 
	 * @return True if successful, false otherwise.
	 */
	public boolean disable() {
		if (!isEnabled()) {
			return false;
		}
		
		settings.getConfig().set("feature." + getName().toLowerCase() + ".enabled", false);
		settings.saveConfig();
		
		enabled = false;
		onDisable();
		return true;
	}
	
	/**
	 * Toggle the feature.
	 * 
	 * @return True if successful, false otherwise.
	 */
	public boolean toggle() {
		return isEnabled() ? disable() : enable();
	}
	
	/**
	 * Check if the scenario is enabled
	 * 
	 * @return True if enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}
}