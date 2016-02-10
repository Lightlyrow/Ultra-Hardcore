package com.leontg77.ultrahardcore.commands.basic;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

public class ParkourCommand extends UHCCommand {

	public ParkourCommand() {
		super("pakour", "<checkpoint|restart|leave>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
