package com.leontg77.ultrahardcore.commands.basic;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Back command class.
 * 
 * @author LeonTG77
 */
public class BackCommand extends UHCCommand {

	public BackCommand() {
		super("back", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}