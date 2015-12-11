package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * BenchBlitz scenario class
 * 
 * @author LeonTG77
 */
public class BenchBlitz extends Scenario implements Listener {
	private List<String> hasCrafted = new ArrayList<String>();
	
	public BenchBlitz() {
		super("BenchBlitz", "You can only craft one crafting table.");
	}
	
	@Override
	public void onDisable() {
		hasCrafted.clear();
	}

	@Override
	public void onEnable() {
		hasCrafted.clear();
	}
	
	@EventHandler
	public void on(CraftItemEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		// this cast is safe, fyi.
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		if (item.getType() != Material.WORKBENCH) {
			return;
		}
		
		if (hasCrafted.contains(player.getName())) {
			player.sendMessage(ChatColor.RED + "You are too weak to arrange wood in a square!");
			event.setCancelled(true);
			return;
		}

		player.sendMessage(ChatColor.GOLD + "That's your only bench! Don't lose it!");
		hasCrafted.add(player.getName());
		event.setCancelled(true);
		
		// the rest of the code is to make sure the only get 1.
		if (item != null) {
			item.setType(Material.AIR);
		}
		
		for (ItemStack content : player.getInventory().getContents()) {
			if (content == null) {
				continue;
			}
			
			if (content.getType() == Material.WORKBENCH) {
				content.setType(Material.AIR);
			}
		}
		
		player.getInventory().addItem(new ItemStack(Material.WORKBENCH));
	}
}