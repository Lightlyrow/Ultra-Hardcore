package com.leontg77.uhc.cmds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.utils.FileUtils;

/**
 * Top command class.
 * 
 * @author LeonTG77
 */
public class TopCommand implements CommandExecutor, TabCompleter {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can open the top 10 inventory.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length == 0) {
			List<String> data = new ArrayList<String>();

			Inventory inv = Bukkit.createInventory(null, 27, "Top players");
			
			for (Stat stat : Stat.values()) {
				for (FileConfiguration config : FileUtils.getUserFiles()) {
					String name = config.getString("username");
					int number = config.getInt("stats." + stat.name().toLowerCase());
					
					data.add(number + " " + name);
				}
				
				Collections.sort(data, new Comparator<String>() {
				    public int compare(String a, String b) {
				       	int aVal = Integer.parseInt(a.split(" ")[0]);
				       	int bVal = Integer.parseInt(b.split(" ")[0]);
				       	
				       	return Integer.compare(aVal, bVal);
				    }
				});
				
				String line = data.get(data.size() - 1);
				String name = line.split(" ")[1], value = line.split(" ")[0];
				
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName("§6" + stat.getName() + " §8| §7" + name + " §8» §a" + value);
				meta.setOwner(name);
				item.setItemMeta(meta);
				inv.addItem(item);
				
				data.clear();
			}

			player.openInventory(inv);
			return true;
		}
		
		Stat stat;
		
		try {
			stat = Stat.valueOf(args[0].toUpperCase());
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + args[0] + " is not an vaild stat.");
			return true;
		}
		
		List<String> data = new ArrayList<String>();
		
		for (FileConfiguration config : FileUtils.getUserFiles()) {
			String name = config.getString("username");
			int number = config.getInt("stats." + stat.name().toLowerCase());
			
			data.add(number + " " + name);
		}
		
		Collections.sort(data, new Comparator<String>() {
		    public int compare(String a, String b) {
		       	int aVal = Integer.parseInt(a.split(" ")[0]);
		       	int bVal = Integer.parseInt(b.split(" ")[0]);
		       	
		       	return Integer.compare(aVal, bVal);
		    }
		});
		
		Inventory inv = Bukkit.createInventory(null, 18, "Top 10: §7" + stat.getName());
		
		int current = 4;
		int hash = 1;
		
		for (int i = data.size() - 1; i >= data.size() - 10; i--) {
			String line = data.get(i);
			String name = line.split(" ")[1], value = line.split(" ")[0];
			
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setDisplayName("§6#" + hash + " §8| §7" + name + " §8» §a" + value);
			meta.setOwner(name);
			item.setItemMeta(meta);
			inv.setItem(current, item);
			
			if (current == 4) {
				current = 9;
			} else {
				current++;
			}
			hash++;
		}

		player.openInventory(inv);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}

    	ArrayList<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
        	if (args[0].equals("")) {
        		for (Stat stat : Stat.values()) {
        			toReturn.add(stat.name().toLowerCase());
        		}
        		
        		toReturn.add("global");
        	} else {
        		for (Stat stat : Stat.values()) {
        			if (stat.name().toLowerCase().startsWith(args[0].toLowerCase())) {
        				toReturn.add(stat.name().toLowerCase());
        			}
        		}
        		
        		if ("global".startsWith(args[0].toLowerCase())) {
            		toReturn.add("global");
    			}
        	}
        }
		
    	return toReturn;
	}
}