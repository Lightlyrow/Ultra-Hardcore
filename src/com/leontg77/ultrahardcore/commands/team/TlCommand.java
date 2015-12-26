package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.GameUtils;

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
			throw new CommandException("Only players can display their location to their team.");
		}
		
		Player player = (Player) sender;

		TeamManager teams = TeamManager.getInstance(); 
		Spectator spec = Spectator.getInstance();
		
		Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
			throw new CommandException("You are not on a team.");
		} 
		
		Location loc = player.getLocation();
		
        teams.sendMessage(team, "§8[§9TeamLoc§8] §7" + player.getName() + " §8» §fx: " + loc.getBlockX() + ", y: " + loc.getBlockY() + ", z: " + loc.getBlockZ() + " (" + environment(loc.getWorld()) + ")");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}

	/**
	 * Get the String version of the given world's environment type.
	 * 
	 * @param world The world checking.
	 * @return The string version of the type.
	 */
	private String environment(World world) {
		if (!GameUtils.getGameWorlds().contains(world)) {
			return "Unknown";
		}
		
		switch (world.getEnvironment()) {
		case NORMAL:
			return "Overworld";
		case NETHER:
			return "Nether";
		case THE_END:
			return "The End";
		default:
			return "Unknown";
		}
	}
}