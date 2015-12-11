package com.leontg77.ultrahardcore.commands.basic;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Near command class.
 * 
 * @author LeonTG77
 */
public class NearCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can see nearby players.");
			return true;
		}

		Spectator spec = Spectator.getInstance();
		Player player = (Player) sender;
		
		if (!sender.hasPermission("uhc.near") && !spec.isSpectating(player)) {
			player.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		StringBuilder nearList = new StringBuilder("");
		
		int radius = 200;
		
		if (args.length > 0) {
			try {
				radius = Integer.parseInt(args[0]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not a vaild radius.");
				return true;
			}
		}
		
		for (Entity near : PlayerUtils.getNearby(player.getLocation(), radius)) {
			if (near instanceof Player) {
				Player nearby = (Player) near;
				
				if (nearby == player) {
					continue;
				}
				
				if (!player.canSee(nearby)) {
					continue;
				}
				
				if (nearList.length() > 0) {
					nearList.append("§8, ");
				}
				
				nearList.append("§a" + nearby.getName() + "§7(§c" + ((int) player.getLocation().distance(nearby.getLocation())) + "m§7)§a");
			}
		}
		
		player.sendMessage(Main.PREFIX + "Nearby players:");
		player.sendMessage("§8» §7" + (nearList.length() > 0 ? nearList.toString().trim() : "There are no players nearby."));
		return true;
	}
}