package com.leontg77.ultrahardcore.commands.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;

/**
 * Top command class.
 * 
 * @author LeonTG77
 */
public class TopCommand extends UHCCommand {	

	public TopCommand() {
		super("top", "");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can open the top 10 inventory.");
		}
		
		Game game = Game.getInstance();
		
		if (game.isRecordedRound()) {
			throw new CommandException("Stats are disabled in Recorded Rounds.");
		}

		InvGUI gui = InvGUI.getInstance();
		Player player = (Player) sender;
		
		gui.openTopStats(player);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}