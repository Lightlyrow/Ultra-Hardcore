package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * TeamLoc command class.
 * 
 * @author LeonTG77
 */
public class TlCommand extends UHCCommand {

	public TlCommand() {
		super("tl", "");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can display their location to their location.");
		}
		
		Player player = (Player) sender;

		TeamManager teams = TeamManager.getInstance(); 
		Spectator spec = Spectator.getInstance();
		
		Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
			throw new CommandException("You are not on a team.");
		} 
		
		teams.sendMessage(team, "§9§lTeam §8» §7" + player.getName() + "'s coords: §fx:" + player.getLocation().getBlockX() + " y:" + player.getLocation().getBlockY() + " z:" + player.getLocation().getBlockZ() + " (" + player.getWorld().getEnvironment().name().replaceAll("_", " ").replaceAll("NORMAL", "overworld").toLowerCase().replaceAll("normal", "overworld") + ")");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}