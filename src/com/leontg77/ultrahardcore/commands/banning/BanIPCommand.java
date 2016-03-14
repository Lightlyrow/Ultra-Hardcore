package com.leontg77.ultrahardcore.commands.banning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
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
 * BanIP command class
 * 
 * @author LeonTG77
 */
public class BanIPCommand extends UHCCommand {	
	private final BoardManager board;

	public BanIPCommand(BoardManager board) {
		super("banip", "<ip> <reason>");
		
		this.board = board;
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length < 3) {
			return false;
		}
		
    	final BanList list = Bukkit.getBanList(Type.IP);

		final String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

		PlayerUtils.broadcast(Main.PREFIX + "An IP has been banned for §a" + message);
		final BanEntry ban = list.addBan(args[0], message, null, sender.getName());
		
    	for (Player online : Bukkit.getOnlinePlayers()) {
    		if (!online.getAddress().getAddress().getHostAddress().equals(args[0])) {
    			continue;
    		}
    		
			board.resetScore(online.getName());
	    	
			final PlayerDeathEvent event = new PlayerDeathEvent(online, new ArrayList<ItemStack>(), 0, null);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			online.kickPlayer(
	    	"§8» §7You have been §4IP banned §7from §6Arctic UHC §8«" +
	    	"\n" + 
	    	"\n§cReason §8» §7" + ban.getReason() +
	    	"\n§cBanned by §8» §7" + ban.getSource() +
 			"\n" +
	   		"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
	    	);
    	}
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
}