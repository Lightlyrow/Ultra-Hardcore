package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.BlockUtils;

/**
 * Diamondless scenario class
 * 
 * @author LeonTG77
 */
public class Diamondless extends Scenario implements Listener {

	public Diamondless() {
		super("Diamondless", "You can't mine diamonds.");
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
    	
		boolean cutclean = ScenarioManager.getInstance().getScenario("CutClean").isEnabled();
		ItemStack replace = new ItemStack(cutclean ? Material.IRON_INGOT : Material.IRON_ORE);
		
		event.setCancelled(true);
		block.setType(Material.AIR);
		
		BlockUtils.blockBreak(player, block);
		BlockUtils.degradeDurabiliy(player);
		BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), replace);
    }
}