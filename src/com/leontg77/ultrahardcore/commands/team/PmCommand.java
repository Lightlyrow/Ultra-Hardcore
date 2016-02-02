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
		
		final Player player = (Player) sender;
		
		final Spectator spec = Spectator.getInstance();
		final TeamManager teams = TeamManager.getInstance();
		
		final Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
			throw new CommandException("You are not on a team.");
		} 
		
		if (args.length == 0) {
			return false;
		}
		
		final String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
        teams.sendMessage(team, "§4§lTeam §8» §6§o" + player.getName() + "§8§o: §f" + msg);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
}