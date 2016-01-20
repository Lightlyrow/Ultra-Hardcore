package com.leontg77.ultrahardcore.commands.banning;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Tempban command class
 * 
 * @author LeonTG77
 */
public class TempbanCommand extends UHCCommand {	

	public TempbanCommand() {
		super("tempban", "<player> <time> <reason>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length < 3) {
			return false;
		}
    	
    	final Player target = Bukkit.getPlayer(args[0]);
    	
    	final BoardManager board = BoardManager.getInstance();
    	final BanList list = Bukkit.getBanList(Type.NAME);
    	
    	long time = DateUtils.parseDateDiff(args[1], true);
		Date date = new Date(time);
    	
		final String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length));

    	if (target == null) {
			PlayerUtils.broadcast(Main.PREFIX + "§6" + args[0] + " §7has been temp-banned for §a" + message);
			
    		list.addBan(args[0], message, date, sender.getName());
			board.resetScore(args[0]);
            return true;
		}
    	
    	if (target.hasPermission("uhc.staff") && !sender.hasPermission("uhc.tempban.bypass")) {
	    	throw new CommandException("You cannot ban this player.");
    	}

    	new BukkitRunnable() {
        	int left = 3;
        	
    		public void run() {
    			if (left > 0) {
    				PlayerUtils.broadcast(Main.PREFIX + "Incoming ban in §6" + left + "§7.");
    				left--;
    				
    		    	for (Player online : PlayerUtils.getPlayers()) {
    		    		online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1, 1);
    		    	}
    		    	return;
    			}
    			
				long time = DateUtils.parseDateDiff(args[1], true);
				final Date date = new Date(time);
				
				PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been temp-banned for §a" + message + "§7. §8(§a" + DateUtils.formatDateDiff(time) + "§8)");
				
		    	for (Player online : PlayerUtils.getPlayers()) {
		    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
		    	}
		    	
		    	final BanEntry ban = list.addBan(target.getName(), message, date, sender.getName());
		    	target.setWhitelisted(false);
		    	
				board.resetScore(args[0]);
		    	board.resetScore(target.getName());
		    	
		    	final PlayerDeathEvent event = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
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
    		}
    	}.runTaskTimer(Main.plugin, 0, 20);
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			for (Player online : PlayerUtils.getPlayers()) {
				toReturn.add(online.getName());
			}
		}
		
		return toReturn;
	}
}