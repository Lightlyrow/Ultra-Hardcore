package com.leontg77.uhc.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * BestPvE scenario class
 * 
 * @author LeonTG77
 */
public class BestPvE extends Scenario implements Listener, CommandExecutor {
	private static HashSet<String> list = new HashSet<String>();
	private BukkitRunnable task;

	public BestPvE() {
		super("BestPvE", "Everyone starts on a list called bestpve list, if you take damage you are removed from the list. The only way to get back on the list is getting a kill, All players on the bestpve list gets 1 extra heart each 10 minutes.");
	
		Bukkit.getPluginCommand("pve").setExecutor(this);
		Bukkit.getPluginCommand("pvelist").setExecutor(this);
	}
	
	@Override
	public void onDisable() {
		list.clear();
		task.cancel();
		task = null;
	}
	
	@Override
	public void onEnable() {
		for (Player online : PlayerUtils.getPlayers()) {
			list.add(online.getName());
		}
		
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					if (!list.contains(online.getName())) {
						online.sendMessage(ChatColor.GREEN + "BestPvE players gained a heart!");
						continue;
					}

					online.sendMessage(ChatColor.GREEN + "You were rewarded for your PvE skills!");
					
					online.setMaxHealth(online.getMaxHealth() + 2);
					online.setHealth(online.getHealth() + 2);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 12000, 12000);
	}
	
	/**
	 * Get the Best PvE list.
	 * 
	 * @return The list.
	 */
	public static Set<String> getList() {
		return list;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player killer = event.getEntity().getKiller();
		
		if (killer == null) {
			return;
		}

		final Player player = killer;

		if (list.contains(player.getName())) {
			return;
		}
		
		PlayerUtils.broadcast(ChatColor.GREEN + player.getName() + " got a kill! He is back on the Best PvE List!");
		
		new BukkitRunnable() {
			public void run() {
				list.add(player.getName());
			}
		}.runTaskLater(Main.plugin, 40);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (!list.contains(player.getName())) {
			return;
		}
		
		if (Timers.timeSeconds < 20) {
			return;
		}
		
		PlayerUtils.broadcast(ChatColor.RED + player.getName() + " took damage!");
		list.remove(player.getName());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
            sender.sendMessage(ChatColor.RED + "Best PvE game is not in progress!");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("pvelist")) {
			sender.sendMessage(ChatColor.GREEN + "Best PvE players:");
			
            for (String player : list) {
                sender.sendMessage(ChatColor.YELLOW + player);
            }
		}
		
		if (cmd.getName().equalsIgnoreCase("pve")) {
			if (!sender.hasPermission("uhc.bestpve")) {
				sender.sendMessage(Main.NO_PERM_MSG);
				return true;
			}
			
			if (args.length < 2) {
				sender.sendMessage(ChatColor.GREEN + "Help for BestPvE:");
				sender.sendMessage("§7- §f/pve add <player> - §oAdd a player to the list.");
				sender.sendMessage("§7- §f/pve remove <player> - §oRemove a player from the list.");
				return true;
			}
			
			String player = args[1];
			
			if (args[0].equalsIgnoreCase("add")) {
				if (list.contains(player)) {
	                sender.sendMessage(ChatColor.RED + player + " is already on the list!");
					return true;
				}

                sender.sendMessage(ChatColor.GOLD + player + " was added to the list!");
				list.add(player);
				return true;
			} 
				
			if (args[0].equalsIgnoreCase("remove")) {
				if (!list.contains(player)) {
	                sender.sendMessage(ChatColor.RED + player + " is not present on the list!");
					return true;
				}
				
                sender.sendMessage(ChatColor.GOLD + player + " was removed from the list!");
				list.remove(player);
				return true;
			}

			sender.sendMessage(ChatColor.GREEN + "Help for BestPvE:");
			sender.sendMessage("§7- §f/pve add <player> - §oAdd a player to the list.");
			sender.sendMessage("§7- §f/pve remove <player> - §oRemove a player from the list.");
		}
		return true;
	}
}