package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DamageCycle scenario class
 * 
 * @author LeonTG77
 */
public class DamageCycle extends Scenario implements Listener, CommandExecutor {
	private BukkitRunnable task;
	private DamageType current;
	
	public static final String PREFIX = "§7§lDamageCycle §8» §7";
	
	public DamageCycle() {
		super("DamageCycle", "Every 10 minutes the damage type changes, during the next 10 minutes if you take damage from that type you die.");
		Main main = Main.plugin;
		
		main.getCommand("current").setExecutor(this);
	}

	@Override
	public void onDisable() {
		task.cancel();
		task = null;
	}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void on(GameStartEvent event) {
		current = DamageType.values()[new Random().nextInt(DamageType.values().length)];
        PlayerUtils.broadcast(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will now instant kill you!");
        
		task = new BukkitRunnable() {
			int seconds = 600;
			
			public void run() {
				seconds--;
				
				switch (seconds) {
	            case 300:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in 5 minutes!");
	                break;
	            case 60:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in 1 minute!");
	                break;
	            case 30:
	            case 10:
	            case 5:
	            case 4:
	            case 3:
	            case 2:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in " + seconds + " seconds!");
	                break;
	            case 1:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in 1 second!");
	                break;
	            case 0:
	                current = DamageType.values()[new Random().nextInt(DamageType.values().length)];
	                PlayerUtils.broadcast(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will now instant kill you!");

	                seconds = 600;
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 20, 20);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		DamageCause cause = event.getCause();
		
		if (cause == DamageCause.FALL && current == DamageType.FALLING) {
			event.setDamage(10000);
		}
		
		if (cause == DamageCause.POISON && current == DamageType.POISON) {
			event.setDamage(10000);
		}
		
		if (cause == DamageCause.SUFFOCATION && current == DamageType.SUFFOCATION) {
			event.setDamage(10000);
		}
		
		if ((cause == DamageCause.LAVA || cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK) && current == DamageType.LAVA_AND_FIRE) {
			event.setDamage(10000);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Entity damager = event.getDamager();
		
		if (damager instanceof Zombie && current == DamageType.ZOMBIES) {
			event.setDamage(10000);
		}
		
		if (damager instanceof Creeper && current == DamageType.CREEPERS) {
			event.setDamage(10000);
		}
		
		if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Skeleton && current == DamageType.SKELETONS) {
			event.setDamage(10000);
		}
		
		if ((damager instanceof Spider || damager instanceof CaveSpider) && current == DamageType.SPIDERS) {
			event.setDamage(10000);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "DamageCycle is not enabled.");
			return true;
		}
		
		sender.sendMessage(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will instant kill you!");
		return true;
	}
	
	public enum DamageType {
		ZOMBIES, SPIDERS, FALLING, SKELETONS, CREEPERS, LAVA_AND_FIRE, SUFFOCATION, POISON;
	}
}