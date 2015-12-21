package com.leontg77.ultrahardcore.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

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
		
		StringBuilder message = new StringBuilder();
        
        for (int i = 0; i < args.length; i++) {
        	message.append(args[i]).append(" ");
        }
               
        String msg = message.toString().trim();
        
        teams.sendMessage(team, "§9§lTeam §8» §7" + player.getName() + ": §f" + msg);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
}