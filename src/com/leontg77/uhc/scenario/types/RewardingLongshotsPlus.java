package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * RewardingLongshots+ scenario class
 * 
 * @author LeonTG77
 */
public class RewardingLongshotsPlus extends Scenario implements Listener {
	private boolean enabled = false;
	
	public RewardingLongshotsPlus() {
		super("RewardingLongshots+", "When shooting and hitting people with a bow from a variable distance, you will be rewarded with various different items.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
			return;
		}
	
		Player player = (Player) event.getEntity();
		Arrow damager = (Arrow) event.getDamager();
		
		if (!(damager.getShooter() instanceof Player)) {
			return;
		}
		
		Player killer = (Player) damager.getShooter();
		double distance = killer.getLocation().distance(player.getLocation());

		PlayerUtils.broadcast("§7[§9Rewarding Longshots+§7] §r" + killer.getName() + "just got a longshot of " + NumberUtils.convertDouble(distance) + " metres!");
		
		if (distance >= 30 && distance <= 49) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 8));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
		}

		if (distance >= 50 && distance <= 99) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 16));
			PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			return;
		} 

		if (distance >= 100 && distance <= 199) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.BOW));
			PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 32));
			PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND));
			return;
		}

		if (distance >= 200) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.BOW, 2));
			PlayerUtils.giveItem(killer, new ItemStack(Material.ARROW, 64));
			PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT, 3));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT, 2));
			PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND, 5));
		}
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		
	}
}