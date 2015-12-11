package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.NumberUtils;

/**
 * BedBombs scenario class
 * 
 * @author LeonTG77
 */
public class BedBombs extends Scenario implements Listener {
	
	public BedBombs() {
		super("BedBombs", "Beds explode in the overworld.");
	}
	
	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void on(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		if (block == null) {
			return;
		}
		
		if (!GameUtils.getGameWorlds().contains(block.getWorld())) {
			return;
		}
		
		if (block.getType() != Material.BED_BLOCK) {
			return;
		}
		
		event.setCancelled(true);
		
		Location loc = block.getLocation();
		block.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), NumberUtils.randInt(4, 6), false, true);
	}
}