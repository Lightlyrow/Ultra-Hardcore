package com.leontg77.uhc.cmds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.inventory.InvGUI;
import com.leontg77.uhc.user.User;

/**
 * Stats command class.
 * 
 * @author LeonTG77
 */
public class StatsCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Game game = Game.getInstance();
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can view the own stats.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (game.isRecordedRound()) {
			sender.sendMessage(Main.PREFIX + "Stats are disabled in RR's.");
			return true;
		}

		InvGUI inv = InvGUI.getInstance();
		Player target;
		
		if (args.length == 0) {
			target = player;
		} else {
			target = Bukkit.getServer().getPlayer(args[0]);
		}
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not online.");
			return true;
		}
		
		User user = User.get(target);
		inv.openStats(player, user);
		return true;
	}
}