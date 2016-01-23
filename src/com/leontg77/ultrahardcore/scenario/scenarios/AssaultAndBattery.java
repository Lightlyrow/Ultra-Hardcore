package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * AssaultAndBattery scenario class
 * 
 * @author LeonTG77
 */
public class AssaultAndBattery extends Scenario implements Listener, CommandExecutor {
	private final Map<String, Type> types = new HashMap<String, Type>();
	
	private static final String PREFIX = "§b§lA&B §8» §7";

	public AssaultAndBattery() {
		super("AssaultAndBattery", "To2 Where one person can only do meelee damage to players, while the other one can only do ranged attacks. If a teammate dies, you can do both meelee and ranged attacks.");
		
		Bukkit.getPluginCommand("class").setExecutor(this);
		Bukkit.getPluginCommand("listclass").setExecutor(this);
	}

	@Override
	public void onDisable() {
		types.clear();
	}
	
	@Override
	public void onEnable() {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		on(new GameStartEvent());
	}
	
	@EventHandler
	public void on(GameStartEvent event) {
		final TeamManager teams = TeamManager.getInstance();
		
		PlayerUtils.broadcast(PREFIX + "Setting classes...");
		
		for (Team team : teams.getTeamsWithPlayers()) {
			if (team.getSize() < 2) {
				continue;
			}
				
			final List<String> entry = new ArrayList<String>(team.getEntries());
			
			types.put(entry.get(0), Type.ASSAULT);
			types.put(entry.get(1), Type.BATTERY);

			teams.sendMessage(team, PREFIX + "Assaulter (Melee): §e" + entry.get(0));
			teams.sendMessage(team, PREFIX + "Battery (Bow): §e" + entry.get(1));
		}
	}
	
	@EventHandler
	public void on(PlayerDeathEvent event) {
		Player player = event.getEntity();
		types.remove(player.getName());
		
		TeamManager teams = TeamManager.getInstance();
		Team team = teams.getTeam(player);
		
		if (team == null) {
			return;
		}
		
		teams.leaveTeam(player, false);
		
		for (String entry : team.getEntries()) {
			types.remove(entry);
		}
		
		teams.sendMessage(team, PREFIX + "Your teammate died, You can now damage with both melee and ranged attacks.");
		
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		final Entity damager = event.getDamager();
		final Entity entity = event.getEntity();
		
		if (!(entity instanceof Player)) {
			return;
		}
		
		if (damager instanceof Player) {
			final Player player = (Player) damager;
			final Type type = types.get(player.getName());
			
			if (!type.equals(Type.BATTERY)) {
				return;
			}
			
			player.sendMessage(ChatColor.RED + "You can't do melee damage.");
			event.setCancelled(true);
			return;
		}

		if (damager instanceof Projectile) {
			final Projectile proj = (Projectile) damager;
			
			if (!(proj.getShooter() instanceof Player)) {
				return;
			}
			
			final Player player = (Player) proj.getShooter();
			final Type type = types.get(player.getName());
			
			if (!type.equals(Type.ASSAULT)) {
				return;
			}
			
			player.sendMessage(ChatColor.RED + "You can't do projectile damage.");
			event.setCancelled(true);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use AssaultAndBattery commands.");
			return true;
		}
		
		Player player = (Player) sender;

		if (!isEnabled()) {
			player.sendMessage(PREFIX + "Assault & Battery is not enabled.");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("class")) {
			if (!types.containsKey(player.getName())) {
				player.sendMessage(PREFIX + "You are the §eassaulter §7and §ebattery§7! You can both melee and sword!");
				return true;
			}
			
			switch (types.get(player.getName())) {
			case ASSAULT:
				player.sendMessage(PREFIX + "You are the §eassaulter§7! You can only use your sword!");
				break;
			case BATTERY:
				player.sendMessage(PREFIX + "You are the §ebattery§7! You can only use your bow!");
				break;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("listclass")) {
			player.sendMessage(PREFIX + "§8====§7 Assaulters §8====");
			
			for (String key : types.keySet()) {
				Type type = types.get(key);
				
				if (type != Type.ASSAULT) {
					continue;
				} 
				
				player.sendMessage(PREFIX + "§8- §7" + key);
			}
			
			player.sendMessage(PREFIX + "§8====§7 Batteries §8====");
			
			for (String key : types.keySet()) {
				Type type = types.get(key);
				
				if (type != Type.BATTERY) {
					continue;
				} 
				
				player.sendMessage(PREFIX + "§8- §7" + key);
			}
			
			player.sendMessage(PREFIX + "§8====§7 Both §8====");
			
			for (OfflinePlayer wld : Bukkit.getWhitelistedPlayers()) {
				if (types.containsKey(wld.getName())) {
					continue;
				} 
				
				player.sendMessage(PREFIX + "§8- §7" + wld.getName());
			}
		}
		return true;
	}
	
	/**
	 * The assault and battery type enum.
	 * 
	 * @author LeonTG77
	 */
	public enum Type {
		ASSAULT, BATTERY;
	}
}