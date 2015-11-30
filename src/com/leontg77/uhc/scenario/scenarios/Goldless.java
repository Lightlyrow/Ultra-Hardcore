package com.leontg77.uhc.scenario.scenarios;

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
 * Goldless scenario class
 * 
 * @author LeonTG77
 */
public class Goldless extends Scenario implements Listener {

	public Goldless() {
		super("Goldless", "You can't mine gold");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
	    	
		if (block.getType() != Material.GOLD_ORE) {
			return;
		}
    	
		boolean cutclean = ScenarioManager.getInstance().getScenario(CutClean.class).isEnabled();
		ItemStack replace = new ItemStack(cutclean ? Material.IRON_INGOT : Material.IRON_ORE);
		
		BlockUtils.blockBreak(player, block);
		BlockUtils.degradeDurabiliy(player);
		BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), replace);
		
		event.setCancelled(true);
		block.setType(Material.AIR);
    }
}