package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.uhc.GameStartEvent;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Lootcrates scenario class
 * 
 * @author LeonTG77
 */
public class Lootcrates extends Scenario implements Listener {
	private BukkitRunnable task;

	public Lootcrates() {
		super("Lootcrates", "Every 10 minutes, players will be given a loot crate filled with goodies. There are two tiers, an Ender Chest being tier 2 and a normal chest tier 1.");
	}

	@Override
	public void onDisable() {
		task.cancel();
		task = null;
	}

	@Override
	public void onEnable() {
		if (State.isState(State.INGAME)) {
			on(new GameStartEvent());
		}
	}
	
	@EventHandler
	public void on(GameStartEvent event) {
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {
					if (SpecManager.getInstance().isSpectating(online)) {
						continue;
					}
					
					int i = new Random().nextInt(2);
					
					ItemStack item = new ItemStack(i == 0 ? Material.ENDER_CHEST : Material.CHEST);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName("§bTier " + (i == 0 ? "2" : "1") + " lootcrate");
					meta.setLore(Arrays.asList("§5§oRight click to open."));
					item.setItemMeta(meta);

					PlayerUtils.giveItem(online, item);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Lootcrates has been given out.");
			}
		};

		task.runTaskTimer(Main.plugin, 12000, 12000);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (item == null) {
			return;
		}

		if (!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
			return;
		}
		
		if (item.getType() == Material.CHEST && item.getItemMeta().getDisplayName().equals("§bTier 1 lootcrate")) {
			int i = new Random().nextInt(5);
			player.sendMessage(Main.PREFIX + "You rolled an §a" + (i + 1) + "§7.");
			event.setCancelled(true);
			
			switch (i) {
			case 0:
				player.setItemInHand(new ItemStack (Material.IRON_PICKAXE));
				break;
			case 1:
				player.setItemInHand(new ItemStack (Material.APPLE, 2));
				break;
			case 2:
				player.setItemInHand(new ItemStack (Material.COOKED_BEEF, 8));
				break;
			case 3:
				player.setItemInHand(new ItemStack (Material.CAKE));
				break;
			case 4:
				player.setItemInHand(new ItemStack (Material.RAW_FISH, 64, (short) 3));
				break;
			case 5:
				player.setItemInHand(new ItemStack (Material.DIRT, 32));
				break;
			}
			return;
		}

		if (item.getType() == Material.ENDER_CHEST && item.getItemMeta().getDisplayName().equals("§bTier 2 lootcrate")) {
			int i = new Random().nextInt(10);
			player.sendMessage(Main.PREFIX + "You rolled an §a" + (i + 1) + "§7.");
			event.setCancelled(true);
			
			switch (i) {
			case 0:
				player.setItemInHand(new ItemStack (Material.DIAMOND));
				break;
			case 1:
				player.setItemInHand(new ItemStack (Material.GOLD_INGOT, 3));
				break;
			case 2:
				player.setItemInHand(new ItemStack (Material.IRON_INGOT, 5));
				break;
			case 3:
				player.setItemInHand(new ItemStack (Material.BOW));
				break;
			case 4:
				player.setItemInHand(new ItemStack (Material.ENCHANTMENT_TABLE));
				break;
			case 5:
				player.setItemInHand(new ItemStack (Material.DIAMOND_SWORD));
				break;
			case 6:
				player.setItemInHand(new ItemStack (Material.DIAMOND_HELMET));
				break;
			case 7:
				player.setItemInHand(new ItemStack (Material.DIAMOND_BOOTS));
				break;
			case 8:
				player.setItemInHand(new ItemStack (Material.ARROW, 32));
				break;
			case 9:
				player.setItemInHand(new ItemStack (Material.TNT));
				break;
			case 10:
				player.setItemInHand(new ItemStack (Material.FLINT_AND_STEEL));
				break;
			}
		}
	}
}