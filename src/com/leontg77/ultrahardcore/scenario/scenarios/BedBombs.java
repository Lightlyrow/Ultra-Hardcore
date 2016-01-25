package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

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
		if (!State.isState(State.INGAME)) {
			return;
		}

		final Block block = event.getClickedBlock();
		
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
		
		final Location loc = block.getLocation();
		block.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), NumberUtils.randomIntBetween(4, 6), false, true);
	}
}