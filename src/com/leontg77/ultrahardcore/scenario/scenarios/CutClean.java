package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * CutClean scenario class
 * 
 * @author LeonTG77
 */
public class CutClean extends Scenario implements Listener {
	
	public CutClean() {
		super("CutClean", "No furnaces required! Iron, gold and food drop their cooked variety.");
	}
	
	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		List<ItemStack> drops = event.getDrops();
		Entity entity = event.getEntity();
		
		if (entity instanceof Cow) {
			drops.clear();
			drops.add(new ItemStack(Material.COOKED_BEEF, 3));
			drops.add(new ItemStack(Material.LEATHER));
			return;
		} 
			
		if (entity instanceof Chicken) {
			drops.clear();
			drops.add(new ItemStack(Material.COOKED_CHICKEN, 3));
			drops.add(new ItemStack(Material.FEATHER, 2));
			return;
		}
		
		if (entity instanceof Pig) {
			drops.clear();
			drops.add(new ItemStack(Material.GRILLED_PORK, 3));
			return;
		}
		
		if (entity instanceof Sheep) {
			drops.clear();
			drops.add(new ItemStack(Material.COOKED_MUTTON, 2));
			return;
		}
	
		if (entity instanceof Rabbit) {
			drops.clear();
			drops.add(new ItemStack(Material.COOKED_RABBIT, 1));
			drops.add(new ItemStack(Material.RABBIT_HIDE));
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (block.getDrops(player.getItemInHand()).isEmpty()) {
			return;
		}
		
		if (block.getType() == Material.IRON_ORE) {
			if (scen.getScenario(TripleOres.class).isEnabled()) {
				return;
			}
			
			BlockUtils.blockBreak(player, block);
			BlockUtils.degradeDurabiliy(player);
			BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.IRON_INGOT));

			event.setCancelled(true);
			block.setType(Material.AIR);
			
			ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(1);
			return;
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			if (scen.getScenario(Barebones.class).isEnabled()) {
				return;
			}
			
			if (scen.getScenario(Goldless.class).isEnabled()) {
				return;
			}
			
			if (scen.getScenario(TripleOres.class).isEnabled()) {
				return;
			}
			
			BlockUtils.blockBreak(player, block);
			BlockUtils.degradeDurabiliy(player);
			BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.GOLD_INGOT));

			event.setCancelled(true);
			block.setType(Material.AIR);
			
			ExperienceOrb exp = (ExperienceOrb) block.getWorld().spawn(block.getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(2);
			return;
		}
		
		if (block.getType() == Material.POTATO) {
			BlockUtils.blockBreak(player, block);
			BlockUtils.degradeDurabiliy(player);
			BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.BAKED_POTATO, 1 + new Random().nextInt(2)));

			event.setCancelled(true);
			block.setType(Material.AIR);
		}
	}
}