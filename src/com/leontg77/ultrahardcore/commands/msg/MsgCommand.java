package com.leontg77.ultrahardcore.commands.msg;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	public static Map<String, String> msg = new HashMap<String, String>();
	
	public MsgCommand() {
		super("msg", "<player> <message>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can message other players.");
			return true;
		}
		
		Player player = (Player) sender;
		
    	if (args.length < 2) {
        	return false;
        }
		
    	if (isMuted(player)) {
    		User user = User.get(player);
    		
    		player.sendMessage(Main.PREFIX + "You have been muted for: §a" + user.getMutedReason());
			
			if (user.getMuteExpiration() == null) {
				player.sendMessage(Main.PREFIX + "Your mute is permanent.");
			} else {
				player.sendMessage(Main.PREFIX + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getMuteExpiration().getTime()));
			}
			return true;
    	}
    	
    	Player target = Bukkit.getPlayer(args[0]);
               
        if (target == null) {
        	throw new CommandException("'" + args[0] + "' is not online.");
        }
        
		User user = User.get(target);
		
		if (user.isIgnoring(player)) {
			throw new CommandException("'" + player.getName() + "' have you ignored.");
		}
		
    	if (isMuted(target)) {
    		player.sendMessage(ChatColor.RED + "'" + target.getName() + "' is muted and won't be able to respond.");
    	}
        
        String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
               
        player.sendMessage("§8[§a§ome §8-> §a§o" + target.getName() + "§8] §7" + message);
    	target.sendMessage("§8[§a§o" + player.getName() + " §8-> §a§ome§8] §7" + message);
    	
    	msg.put(target.getName(), player.getName());
		msg.put(player.getName(), target.getName());
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
	protected static boolean isMuted(Player player) {
		User user = User.get(player);
    	
		if (!user.isMuted()) {
			return false;
		}

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		
		// if the mute isnt permanent (perm == -1) and their mute time experied, return false and unmute.
		if (user.getMuteExpiration() != null && user.getMuteExpiration().getTime() < date.getTime()) {
			user.unmute();
			return false;
		} 
		
		return true;
	}
}