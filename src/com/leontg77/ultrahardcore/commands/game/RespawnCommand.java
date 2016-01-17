package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Respawn command class.
 * 
 * @author LeonTG77
 */
public class RespawnCommand extends UHCCommand {

	public RespawnCommand() {
		super("respawn", "<player> [givebasicstuff]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}

		boolean giveBasicStuff = false;
		User user = User.get(target);
		
		if (args.length > 1) {
			giveBasicStuff = parseBoolean(args[1], "Give basic stuff");
		}
		
		target.teleport(user.getDeathLocation());
		
		if (!giveBasicStuff) {
			return true;
		}
		
		PlayerUtils.giveItem(target, new ItemStack(Material.IRON_SWORD));
		PlayerUtils.giveItem(target, new ItemStack(Material.IRON_PICKAXE));
		PlayerUtils.giveItem(target, new ItemStack(Material.STONE_AXE));
		PlayerUtils.giveItem(target, new ItemStack(Material.STONE_PICKAXE));
		PlayerUtils.giveItem(target, new ItemStack(Material.STONE_SPADE));
		PlayerUtils.giveItem(target, new ItemStack(Material.IRON_HELMET));
		PlayerUtils.giveItem(target, new ItemStack(Material.IRON_CHESTPLATE));
		PlayerUtils.giveItem(target, new ItemStack(Material.IRON_LEGGINGS));
		PlayerUtils.giveItem(target, new ItemStack(Material.IRON_BOOTS));
		PlayerUtils.giveItem(target, new ItemStack(Material.LOG, 32));
		PlayerUtils.giveItem(target, new ItemStack(Material.COOKED_BEEF, 22));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return null;
		}
		
		return new ArrayList<String>();
	}
}