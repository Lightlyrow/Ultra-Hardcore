package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.utils.PlayerUtils;

public class StaffChatCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ac")) {
			if (sender.hasPermission("uhc.staff")) {
				if (args.length == 0) {
		    		sender.sendMessage(ChatColor.RED + "Usage: /ac <message>");
		        	return true;
		        } 
		        
		    	StringBuilder message = new StringBuilder("");
				
				for (int i = 0; i < args.length; i++) {
					message.append(args[i]).append(" ");
				}
		               
		        String msg = message.toString().trim();

				PlayerUtils.broadcast("�c�lStaffChat �8� �a" + sender.getName() + "�8: �f" + msg, "uhc.staff");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}