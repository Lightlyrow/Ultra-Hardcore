package com.leontg77.ultrahardcore.commands.banning;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		
		if (!Bukkit.hasWhitelist()) {
			throw new CommandException("You cannot DQ when the whitelist is off.");
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

    	board.resetScore(target.getName());
		board.resetScore(args[0]);
    	
    	target.setWhitelisted(false);
    	target.setHealth(0);
		
    	target.kickPlayer(
    	"§8» §7You have been §cdisqualified §7from this game §8«" +
    	"\n" + 
    	"\n§cReason §8» §7" + message + 
    	"\n§cDQ'ed by §8» §7" + sender.getName() + 
    	"\n" + 
    	"\n§8» §7Don't worry, this is not a perma ban. §8«"
    	);
    	
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allPlayers();
	}
}