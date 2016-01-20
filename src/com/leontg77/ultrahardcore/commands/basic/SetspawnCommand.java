package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Setspawn command class.
 * 
 * @author LeonTG77
 */
public class SetspawnCommand extends UHCCommand {
	
	public SetspawnCommand() {
		super("setspawn", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can set the spawn point.");
		}

		final Player player = (Player) sender;

		final Settings settings = Settings.getInstance();
		final Location loc = player.getLocation();
		
		settings.getData().set("spawn.world", loc.getWorld().getName());
		settings.getData().set("spawn.x", loc.getX());
		settings.getData().set("spawn.y", loc.getY());
		settings.getData().set("spawn.z", loc.getZ());
		settings.getData().set("spawn.yaw", loc.getYaw());
		settings.getData().set("spawn.pitch", loc.getPitch());
        settings.saveData();
        
        player.sendMessage(Main.PREFIX + "You have set the spawnpoint.");
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
}