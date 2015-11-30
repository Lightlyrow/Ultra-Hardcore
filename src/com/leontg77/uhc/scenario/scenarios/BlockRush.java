package com.leontg77.uhc.scenario.scenarios;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.BlockUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * BlockRush scenario class
 * 
 * @author LeonTG77
 */
public class BlockRush extends Scenario implements Listener {
	private HashSet<String> mined = new HashSet<String>();

	public BlockRush() {
		super("BlockRush", "Mining a specific block type for the first time drops 1 gold ingot.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		short durability = block.getState().getData().toItemStack().getDurability();
		String name = block.getType().name();
		
		String combined = name + durability;
		
		if (mined.contains(combined)) {
			return;
		}
		
		PlayerUtils.broadcast("§6" + player.getName() + " §7was the first player to break " + name.toLowerCase().replaceAll("_", " ") + (durability > 0 ? ":" + durability : ""));
		mined.add(combined);
		
		BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GOLD_INGOT));
	}
}