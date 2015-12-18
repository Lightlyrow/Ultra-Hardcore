package com.leontg77.ultrahardcore.commands.msg;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;

/**
 * Msg command class
 * 
 * @author LeonTG77
 */
public class MsgCommand extends UHCCommand {
	
	public MsgCommand() {
		super("msg", "<player> <message>");
	}

	public static HashMap<CommandSender, CommandSender> msg = new HashMap<CommandSender, CommandSender>();

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
    	if (args.length < 2) {
        	return false;
        }
		
    	if (isMuted(sender)) {
    		// this cast is safe, isMuted returns false if its not a player.
    		Player player = (Player) sender;
    		User user = User.get(player);
    		
			sender.sendMessage(Main.PREFIX + "You have been muted for: §a" + user.getMutedReason());
			
			if (user.getUnmuteTime() < 0) {
				sender.sendMessage(Main.PREFIX + "Your mute is permanent.");
			} else {
				sender.sendMessage(Main.PREFIX + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getUnmuteTime()));
			}
			return true;
    	}
    	
    	Player target = Bukkit.getPlayer(args[0]);
               
        if (target == null) {
        	throw new CommandException("'" + args[0] + "' is not online.");
        }
        
        String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
               
        sender.sendMessage("§8[§a§ome §8-> §a§o" + target.getName() + "§8] §7" + message);
    	target.sendMessage("§8[§a§o" + sender.getName() + " §8-> §a§ome§8] §7" + message);
    	
    	msg.put(target, sender);
		msg.put(sender, target);
		return true;
    }

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null; // returning null actually returns a list of all online player names.
	}
	
	/**
	 * Check if the given sender is muted.
	 * <p>
	 * This will only return true if its a player, they're muted and their mute hasn't experied.
	 * 
	 * @param sender The sender checking for.
	 * @return True if they are, false otherwise.
	 */
	protected static boolean isMuted(CommandSender sender) {
		if (!(sender instanceof Player)) {
			return false;
		}
		
		Player player = (Player) sender;
		User user = User.get(player);
    	
		if (!user.isMuted()) {
			return false;
		}

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		
		// if the mute isnt permanent (perm == -1) and their mute time experied, return false and unmute.
		if (user.getUnmuteTime() != -1 && user.getUnmuteTime() < date.getTime()) {
			user.unmute();
			return false;
		} 
		
		return true;
	}
}