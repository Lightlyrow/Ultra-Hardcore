package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.uhc.scenario.Scenario;

/**
 * Timber scenario class
 * 
 * @author LeonTG77
 */
public class Timber extends Scenario implements Listener {
	private boolean enabled = false;
	
	public Timber() {
		super("Timber", "When chopping down a tree the entire tree will chop.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (block.getType() != Material.LOG && block.getType() != Material.LOG_2) {
			return;
		}

		block = block.getRelative(BlockFace.UP);
		
		while (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
			block.breakNaturally();
			block = block.getRelative(BlockFace.UP);
		}
	}
}