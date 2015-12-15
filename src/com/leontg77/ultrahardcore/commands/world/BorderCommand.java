package com.leontg77.ultrahardcore.commands.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Border command class.
 * 
 * @author LeonTG77
 */
public class BorderCommand extends UHCCommand {

	public BorderCommand() {
		super("border", "<world> <size>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player player = (Player) sender;
			World world = player.getWorld();
			
			WorldBorder border = world.getWorldBorder();
			int size = (int) border.getSize();
			
			sender.sendMessage(Main.PREFIX + "The border is currently: §a" + size + "x" + size);
			return true;
		}
		
		if (!sender.hasPermission("uhc.border.set")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 1) {
			return false;
		}
		
		World world = Bukkit.getWorld(args[0]);
		
		if (world == null) {
			throw new CommandException("The world '" + args[0] + "' does not exist.");
		}

		WorldBorder border = world.getWorldBorder();
		
		int radius = parseInt(args[1], "radius");
		border.setSize(radius);
		
		PlayerUtils.broadcast(Main.PREFIX + "Borders in world '§6" + world.getName() + "§7' has been setup with radius §a" + radius + "x" + radius + "§7.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	for (World world : Bukkit.getWorlds()) {
        		toReturn.add(world.getName());
        	}
        }
		
		if (args.length == 2) {
        	toReturn.add("3000");
        	toReturn.add("2000");
        }
		
		return toReturn;
	}
}