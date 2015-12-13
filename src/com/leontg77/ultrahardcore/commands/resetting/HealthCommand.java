package com.leontg77.ultrahardcore.commands.resetting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Paranoia;
import com.leontg77.ultrahardcore.scenario.scenarios.TeamHealth;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Health command class.
 * 
 * @author LeonTG77
 */
public class HealthCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ScenarioManager scen = ScenarioManager.getInstance();
		
		if (scen.getScenario(Paranoia.class).isEnabled() || scen.getScenario(TeamHealth.class).isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Oops, /h is disabled in this scenario.");
			return true;
		}
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can view their own health.");
				return true;
			}
			
			Player player = (Player) sender;
			
			double health = player.getHealth();
			double maxhealth = player.getMaxHealth();

			player.sendMessage(Main.PREFIX + "�7You are at �6" + NumberUtils.makePercent(health) + "%" + (maxhealth == 20 ? "" : " �7out of maximum �6" + NumberUtils.makePercent(maxhealth) + "%"));
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}
		
		double health = target.getHealth();
		double maxhealth = target.getMaxHealth();

		sender.sendMessage(Main.PREFIX + "�a" + target.getName() + " �7is at �6" + NumberUtils.makePercent(health) + "%" + (maxhealth == 20 ? "" : " �7out of maximum �6" + NumberUtils.makePercent(maxhealth) + "%"));
		return true;
	}
}