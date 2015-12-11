package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Balance scenario class
 * 
 * @author LeonTG77
 */
public class Balance extends Scenario implements Listener {
	private Map<String, Integer> minedAmount = new HashMap<String, Integer>();
	private Map<String, Integer> chance = new HashMap<String, Integer>();
	
	public Balance() {
		super("Balance", "After the 5th diamond, it gets progressively harder to obtain diamonds.");
	}

	@Override
	public void onDisable() {
		minedAmount.clear();
		chance.clear();
	}

	@Override
	public void onEnable() {
		minedAmount.clear();
		chance.clear();
	}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (block.getType() != Material.DIAMOND) {
			return;
		}
		
		if (!minedAmount.containsKey(player.getName())) {
			minedAmount.put(player.getName(), 0);
		}
		
		int amount = minedAmount.get(player.getName());
		amount++;
		
		minedAmount.put(player.getName(), amount);
		
		if (amount < 5) {
			return;
		}
		
		if (amount == 5) {
			player.sendMessage("§6Uh oh! Diamonds might disappear when mined now!");
			return;
		}
		
		chance.put(player.getName(), amount - 3);
		
		int rand = new Random().nextInt(chance.get(player.getName()));
		
		if (rand == 1) {
			chance.put(player.getName(), chance.get(player.getName()) + 1);
			
			player.sendMessage("§6Diamonds now have a 1/" + chance.get(player.getName()) + " chance to drop!");
		} else {
			BlockUtils.blockBreak(player, block);
			BlockUtils.degradeDurabiliy(player);
			
			event.setCancelled(true);
			block.setType(Material.AIR);
		}
    }
}