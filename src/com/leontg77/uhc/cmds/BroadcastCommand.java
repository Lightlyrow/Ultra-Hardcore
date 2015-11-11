package com.leontg77.uhc.cmds;

import java.util.Arrays;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Broadcast command class.
 * 
 * @author LeonTG77
 */
public class BroadcastCommand implements CommandExecutor {
	private static final String PERMISSION = "uhc.broadcast";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission(PERMISSION)) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /broadcast <message>");
			return true;
		}
		
		String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
		PlayerUtils.broadcast(Main.PREFIX + "§a§l" + message);
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
		}
		return true;
	}
}