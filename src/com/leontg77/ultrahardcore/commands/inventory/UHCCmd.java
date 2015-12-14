package com.leontg77.ultrahardcore.commands.inventory;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.inventory.InvGUI;

/**
 * UHC command class.
 * 
 * @author LeonTG77
 */
public class UHCCmd implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can view the game info inventory.");
			return true;
		}
		
		Player player = (Player) sender;
		InvGUI inv = InvGUI.getInstance();
		
		inv.openGameInfo(player);
		return true;
	}
}