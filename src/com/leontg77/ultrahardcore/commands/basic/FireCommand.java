package com.leontg77.ultrahardcore.commands.basic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Fireworks;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Fire command class.
 * 
 * @author LeonTG77
 */
public class FireCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!sender.hasPermission("uhc.fire")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		Fireworks fire = Fireworks.getInstance();
		
		PlayerUtils.broadcast(Main.PREFIX + "The firework show has started.");
		fire.startFireworkShow();
		return true;
	}
}