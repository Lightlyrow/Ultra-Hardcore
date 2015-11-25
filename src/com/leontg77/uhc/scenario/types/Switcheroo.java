package com.leontg77.uhc.scenario.types;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.uhc.scenario.Scenario;

/**
 * Switcheroo scenario class
 * 
 * @author LeonTG77
 */
public class Switcheroo extends Scenario implements Listener {

	public Switcheroo() {
		super("Switcheroo", "When you shoot someone, you trade places with them");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player player = (Player) entity;
		
		if (!(damager instanceof Projectile)) {
			return;
		}
		
		Projectile proj = (Projectile) damager;
		
		if (!(proj.getShooter() instanceof Player)) {
			return;
		}
		
		Player shooter = (Player) proj.getShooter();
		
		Location loc1 = player.getLocation();
		Location loc2 = shooter.getLocation();

		shooter.teleport(loc1);
		player.teleport(loc2);
	}
}