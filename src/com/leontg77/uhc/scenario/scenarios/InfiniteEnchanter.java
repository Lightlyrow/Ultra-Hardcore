package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * InfinieEnchanter scenario class
 * 
 * @author LeonTG77
 */
public class InfiniteEnchanter extends Scenario {
	
	public InfiniteEnchanter() {
		super("InfiniteEnchanter", "Everyone is given 128 enchantment tables, anvils and bookshelves.");
	}

	@Override
	public void onDisable() {
		for (Player online : PlayerUtils.getPlayers()) {
			online.getInventory().remove(Material.ENCHANTMENT_TABLE);
			online.getInventory().remove(Material.BOOKSHELF);
			online.getInventory().remove(Material.ANVIL);
			online.setLevel(0);
		}
	}

	@Override
	public void onEnable() {
		for (Player online : PlayerUtils.getPlayers()) {
			online.getInventory().addItem(new ItemStack (Material.ENCHANTMENT_TABLE, 128));
			online.getInventory().addItem(new ItemStack (Material.BOOKSHELF, 128));
			online.getInventory().addItem(new ItemStack (Material.ANVIL, 128));
			online.setLevel(23000);
		}
	}
}