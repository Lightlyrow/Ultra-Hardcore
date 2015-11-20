package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.scenario.Scenario;

/**
 * NightmareMode scenario class
 * 
 * @author LeonTG77
 */
public class NightmareMode extends Scenario implements Listener {
	
	public NightmareMode() {
		super("NightmareMode", "Variety of changes to mobs to make them more difficult.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		LivingEntity entity = event.getEntity();
		
		if (entity instanceof Witch) {
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 4));
			return;
		} 
		
		if (entity instanceof Spider) {
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 3));
			return;
		} 
		
		if (entity instanceof Zombie) {
			entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 1));
			return;
		} 
		
		if (entity instanceof Creeper) {
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 1));
		} 
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		DamageCause cause = event.getCause();
		Entity entity = event.getEntity();
		
		Random rand = new Random();
		
		if (entity instanceof Zombie) {
			if (cause != DamageCause.FIRE && cause != DamageCause.FIRE_TICK) {
				return;
			}

			event.setCancelled(rand.nextBoolean());
			return;
		}
		
		if (entity instanceof Enderman) {
			if (rand.nextDouble() > 0.10) {
				return;
			}
			
			entity.getWorld().spawn(entity.getLocation(), Blaze.class);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player player = (Player) entity;
		
		if (damager instanceof Arrow) {
			Arrow arrow = (Arrow) damager;
			
			if (arrow.getShooter() instanceof Skeleton) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
			}
			return;
		}
		
		if (damager instanceof Enderman) {
			player.setFireTicks(60);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Creeper)) {
			return;
		}
		
		for (int i = 0; i < 5; i++) {
			entity.getWorld().spawn(entity.getLocation(), Silverfish.class);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		
		if (entity instanceof Creeper) {
			Random rand = new Random();
			
			if (rand.nextBoolean()) {
				entity.getWorld().spawn(entity.getLocation(), Silverfish.class);
			}
			return;
		} 
		
		if (entity instanceof Zombie) {
			Skeleton skelly = entity.getWorld().spawn(entity.getLocation(), Skeleton.class);
			skelly.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
			skelly.getEquipment().setItemInHand(new ItemStack (Material.STONE_SWORD));
			return;
		} 
		
		if (entity instanceof Spider) {
			Location loc = entity.getLocation();
			Block block = loc.getBlock();

			block.getRelative(BlockFace.NORTH_EAST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.NORTH_WEST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.SOUTH_EAST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.SOUTH_WEST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.SOUTH).setType(Material.WEB);
			block.getRelative(BlockFace.NORTH).setType(Material.WEB);
			block.getRelative(BlockFace.EAST).setType(Material.WEB);
			block.getRelative(BlockFace.WEST).setType(Material.WEB);
		}
	}
}