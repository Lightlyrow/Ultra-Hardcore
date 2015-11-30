package com.leontg77.uhc.cmds;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.managers.BoardManager;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Tempban command class
 * 
 * @author LeonTG77
 */
public class TempbanCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (!sender.hasPermission("uhc.tempban")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 3) {
			sender.sendMessage(Main.PREFIX + "Usage: /tempban <player> <time> <reason>");
			return true;
		}
    	
    	final Player target = Bukkit.getServer().getPlayer(args[0]);
    	
    	final BoardManager board = BoardManager.getInstance();
    	final BanList list = Bukkit.getBanList(Type.NAME);
    	
    	long time = DateUtils.parseDateDiff(args[1], true);
		Date date = new Date(time);
    	
		final String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length));

    	if (target == null) {
			PlayerUtils.broadcast(Main.PREFIX + "§6" + args[0] + " §7has been temp-banned for §a" + msg);
			
    		list.addBan(args[0], msg, date, sender.getName());
			board.resetScore(args[0]);
            return true;
		}
    	
    	if (target.hasPermission("uhc.staff") && !sender.hasPermission("uhc.tempban.bypass")) {
    		sender.sendMessage(Main.PREFIX + "You cannot tempban this player.");
    		return true;
    	}

    	new BukkitRunnable() {
        	int left = 3;
        	
    		public void run() {
    			if (left == 0) {
    				long time = DateUtils.parseDateDiff(args[1], true);
    				Date date = new Date(time);
    				
    				PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been temp-banned for §a" + msg + "§7. §8(§a" + DateUtils.formatDateDiff(time) + "§8)");
    				
    		    	for (Player online : PlayerUtils.getPlayers()) {
    		    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
    		    	}
    		    	
    	    		BanEntry ban = list.addBan(target.getName(), msg, date, sender.getName());
    		    	target.setWhitelisted(false);
    		    	
    				board.resetScore(args[0]);
    		    	board.resetScore(target.getName());
    		    	
    		    	PlayerDeathEvent event = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
    				Bukkit.getServer().getPluginManager().callEvent(event);
    				
    				target.kickPlayer(
    				"§8» §7You have been §4temp-banned §7from §6Arctic UHC §8«" +
    				"\n" + 
    				"\n§cReason §8» §7" + ban.getReason() +
    				"\n§cBanned by §8» §7" + ban.getSource() +
    				"\n§cExpires in §8» §7" + DateUtils.formatDateDiff(time) +
    				"\n" +
    				"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
    				);
    		    	
    		    	cancel();
    			} else {
    				PlayerUtils.broadcast(Main.PREFIX + "Incoming ban in §6" + left + "§7.");
    				left--;
    				
    		    	for (Player online : PlayerUtils.getPlayers()) {
    		    		online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1, 1);
    		    	}
    			}
    		}
    	}.runTaskTimer(Main.plugin, 0, 20);
		return true;
	}
}