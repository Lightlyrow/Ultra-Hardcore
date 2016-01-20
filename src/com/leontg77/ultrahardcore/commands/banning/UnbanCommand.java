package com.leontg77.ultrahardcore.commands.banning;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Unban command class
 * 
 * @author LeonTG77
 */
public class UnbanCommand extends UHCCommand {	
	private static final Type BANLIST_TYPE = Type.NAME;

	public UnbanCommand() {
		super("unban", "<player>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}

		BanList list = Bukkit.getBanList(BANLIST_TYPE);
		String target = args[0];
    	
		if (!list.isBanned(target)) {
			throw new CommandException("'" + target + "' is not banned.");
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "§6" + target + " §7has been unbanned.");
		list.pardon(target);
		return true;
	}
	
	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		List<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	BanList list = Bukkit.getBanList(BANLIST_TYPE);
        	
    		for (BanEntry entry : list.getBanEntries()) {
    			String ip = entry.getTarget();
    			
    			toReturn.add(ip);
    		}
        }
		
		return toReturn;
	}
}