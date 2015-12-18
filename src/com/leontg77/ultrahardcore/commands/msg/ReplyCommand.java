package com.leontg77.ultrahardcore.commands.msg;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;

/**
 * Reply command class
 * 
 * @author LeonTG77
 */
public class ReplyCommand extends UHCCommand {

	public ReplyCommand() {
		super("reply", "<message>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
    	if (args.length == 0) {
        	return false;
        }
		
    	if (MsgCommand.isMuted(sender)) {
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

    	if (!MsgCommand.msg.containsKey(sender)) {
    		throw new CommandException("You have no one to reply to.");
    	}
    	
        String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
        CommandSender target = MsgCommand.msg.get(sender);

        sender.sendMessage("§8[§a§ome §8-> §a§o" + target.getName() + "§8] §7" + msg);
    	target.sendMessage("§8[§a§o" + sender.getName() + " §8-> §a§ome§8] §7" + msg);
    	
    	MsgCommand.msg.put(target, sender);
    	MsgCommand.msg.put(sender, target);
		return true;
    }

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null; // returning null actually returns a list of all online player names.
	}
}