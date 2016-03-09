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

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DQ command class.
 * 
 * @author LeonTG77
 */
public class DQCommand extends UHCCommand {	
	private final BoardManager board;

	/**
	 * DQ command class constructor.
	 * 
	 * @param board The board manager class.
	 */
	public DQCommand(BoardManager board) {
		super("dq", "<player> <reason>");
		
		this.board = board;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		Player target = Bukkit.getPlayer(args[0]);

    	if (target == null) {
	    	throw new CommandException("'" + args[0] + "' is not online.");
		}
    	
    	if (target.hasPermission("uhc.staff") && !sender.hasPermission(getPermission() + ".bypass")) {
	    	throw new CommandException("You can't dq this player.");
    	}
		
    	String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
    	
    	PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been disqualified for §a" + message + "§7.");
		
    	for (Player online : Bukkit.getOnlinePlayers()) {
    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
    	}

    	board.resetScore(target.getName());
		board.resetScore(args[0]);
    	
    	target.setWhitelisted(false);
    	
    	PlayerDeathEvent event = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
    	Bukkit.getPluginManager().callEvent(event);
		
    	target.kickPlayer(
    	"§8» §7You have been §cdisqualified §7from this game §8«" +
    	"\n" + 
    	"\n§cReason §8» §7" + message + 
    	"\n" + 
    	"\n§8» §7Don't worry, this is not a perma ban. §8«"
    	);
    	
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, final String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				toReturn.add(online.getName());
			}
		}
		
		return toReturn;
	}
}