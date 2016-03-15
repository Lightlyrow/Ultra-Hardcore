package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.ultrahardcore.gui.GUI;

public class WorldCreatorGUI extends GUI implements Listener {

	public WorldCreatorGUI(String name, String description) {
		super(name, description);
	}
	
	private final Inventory inv = Bukkit.createInventory(null, 45, "§4World Creator Options");

	@Override
	public void onSetup() {
		glassify(inv);
		update();
	}
	
	public Inventory get() {
		return null;
		
	}

	@SuppressWarnings("unused")
	public void update() {
		List<String> lore = new ArrayList<String>();
		
		ItemStack newStone = new ItemStack(Material.STONE, 1, (short) 2);
		ItemMeta newStoneMeta = newStone.getItemMeta();
	}
}