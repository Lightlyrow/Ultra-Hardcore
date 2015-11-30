package com.leontg77.uhc.scenario.scenarios;

import java.util.HashSet;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Inventors scenario class
 * 
 * @author LeonTG77
 */
public class Inventors extends Scenario implements Listener {
	private HashSet<String> crafted = new HashSet<String>();

	public Inventors() {
		super("Inventors", "The first person to craft any item will be broadcasted in chat.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getRecipe().getResult();
		
		if (crafted.contains(item.getType().name() + item.getDurability())) {
			return;
		}
		
		crafted.add(item.getType().name() + item.getDurability());
		PlayerUtils.broadcast("§f§l[Inventors] §7§l" + player.getName() + ": §a§l" + item.getType().name().toLowerCase().replaceAll("_", " ") + (item.getDurability() > 0 ? ":" + item.getDurability() : "") + "!");
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.playSound(online.getLocation(), Sound.ITEM_PICKUP, 1, 2);
		}
	}
}