package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * TimeBomb scenario class
 * 
 * @author LeonTG77
 */
public class Timebomb extends Scenario implements Listener {
	public static final String PREFIX = "§4Timebomb §8» §7";
	
	private final Main plugin;
	private final Game game;

	public Timebomb(Main plugin, Game game) {
		super("Timebomb", "After killing a player all of their items will appear in a double chest rather than dropping on the ground. You then have 30 seconds to loot what you want and get the hell away from it. This is because the chest explodes after the time is up.");
	
		this.plugin = plugin;
		this.game = game;
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		
		if (!game.getWorlds().contains(player.getWorld())) {
			return;
		}
		
		User user = User.get(player);
		
		final Location loc = player.getLocation().add(0, -1, 0);
		event.setKeepInventory(true);
		
		loc.getBlock().setType(Material.TRAPPED_CHEST);
		Chest chest = (Chest) loc.getBlock().getState();
		
		loc.add(0, 0, -1);
		loc.getBlock().setType(Material.TRAPPED_CHEST);
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			
			chest.getInventory().addItem(item);
		}
		
		for (ItemStack item : player.getInventory().getArmorContents()) {
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			
			chest.getInventory().addItem(item);
		}
		
		user.resetInventory();
		
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX.replaceAll("UHC", "Timebomb") + ChatColor.GREEN + player.getName() + "'s §7corpse has exploded!");
				loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 10, false, true);
				// Using actual lightning to kill the items.
				loc.getWorld().strikeLightning(loc);
			}
		}, 600);
		
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				loc.getWorld().strikeLightning(loc);
			}
		}, 620);
	}
}