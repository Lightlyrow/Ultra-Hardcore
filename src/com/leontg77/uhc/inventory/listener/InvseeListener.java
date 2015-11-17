package com.leontg77.uhc.inventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.leontg77.uhc.inventory.InvGUI;

/**
 * Invsee inventory listener class.
 * <p> 
 * Contains all eventhandlers for invsee inventory releated events.
 * 
 * @author LeonTG77
 */
public class InvseeListener implements Listener {
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();

		if (!InvGUI.invsee.containsKey(inv)) {
			return;
		}
		
		InvGUI.invsee.get(inv).cancel();
		InvGUI.invsee.remove(inv);
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().endsWith("'s Inventory")) {
			return;
		}
		
		event.setCancelled(true);
	}
}