package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.User;

/**
 * Hotbar command class.
 * 
 * @author LeonTG77
 */
public class HotbarCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can change their hotbar.");
			return true;
		}
		
		Player player = (Player) sender;
		Arena arena = Arena.getInstance();
		
		if (!arena.hasPlayer(player)) {
			player.sendMessage(Arena.PREFIX + "You are not in the arena.");
			return true;
		}
		
		for (int i = 0; i < 9; i++) {
			ItemStack item = player.getInventory().getItem(i);
			
			if (item == null || item.getType() == Material.AIR) {
				player.sendMessage(Arena.PREFIX + "You cannot have empty slots in your hotbar");
				return true;
			}
		}
		
		for (int i = 0; i < 9; i++) {
			ItemStack item = player.getInventory().getItem(i);
			checkForItemAndSaveIfFound(player, i, item);
		}
		
		player.sendMessage(Arena.PREFIX + "Your hotbar has been saved.");
		return true;
	}
	
	public void checkForItemAndSaveIfFound(Player player, int slot, ItemStack item) {
		String name = item.getType().name();
		User user = User.get(player);
		
		if (name.endsWith("_SWORD")) {
			user.getFile().set("hotbar.sword", slot);
			user.saveFile();
			return;
		}
		
		if (name.equals("BOW")) {
			user.getFile().set("hotbar.bow", slot);
			user.saveFile();
			return;
		}
		
		if (name.equals("WATER_BUCKET")) {
			user.getFile().set("hotbar.bucket", slot);
			user.saveFile();
			return;
		}
		
		if (name.endsWith("_PICKAXE")) {
			user.getFile().set("hotbar.pickaxe", slot);
			user.saveFile();
			return;
		}
		
		if (name.equals("COBBLESTONE")) {
			user.getFile().set("hotbar.cobble", slot);
			user.saveFile();
			return;
		}
		
		if (name.equals("GOLDEN_APPLE")) {
			user.getFile().set("hotbar.gapple", slot);
			user.saveFile();
			return;
		}
		
		if (name.endsWith("_SPADE")) {
			user.getFile().set("hotbar.shovel", slot);
			user.saveFile();
			return;
		}
		
		if (name.endsWith("_AXE")) {
			user.getFile().set("hotbar.axe", slot);
			user.saveFile();
			return;
		}
		
		if (item.getType().isEdible()) {
			user.getFile().set("hotbar.food", slot);
			user.saveFile();
		}
	}
}