package com.leontg77.uhc.scenario.types;

import java.util.ArrayList;
import java.util.HashMap;

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

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scoreboard.Teams;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * AssaultAndBattery scenario class
 * 
 * @author LeonTG77
 */
public class AssaultAndBattery extends Scenario implements Listener, CommandExecutor {
	private HashMap<String, Type> types = new HashMap<String, Type>();
	public static final String PREFIX = "§8[§aAB§8] §b";

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
		PlayerUtils.broadcast(PREFIX + "Setting classes!");
		
		Teams teams = Teams.getInstance();
		
		for (Team team : teams.getTeamsWithPlayers()) {
			if (team.getSize() < 2) {
				continue;
			}
				
			ArrayList<String> entry = new ArrayList<String>(team.getEntries());
			
			types.put(entry.get(0), Type.ASSAULT);
			types.put(entry.get(1), Type.BATTERY);
			
			teams.sendMessage(team, PREFIX + "Assaulter: §e" + entry.get(0));
			teams.sendMessage(team, PREFIX + "Battery: §e" + entry.get(1));
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		types.remove(player.getName());
		
		Teams teams = Teams.getInstance();
		Team team = teams.getTeam(player);
		
		if (team == null) {
			return;
		}
		
		teams.leaveTeam(player);
		
		for (String entry : team.getEntries()) {
			types.remove(entry);
		}
		
		teams.sendMessage(team, PREFIX + "§cYour teammate, " + player.getName() + ", has died! You can now damage with both melee and ranged attacks!");
		
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Player)) {
			return;
		}
		
		if (damager instanceof Player) {
			Player player = (Player) damager;
			Type type = types.get(player.getName());
			
			if (!type.equals(Type.BATTERY)) {
				return;
			}
			
			player.sendMessage(PREFIX + "You cannot do melee damage.");
			event.setCancelled(true);
			return;
		}

		if (damager instanceof Projectile) {
			Projectile proj = (Projectile) damager;
			
			if (!(proj.getShooter() instanceof Player)) {
				return;
			}
			
			Player player = (Player) proj.getShooter();
			Type type = types.get(player.getName());
			
			if (!type.equals(Type.ASSAULT)) {
				return;
			}
			
			player.sendMessage(PREFIX + "You cannot do projectile damage.");
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
			player.sendMessage(PREFIX + "AssaultAndBattery is not enabled.");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("class")) {
			if (!types.containsKey(player.getName())) {
				player.sendMessage(PREFIX + "You are the §eassaulter §band §ebattery§b! You can both melee and sword!");
				return true;
			}
			
			switch (types.get(player.getName())) {
			case ASSAULT:
				player.sendMessage(PREFIX + "You are the §eassaulter§b! You can only use your sword!");
				break;
			case BATTERY:
				player.sendMessage(PREFIX + "You are the §ebattery§b! You can only use your bow!");
				break;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("listclass")) {
			player.sendMessage(PREFIX + "§e====§bAssaulters§e====");
			
			for (String key : types.keySet()) {
				Type type = types.get(key);
				
				if (type != Type.ASSAULT) {
					continue;
				} 
				
				player.sendMessage(PREFIX + "- §e" + key);
			}
			
			player.sendMessage(PREFIX + "§e====§bBatteries§e====");
			
			for (String key : types.keySet()) {
				Type type = types.get(key);
				
				if (type != Type.BATTERY) {
					continue;
				} 
				
				player.sendMessage(PREFIX + "- §e" + key);
			}
			
			player.sendMessage(PREFIX + "§e====§bBoth§e====");
			
			for (OfflinePlayer wld : Bukkit.getWhitelistedPlayers()) {
				if (types.containsKey(wld.getName())) {
					continue;
				} 
				
				player.sendMessage(PREFIX + "- §e" + wld.getName());
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