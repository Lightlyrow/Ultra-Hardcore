package com.leontg77.ultrahardcore.commands.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;

/**
 * Stats command class.
 * 
 * @author LeonTG77
 */
public class StatsCommand extends UHCCommand {
	
	public StatsCommand() {
		super("stats", "[player]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can view someones stats.");
		}
		
		Player player = (Player) sender;
		Game game = Game.getInstance();
		
		if (game.isRecordedRound()) {
			throw new CommandException("Stats are disabled in Recorded Rounds.");
		}

		Player target;
		
		if (args.length == 0) {
			target = player;
		} else {
			target = Bukkit.getPlayer(args[0]);
		}
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}
		
		User user = User.get(target);
		player.openInventory(InvGUI.getStats().get(user));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length != 1) {
			return new ArrayList<String>();
		}
		
		return null;
	}
}