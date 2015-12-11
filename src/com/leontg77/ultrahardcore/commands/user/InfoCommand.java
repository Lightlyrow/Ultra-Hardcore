package com.leontg77.ultrahardcore.commands.user;

import static com.leontg77.ultrahardcore.Main.plugin;

import java.io.File;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Info command class.
 * 
 * @author LeonTG77
 */
public class InfoCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.info")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /info <player>");
			return true;
		}

		File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
        boolean found = false;
        
		OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[0]);
		
        if (folder.exists()) {
    		for (File file : folder.listFiles()) {
    			if (file.getName().substring(0, file.getName().length() - 4).equals(target.getUniqueId().toString())) {
    				found = true;
    				break;
    			}
    		}
        }
		
		if (!found) {
			sender.sendMessage(Main.PREFIX + args[0] + " has never joined this server.");
			return true;
		}
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		User user = User.get(target);
		
		BanList list = Bukkit.getBanList(Type.NAME);
		BanEntry entry = list.getBanEntry(target.getName());
		
		long lastlogout = user.getFile().getLong("lastlogout", -1l);
		
		String muteMessage;
		String banMessage;
		
		if (user.isMuted()) {
			if (user.getUnmuteTime() == -1) {
				muteMessage = "§aTrue§7, Reason: §6" + user.getMutedReason() + " §8(§apermanent§8)";
			} else {
				muteMessage = "§aTrue§7, Reason: §6" + user.getMutedReason() + " §8(§a" + DateUtils.formatDateDiff(user.getUnmuteTime()) + "§8)";
			}
		} else {
			muteMessage = "§cFalse";
		}
		
		if (list.isBanned(target.getName())) {
			if (entry.getExpiration() == null) {
				banMessage = "§aTrue§7, Reason: §6" + entry.getReason() + " §8(§apermanent§8)";
			} else {
				banMessage = "§aTrue§7, Reason: §6" + entry.getReason() + " §8(§a" + DateUtils.formatDateDiff(entry.getExpiration().getTime()) + "§8)";
			}
		} else {
			banMessage = "§cFalse";
		}

		sender.sendMessage(Main.PREFIX + "Info about §6" + target.getName() + "§8:");
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		sender.sendMessage("§8» §7Status: §6" + (target.getPlayer() == null ? "§cNot online" : "§aOnline"));
		sender.sendMessage("§8» §7UUID: §6" + user.getFile().getString("uuid"));
		sender.sendMessage("§8» §7IP: §6" + (sender.hasPermission("uhc.info.ip") ? user.getFile().getString("ip") : "§m###.##.##.###"));
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		sender.sendMessage("§8» §7Banned: §6" + banMessage);
		sender.sendMessage("§8» §7Muted: §6" + muteMessage);
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		sender.sendMessage("§8» §7First Joined: §6" + new Date(user.getFile().getLong("firstjoined")));
		sender.sendMessage("§8» §7Last login: §6" + DateUtils.formatDateDiff(user.getFile().getLong("lastlogin")));
		sender.sendMessage("§8» §7Last logout: §6" + (lastlogout == -1l ? "§cHasn't logged out" : DateUtils.formatDateDiff(lastlogout)));
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		return true;
	}
}