package com.leontg77.ultrahardcore.commands.basic;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * StaffChat command class
 * 
 * @author LeonTG77
 */
public class StaffChatCommand extends UHCCommand {

	public StaffChatCommand() {
		super("ac", "<message>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) {
		if (args.length == 0) {
        	return false;
        } 
        
    	String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
		PlayerUtils.broadcast("§8[§4StaffChat§8] §c" + sender.getName() + "§8 » §f" + message, "uhc.staff");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
}