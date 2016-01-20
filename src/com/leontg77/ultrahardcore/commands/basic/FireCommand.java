package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Fireworks;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Fire command class.
 * 
 * @author LeonTG77
 */
public class FireCommand extends UHCCommand {

	public FireCommand() {
		super("fire", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		Fireworks fire = Fireworks.getInstance();
		
		PlayerUtils.broadcast(Main.PREFIX + "The firework show has started.");
		fire.startFireworkShow();
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
}