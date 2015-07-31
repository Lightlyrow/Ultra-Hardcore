package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("setup")) {
			if (sender.hasPermission("uhc.setup")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /setup <radius> [world]");
					return true;
				}
				
				int radius;
				
				try {
					radius = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild radius.");
					return true;
				}
				
				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
					} else {
						sender.sendMessage(ChatColor.RED + "Only players can generate their own world.");
					}
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			}
		}
		return true;
	}
}