package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.Spectator.SpecInfo;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * PMores command class.
 * 
 * @author LeonTG77
 */
public class PmoresCommand extends UHCCommand {

	public PmoresCommand() {
		super("pmores", "");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can send their ore count to their team.");
		}
		
		Player player = (Player) sender;
		
		Spectator spec = Spectator.getInstance();
		TeamManager teams = TeamManager.getInstance();
		
		Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
			throw new CommandException("You are not on a team.");
		}
		
		Map<Material, Integer> total = SpecInfo.getTotal(player);
		
		String ores = displayOres(total).trim();
		
		if (ores.isEmpty()) {
			throw new CommandException("You haven't mined any ores since last reload.");
		}
		
        teams.sendMessage(team, "§8[§9PMOres§8] §7" + player.getName() + " §8» " + ores);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
	
	private String displayOres(Map<Material, Integer> total) {
		StringBuilder builder = new StringBuilder();
		
		for (Material type : total.keySet()) {
			if (total.get(type) == 0) {
				continue;
			}
			
			builder.append("§8[§a" + total.get(type) + " " + type.name().toLowerCase().replace("_ore", "") + "§8]");
			builder.append(" ");
		}
		
		return builder.toString();
	}
}