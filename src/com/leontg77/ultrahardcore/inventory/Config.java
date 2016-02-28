package com.leontg77.ultrahardcore.inventory;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

public class Config extends InvGUI implements Listener {
	private final Inventory inv = Bukkit.createInventory(null, 54, "» §7Game configuration");

	/**
	 * Get the config inventory.
	 * 
	 * @return The inventory.
	 */
	public Inventory get() {
		return inv;
	}

	/**
	 * Update the config inventory.
	 */
	public void update() {
		final FeatureManager manager = FeatureManager.getInstance();
		
		for (ToggleableFeature feature : manager.getToggleableFeatures()) {
			final int slot = feature.getInventorySlot();
			
			if (slot > 53 || slot < 0) {
				continue;
			}
			
			inv.setItem(slot, feature.getToggleItem());
		}
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		if (event.getCurrentItem() == null) {
			return;
		}
		
		final FeatureManager manager = FeatureManager.getInstance();

		final Inventory inv = event.getInventory();
		final ItemStack item = event.getCurrentItem();

		if (!inv.getTitle().equals(this.inv.getTitle())) {
			return;
		}

		event.setCancelled(true);

		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}

		// the substring is just getting the name since theres some things before and after the name
		final String name = item.getItemMeta().getDisplayName().substring(6, item.getItemMeta().getDisplayName().length() - 4);
		
		final Feature feature = manager.getFeature(name);
		
		if (feature == null || !(feature instanceof ToggleableFeature)) {
			return;
		}
		
		final ToggleableFeature toggle = (ToggleableFeature) feature;
		toggle.toggle();

		final ItemMeta meta = item.getItemMeta();
		meta.setLore(toggle.getToggleItem().getItemMeta().getLore());
		item.setItemMeta(meta);
		
		PlayerUtils.broadcast(Main.PREFIX + name + " has been " + (toggle.isEnabled() ? "enabled" : "disabled") + ".");
		
		InvGUI.getGameInfo().update();
	}
}