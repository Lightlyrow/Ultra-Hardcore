package com.leontg77.ultrahardcore.commands.banning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Kick command class.
 * 
 * @author LeonTG77
 */
public class KickCommand extends UHCCommand {	

	public KickCommand() {
		super("kick", "<player> <reason>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}

		final String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

		if (args[0].equals("*")) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online.hasPermission("uhc.prelist")) {
					continue;
				}
				
		    	online.kickPlayer(message);
			}
			
	    	PlayerUtils.broadcast(Main.PREFIX + "All normal players has been kicked for �6" + message);
			return true;
		}

		if (args[0].equals("**")) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online.isOp()) {
					continue;
				}
				
		    	online.kickPlayer(message);
			}
			
	    	PlayerUtils.broadcast(Main.PREFIX + "All players has been kicked for �6" + message);
			return true;
		}
    	
    	Player target = Bukkit.getServer().getPlayer(args[0]);
		
    	if (target == null) {
    		throw new CommandException("'" + args[0] + "' is not online.");
		}
    	
    	PlayerUtils.broadcast(Main.PREFIX + "�6" + target.getName() + " �7has been kicked for �a" + message, "uhc.kick");
    	target.kickPlayer(message);
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			toReturn.add("*");
			toReturn.add("**");
			
			for (String name : allPlayers()) {
				toReturn.add(name);
			}
		}
		
		return toReturn;
	}
}