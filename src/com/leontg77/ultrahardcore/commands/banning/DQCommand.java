package com.leontg77.ultrahardcore.commands.banning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DQ command class
 * 
 * @author LeonTG77
 */
public class DQCommand extends UHCCommand {	

	public DQCommand() {
		super("dq", "<player> <reason>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
    	final BoardManager board = BoardManager.getInstance();
    	final Player target = Bukkit.getPlayer(args[0]);
				
		final String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

    	if (target == null) {
	    	throw new CommandException("'" + args[0] + "' is not online.");
		}
    	
    	if (target.hasPermission("uhc.staff") && !sender.hasPermission("uhc.dq.bypass")) {
	    	throw new CommandException("You cannot ban this player.");
    	}

    	new BukkitRunnable() {
        	int left = 3;
        	
    		public void run() {
    			if (left > 0) {
    				PlayerUtils.broadcast(Main.PREFIX + "Incoming DQ in §6" + left + "§7.");
    				left--;
    				
    		    	for (Player online : Bukkit.getOnlinePlayers()) {
    		    		online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1, 1);
    		    	}
    		    	return;
    			}
    				
    			PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been disqualified for §a" + message + " §8(§afor this game§8)");
				
		    	for (Player online : Bukkit.getOnlinePlayers()) {
		    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
		    	}
		    	
		    	target.setWhitelisted(false);
		    	
				board.resetScore(args[0]);
		    	board.resetScore(target.getName());
		    	
		    	final PlayerDeathEvent event = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
		    	Bukkit.getPluginManager().callEvent(event);
				
		    	target.kickPlayer(
		    	"§8» §7You have been §cdisqualified §7from this game §8«" +
		    	"\n" + 
		    	"\n§cReason §8» §7" + message
		    	);
		    	
		    	cancel();
    		}
    	}.runTaskTimer(Main.plugin, 0, 20);
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return null;
	}
}