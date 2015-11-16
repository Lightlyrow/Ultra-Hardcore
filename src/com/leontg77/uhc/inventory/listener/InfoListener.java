package com.leontg77.uhc.inventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Gameinfo inventory listener class.
 * <p> 
 * Contains all eventhandlers for gameinfo inventory releated events.
 * 
 * @author LeonTG77
 */
public class InfoListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().equals("» §7Game Information")) {
			return;
		}
		
		event.setCancelled(true);
	}
}