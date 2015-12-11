package com.leontg77.ultrahardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.resetting.FeedCommand;
import com.leontg77.ultrahardcore.commands.resetting.SethealthCommand;
import com.leontg77.ultrahardcore.commands.world.WorldCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Command handler class.
 * 
 * @author LeonTG77
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
	public List<UHCCommand> cmds = new ArrayList<UHCCommand>();
	
	/**
	 * Register all the commands.
	 */
	public void registerCommands() {
		cmds.add(new FeedCommand());
		cmds.add(new SethealthCommand());
		cmds.add(new WorldCommand());
		
		for (UHCCommand cmd : cmds) {
			Bukkit.getPluginCommand(cmd.getName()).setExecutor(this);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		UHCCommand command = getCommandExact(cmd.getName());
		
		if (command == null) {
			// this shouldn't happen, it only uses registered commands.
			return true;
		}
		
		// if they don't have permission, tell them so and stop
		if (!sender.hasPermission(command.getPermission())) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		try {
			// if the command returned false, send the usage.
			if (!command.execute(sender, args)) {
				sender.sendMessage(Main.PREFIX + "Usage: " + command.getUsage());
			}
		} catch (Exception ex) {
			// send them the error message in red if anything failed.
			sender.sendMessage(ChatColor.RED + ex.getMessage());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		UHCCommand command = getCommandExact(cmd.getName());
		
		if (command == null) {
			// this shouldn't happen, it only uses registered commands.
			return null;
		}
		
		// if they don't have permission, stop
		if (!sender.hasPermission(command.getPermission())) {
			return null;
		}
		
		try {
			List<String> list = command.tabComplete(sender, args);
			
			if (list == null) {
				return getAllPlayerNames(sender);
			}
			
			return list;
		} catch (Exception ex) {
			// send them the error message in red if anything failed.
			sender.sendMessage(ChatColor.RED + ex.getMessage());
		}
		return null;
	}

	/**
     * Get a uhc command.
     *
     * @param name The name of the uhc command
     * @return The UHCCommand if found, null otherwise.
     */
    protected UHCCommand getCommandExact(String name) {
        for (UHCCommand cmd : cmds) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        
        return null;
    }
	
    /**
     * Get a list of all players online's names
     * 
     * @param sender The sender, if a player it will not add hidden players to the list, adds everyone if it's the console.
     * @return List of player names.
     */
	private List<String> getAllPlayerNames(CommandSender sender) {
		List<String> list = new ArrayList<String>();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (!(sender instanceof Player) || ((Player) sender).canSee(online)) {
				list.add(online.getName());
			}
		}
		
		return list;
	}
}