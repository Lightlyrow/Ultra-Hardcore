package com.leontg77.uhc.scenario.scenarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Assassins scenario class.
 * 
 * @author audicymc
 */
public class Assassins extends Scenario implements Listener, CommandExecutor {
	private static HashMap<String, String> assassins = new HashMap<String, String>();
	public static final String PREFIX = "§4[§6Assassins§4] §c";

	public Assassins() {
		super("Assassins", "Each player has a target that they must kill. Killing anyone that is not your target or assassin will result in no items dropping. When your target dies, you get their target.");
		Bukkit.getPluginCommand("target").setExecutor(this);
	}

	@Override
	public void onDisable() {
		assassins.clear();
	}
	
	@Override
	public void onEnable() {
		PlayerUtils.broadcast(PREFIX + "Assigning targets...");
		
		ArrayList<Player> players = new ArrayList<Player>(PlayerUtils.getPlayers());
		Collections.shuffle(players);

		for (int i = 0; i < players.size(); i++) {
			Player assassin = players.get(i);
			Player target = players.get(i < players.size() - 1 ? i + 1 : 0);

			setTarget(assassin.getName(), target.getName());
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();

		if (!assassins.containsKey(player.getName())) {
			return;
		}
		
		String assassin = getAssassin(player.getName());
		String target = getTarget(player.getName());

		if (killer != null && !killer.getName().equals(assassin) && !killer.getName().equals(target)) {
			event.getDrops().clear();
		}

		setTarget(assassin, target);
		assassins.remove(player.getName());
		
		PlayerUtils.broadcast(PREFIX + "§6" + player.getName() + " §cwas eliminated!");
		event.setDeathMessage(null);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String target = getAssassin(player.getName());

		if (target == null) {
			return;
		}
		
		Player targetP = Bukkit.getPlayer(target);

		if (targetP == null) {
			return;
		}
		
		targetP.setCompassTarget(player.getLocation());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can have targets.");
			return true;
		}

		Player player = (Player) sender;

		if (!isEnabled()) {
			player.sendMessage(PREFIX + "Assassins is not enabled.");
			return true;
		}

		player.sendMessage(PREFIX + "Target: §6" + getTarget(player.getName()));
		return true;
	}

	/**
	 * Get the assassins map
	 * 
	 * @return the Assassins map
	 */
	public static Map<String, String> getAssassins() {
		return assassins;
	}

	/**
	 * Get the assassin that has the target.
	 * 
	 * @param target the target.
	 * @return the assassin.
	 */
	private String getAssassin(String target) {
		for (Entry<String, String> e : assassins.entrySet()) {
			if (e.getValue().equalsIgnoreCase(target)) {
				return e.getKey();
			}
		}
		
		return null;
	}

	/**
	 * Get the target for an assassin
	 * 
	 * @param assassin the assassin.
	 * @return the target of the assassin
	 */
	private String getTarget(String assassin) {
		for (Entry<String, String> entry : assassins.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(assassin)) {
				return entry.getValue();
			}
		}
		
		return null;
	}

	/**
	 * Set a new target for the assassin.
	 * 
	 * @param assassin the assassin.
	 * @param target the new target.
	 */
	private void setTarget(final String assassin, final String target) {
		assassins.put(assassin, target);

		new BukkitRunnable() {
			public void run() {
				Player player = Bukkit.getPlayer(assassin);
				
				if (player != null) {
					player.sendMessage(PREFIX + "New Target: §6" + target);
				}
			}
		}.runTaskLater(Main.plugin, 1L);
	}
}