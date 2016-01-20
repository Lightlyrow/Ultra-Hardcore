package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Back command class.
 * 
 * @author LeonTG77
 */
public class BackCommand extends UHCCommand {

	public BackCommand() {
		super("back", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can teleport to their last location.");
		}

		Spectator spec = Spectator.getInstance();
		
		Player player = (Player) sender;
		User user = User.get(player);
		
		if (!spec.isSpectating(player)) {
			throw new CommandException("You can only do this while spectating.");
		}

		player.sendMessage(Main.PREFIX + "Returning to your last location.");
		player.teleport(user.getLastLocation());
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
}