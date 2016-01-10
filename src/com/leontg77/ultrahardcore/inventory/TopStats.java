package com.leontg77.ultrahardcore.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Top stats inventory class.
 * 
 * @author LeonTG77
 */
public class TopStats extends InvGUI implements Listener {
	private Inventory inv = Bukkit.createInventory(null, 45, "Top 10 Stats");
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().equals(this.inv.getTitle())) {
			return;
		}
		
		event.setCancelled(true);
	}

	/**
	 * Get the top stats inventory.
	 * 
	 * @return The inventory.
	 */
	public Inventory get() {
		return inv;
	}

	/**
	 * Update the top stats inventory.
	 */
	public void update() {
		List<String> data = new ArrayList<String>();
		int slot = 0;

		Map<String, Double> deaths = new HashMap<String, Double>();
		Map<String, Double> kills = new HashMap<String, Double>();
		
		List<FileConfiguration> files = FileUtils.getUserFiles();
		
		for (Stat stat : Stat.values()) {
			data.clear();
			
			for (FileConfiguration config : files) {
				String name = config.getString("username");
				double number = config.getDouble("stats." + stat.name().toLowerCase());
				
				data.add(number + " " + name);

				if (stat == Stat.KILLS) {
					kills.put(name, number);
				}
				
				if (stat == Stat.DEATHS) {
					deaths.put(name, number);
				}
			}

			Collections.sort(data, new Comparator<String>() {
			    public int compare(String a, String b) {
			    	double aVal = Double.parseDouble(a.split(" ")[0]);
			       	double bVal = Double.parseDouble(b.split(" ")[0]);
			       	
			       	return Double.compare(aVal, bVal);
			    }
			});
			
			addItem(data, stat.getName(), slot);
			slot++;
			
			inv.setItem(slot, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
			slot++;
		}
		
		data.clear();
		
		for (String name : kills.keySet()) {
			int d = (int) deaths.get(name).doubleValue();
			int k = (int) kills.get(name).doubleValue();
			
			double kdr;
			
			if (d < 2) {
				continue;
			} 
			
			kdr = ((double) k) / ((double) d);
			
			data.add(NumberUtils.formatDouble(kdr) + " " + name);
		}

		Collections.sort(data, new Comparator<String>() {
		    public int compare(String a, String b) {
		       	double aVal = Double.parseDouble(a.split(" ")[0]);
		       	double bVal = Double.parseDouble(b.split(" ")[0]);
		       	
		       	return Double.compare(aVal, bVal);
		    }
		});
		
		addItem(data, "KDR", slot);
		slot++;
	}
	
	/**
	 * Add the stat item to the inventory.
	 * 
	 * @param data The data list for the stat.
	 * @param statName The stat name.
	 * @param slot The slot of the item.
	 */
	private void addItem(List<String> data, String statName, int slot) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		meta.setDisplayName("§8» §6" + statName + " §8«");
		
		List<String> lore = new ArrayList<String>();
		lore.add(" ");
		
		int number = 1;
		
		for (int i = data.size() - 1; i >= data.size() - 10; i--) {
			if (i >= data.size()) {
				continue;
			}
			
			String line = data.get(i);
			
			double value = Double.parseDouble(line.split(" ")[0]);
			String name = line.split(" ")[1];

			boolean isDamage = statName.equalsIgnoreCase("damage taken");

			String sDamage = NumberUtils.makePercent(value);
			int iDamage = Integer.parseInt(sDamage.substring(2));
			
			if (number == 10) {
				lore.add("§6#" + number + "§8 | §7" + name + " §8» §a" + (isDamage ? NumberUtils.formatInt(iDamage) + "%" : NumberUtils.formatDouble(value)));
			} else {
				if (number == 1) {
					meta.setOwner(name);
				}
				
				lore.add(" §6#" + number + "§8  | §7" + name + " §8» §a" + (isDamage ? NumberUtils.formatInt(iDamage) + "%" : NumberUtils.formatDouble(value)));
			}

			number++;
		}
		
		lore.add(" ");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		inv.setItem(slot, item);
	}
}