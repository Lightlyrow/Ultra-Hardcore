package com.leontg77.ultrahardcore.commands.team;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * Pm command class.
 * 
 * @author LeonTG77
 */
public class PmCommand extends UHCCommand {

	public PmCommand() {
		super("pm", "<message>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can talk in team chat.");
		}
		
		Player player = (Player) sender;
		
		if (args.length == 0) {
			return false;
		}
		
		Spectator spec = Spectator.getInstance();
		TeamManager teams = TeamManager.getInstance();
		
		Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
			throw new CommandException("You are not on a team.");
		} 
		
		String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
        teams.sendMessage(team, "§8[§9TeamChat§8] §7" + player.getName() + " §8» §f" + msg);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
}