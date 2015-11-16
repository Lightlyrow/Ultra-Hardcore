package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.LocationUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Halloween scenario class
 * 
 * @author LeonTG77
 */
public class Halloween extends Scenario implements Listener {
	private BukkitRunnable task;
	
	public Halloween() {
		super("Halloween", "Random lightnining strikes around the player at random intervals causing bats to spawn around you and giving you nausea for 10 seconds if you get hit. Getting hit by a hostile mob causes 15 seconds of blindness. All spiders are replaced with cave spiders, witch rates are upped at last Zombies and Skellies spawn with jack o' lanterns.");
	}

	@Override
	public void onDisable() {
		task.cancel();
	}

	@Override
	public void onEnable() {
		task = new BukkitRunnable() {
			public void run() {
				Random ran = new Random();
				
				for (Player online : PlayerUtils.getPlayers()) {
					Location loc = online.getLocation();
					
					if (!GameUtils.getGameWorlds().contains(loc.getWorld())) {
						continue;
					}
					
					int diffX = ran.nextInt(25 * 2) - 25;
					int diffZ = ran.nextInt(25 * 2) - 25;

					final Location hitLoc = loc.clone().add(diffX, 0, diffZ);
					hitLoc.setY(LocationUtils.highestTeleportableYAtLocation(hitLoc) + 1);
					
					hitLoc.getWorld().strikeLightning(hitLoc);
					
					new BukkitRunnable() {
						public void run() {
							hitLoc.setY(LocationUtils.highestTeleportableYAtLocation(hitLoc) + 1);
							
							for (int i = 0; i < 15; i++) {
								hitLoc.getWorld().spawn(hitLoc, Bat.class).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
							}
						}
					}.runTaskLater(Main.plugin, 15);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 600, 700);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Zombie || entity instanceof Skeleton) {
			LivingEntity living = (LivingEntity) entity;
			
			living.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
			return;
		}
		
		if (entity.getType() == EntityType.SPIDER) {
			event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.CAVE_SPIDER);
			event.setCancelled(true);
			return;
		}
		
		if (entity instanceof Witch) {
			return;
		}
		
		if (entity instanceof Bat) {
			return;
		}
		
		if (new Random().nextDouble() > 0.80) {
			event.getLocation().getWorld().spawn(event.getLocation(), Witch.class);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		DamageCause cause = event.getCause();
		
		if (cause != DamageCause.LIGHTNING) {
			return;
		}
		
		event.setDamage(event.getDamage() / 2);
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		
		if (entity instanceof Player && damager instanceof Monster) {
			Player player = (Player) entity;

			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0));
		}
	}
}