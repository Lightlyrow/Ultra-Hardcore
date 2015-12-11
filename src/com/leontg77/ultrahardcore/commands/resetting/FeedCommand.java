package com.leontg77.ultrahardcore.commands.resetting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Feed command class.
 * 
 * @author LeonTG77
 */
public class FeedCommand extends UHCCommand {

	public FeedCommand() {
		super("feed", "[player]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!sender.hasPermission("uhc.feed")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can feed themselves.");
			}
			
			Player player = (Player) sender;
			User user = User.get(player);
			
			player.sendMessage(Main.PREFIX + "You have been fed.");
			user.resetFood();
			return true;
		}
		
		if (!sender.hasPermission("uhc.feed.other")) {
			throw new CommandException("You cannot feed other players.");
		}
		
		if (args[0].equals("*")) {
			for (Player online : PlayerUtils.getPlayers()) {
				User user = User.get(online);
				user.resetFood();
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All players have been fed.");
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}
		
		User user = User.get(target);
		user.resetFood();

		sender.sendMessage(Main.PREFIX + "You fed §a" + target.getName() + "§7.");
		target.sendMessage(Main.PREFIX + "You have been fed by §a" + sender.getName() + "§7.");
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