package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Matchpost command class.
 * 
 * @author LeonTG77
 */
public class MatchpostCommand extends UHCCommand {	

	public MatchpostCommand() {
		super("matchpost", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		final String teamSize = game.getAdvancedTeamSize(false, false).toLowerCase();
		
		if (teamSize.startsWith("no") || teamSize.startsWith("open")) {
			throw new CommandException("There are no matches running.");
		}
		
		sender.sendMessage(Main.PREFIX + "Match post: §a" + game.getMatchPost());
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
}