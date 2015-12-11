package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * BloodDiamonds scenario class
 * 
 * @author LeonTG77
 */
public class BloodDiamonds extends Scenario implements Listener {
	
	public BloodDiamonds() {
		super("BloodDiamonds", "Every time you mine a diamond you take half a heart.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}

	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
    	
    	if (block.getType() != Material.DIAMOND_ORE) {
    		return;
    	}
    	
		player.damage(1);
    }
}