package com.leontg77.ultrahardcore.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Paranoia;
import com.leontg77.ultrahardcore.scenario.scenarios.TeamHealth;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Health command class.
 * 
 * @author LeonTG77
 */
public class HealthCommand extends UHCCommand {

	public HealthCommand() {
		super("health", "[player]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		ScenarioManager scen = ScenarioManager.getInstance();
		
		if (scen.getScenario(Paranoia.class).isEnabled() || scen.getScenario(TeamHealth.class).isEnabled()) {
			throw new CommandException("/h is disabled in this scenario.");
		}
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can view their own health.");
			}
			
			Player player = (Player) sender;
			
			double health = player.getHealth();
			double maxhealth = player.getMaxHealth();

			player.sendMessage(Main.PREFIX + "§7You are at §6" + NumberUtils.makePercent(health) + "%" + (maxhealth == 20 ? "" : " §7out of maximum §6" + NumberUtils.makePercent(maxhealth) + "%"));
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}
		
		double health = target.getHealth();
		double maxhealth = target.getMaxHealth();

		sender.sendMessage(Main.PREFIX + "§a" + target.getName() + " §7is at §6" + NumberUtils.makePercent(health) + "%" + (maxhealth == 20 ? "" : " §7out of maximum §6" + NumberUtils.makePercent(maxhealth) + "%"));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return null;
		}

		return new ArrayList<String>();
	}
}