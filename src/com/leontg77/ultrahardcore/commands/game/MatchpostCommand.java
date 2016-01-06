package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.GameUtils;

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
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		String teamSize = GameUtils.getTeamSize(false, false);
		Game game = Game.getInstance();
		
		if (teamSize.startsWith("No") || teamSize.startsWith("Open")) {
			throw new CommandException("There are no matches running.");
		}
		
		sender.sendMessage(Main.PREFIX + "Match post: §a" + game.getMatchPost());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}