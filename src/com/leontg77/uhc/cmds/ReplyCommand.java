package com.leontg77.uhc.cmds;

import java.util.Date;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.user.User;
import com.leontg77.uhc.utils.DateUtils;

/**
 * Reply command class
 * 
 * @author LeonTG77
 */
public class ReplyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (args.length == 0) {
    		sender.sendMessage(ChatColor.RED + "Usage: /reply <message>");
        	return true;
        }
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = User.get(player);
	    	
			if (user.isMuted()) {
				TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
				Date date = new Date();
				
				if (user.getUnmuteTime() == -1 || user.getUnmuteTime() > date.getTime()) {
					sender.sendMessage(Main.PREFIX + "You have been muted for: �a" + user.getMutedReason());
					
					if (user.getUnmuteTime() < 0) {
						sender.sendMessage(Main.PREFIX + "Your mute is permanent.");
					} else {
						sender.sendMessage(Main.PREFIX + "Your mute expires in: �a" + DateUtils.formatDateDiff(user.getUnmuteTime()));
					}
					return true;
				} else {
					user.unmute();
				}
			}
		}

    	if (!Main.msg.containsKey(sender)) {
    		sender.sendMessage(ChatColor.RED + "You have no one to reply to.");
    		return true;
    	}
    	
        CommandSender target = Main.msg.get(sender);
        StringBuilder message = new StringBuilder();
               
        for (int i = 0; i < args.length; i++) {
        	message.append(args[i]).append(" ");
        }
        
        String msg = message.toString().trim();
               
        sender.sendMessage("�6me �8-> �6" + target.getName() + " �8� �f" + msg);
    	target.sendMessage("�6" + sender.getName() + " �8-> �6me �8� �f" + msg);
    	
    	Main.msg.put(target, sender);
		Main.msg.put(sender, target);
		return true;
    }
}