package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.NumberUtils;

/**
 * BladChicken scenario class
 * 
 * @author LeonTG77
 */
public class BaldChicken extends Scenario implements Listener {
	
	public BaldChicken() {
		super("BaldChicken", "Chicken's drop no feathers. Skeletons drop 3-5 arrows.");
	}
	
	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Chicken) {
			for (ItemStack drop : event.getDrops()) {
				if (drop.getType() == Material.FEATHER) {
					drop.setType(Material.AIR);
				}
			}
			return;
		}
		
		if (entity instanceof Skeleton) {
			for (ItemStack drop : event.getDrops()) {
				if (drop.getType() == Material.ARROW) {
					drop.setType(Material.AIR);
				}
			}
			
			event.getDrops().add(new ItemStack(Material.ARROW, NumberUtils.randInt(3, 5)));
		}
	}
}