package com.leontg77.ultrahardcore.commands.spectate;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * SpecChat command class
 * 
 * @author LeonTG77
 */
public class SpecChatCommand extends UHCCommand {

	public SpecChatCommand() {
		super("specchat", "<message>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can talk in the spectator chat.");
		}
		
        Spectator spec = Spectator.getInstance();
        Player player = (Player) sender;
        
		if (!spec.isSpectating(player)) {
			throw new CommandException("You can only do this while spectating.");
		}
		
		if (args.length == 0) {
        	return false;
        } 
        
        String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
        
        for (Player online : PlayerUtils.getPlayers()) {
        	if (!spec.isSpectating(online)) {
        		continue;
        	}
        	
        	online.sendMessage("§8[§5SpecChat§8] §7" + sender.getName() + "§8 » §f" + msg);
        }
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
}