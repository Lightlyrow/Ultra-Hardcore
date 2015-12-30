package com.leontg77.ultrahardcore.commands.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.utils.GameUtils;

/**
 * Hall of fame command class.
 * 
 * @author LeonTG77
 */
public class HOFCommand extends UHCCommand {

	public HOFCommand() {
		super("hof", "[host]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		String host = GameUtils.getHostName(Game.getInstance().getHost());
		
		Settings settings = Settings.getInstance();
		InvGUI inv = InvGUI.getInstance();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("global")) {
				int i = 0;
				
				for (String path : settings.getHOF().getKeys(false)) {
					i = i + settings.getHOF().getConfigurationSection(path).getKeys(false).size();
				}
				
				sender.sendMessage(Main.PREFIX + "There's been a total of §a" + i + " §7games hosted here.");
				return true;
			}
			
			host = GameUtils.getHostName(args[0]);
		}
		
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can view the hall of fame.");
		}
		
		Player player = (Player) sender;
		
		if (settings.getHOF().getConfigurationSection(host) == null) {
			throw new CommandException("'" + host + "' has never hosted any games here.");
		}
		
		inv.openHOF(player, host);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		
    	ArrayList<String> toReturn = new ArrayList<String>();
		Settings settings = Settings.getInstance();
    	
		if (args.length == 1) {
        	if (args[0].equals("")) {
        		for (String type : settings.getHOF().getKeys(false)) {
    				toReturn.add(type);
        		}
        		
				toReturn.add("global");
        	} else {
        		for (String type : settings.getHOF().getKeys(false)) {
        			if (type.toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        		
    			if ("global".startsWith(args[0].toLowerCase())) {
    				toReturn.add("global");
    			}
        	}
        }
		
    	return toReturn;
	}
}