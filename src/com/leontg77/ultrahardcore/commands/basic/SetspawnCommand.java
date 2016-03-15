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
	private final Settings settings;
	
	public SetspawnCommand(Settings settings) {
		super("setspawn", "");
		
		this.settings = settings;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can set the spawn point.");
		}

		Player player = (Player) sender;
		Location loc = player.getLocation();
		
		settings.getData().set("spawn.world", loc.getWorld().getName());
		settings.getData().set("spawn.x", loc.getX());
		settings.getData().set("spawn.y", loc.getY());
		settings.getData().set("spawn.z", loc.getZ());
		settings.getData().set("spawn.yaw", loc.getYaw());
		settings.getData().set("spawn.pitch", loc.getPitch());
        settings.saveData();
        
        player.sendMessage(String.format(Main.PREFIX + "You have set the spawnpoint to W: §a%s §7X: §a%s §7Y: §a%s §7Z: §a%s§7.", player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}