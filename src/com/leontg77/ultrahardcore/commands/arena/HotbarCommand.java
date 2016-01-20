package com.leontg77.ultrahardcore.commands.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Hotbar command class.
 * 
 * @author LeonTG77
 */
public class HotbarCommand extends UHCCommand {	

	public HotbarCommand() {
		super("hotbar", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can change their hotbar.");
		}
		
		throw new CommandException("Feature currently broken.");
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}