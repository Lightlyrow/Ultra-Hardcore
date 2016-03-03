package com.leontg77.ultrahardcore.feature.pvp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Combat tag listener class.
 * 
 * @author LeonTG77
 */
public class CombatLogFeature extends Feature implements Listener {
	private final SpecManager spec = SpecManager.getInstance();
	
	public static Map<UUID, BukkitRunnable> combatTask = new HashMap<UUID, BukkitRunnable>();
	public static Map<UUID, Integer> combat = new HashMap<UUID, Integer>();
	
	public CombatLogFeature() {
		super("Combat Log", "Kills people that log out in combat.");
	}
	  
	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
	    
	    if (!combat.containsKey(player)) {
	    	return;
	    }	
	    
		if (spec.isSpectating(player)) {
			return;
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "§c" + player.getName() + "§7 left while in combat.");
		
		player.setHealth(0.0D);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		final Entity damager = event.getDamager();
		final Entity entity = event.getEntity();
		
		if (!(entity instanceof Player) || !(damager instanceof Projectile || damager instanceof Player)) {
			return;
		}

		final Player player = (Player) entity;
	    
		if (spec.isSpectating(player)) {
			return;
		}
		
		if (damager instanceof Player) {
			final Player killer = (Player) damager;
			
			// shouldn't work if killer is in god mode, fly mode, vanished or creative mode.
			if (spec.isSpectating(killer)) {
				return;
			}
			
			final UUID killerUUID = killer.getUniqueId();
			
			combat.put(killerUUID, 25);
			
			if (!combat.containsKey(killerUUID)) {
				combatTask.put(killerUUID, new BukkitRunnable() {
					public void run() {
						combat.put(killerUUID, combat.get(killerUUID) - 1);
						
						if (combat.get(killerUUID) == 0) {
							combatTask.remove(killerUUID);
							combat.remove(killerUUID);
							
							cancel();
						}
					}
				});
				
				combatTask.get(killerUUID).runTaskTimer(Main.plugin, 20L, 20L);
			}
		} 
		
		if (damager instanceof Projectile) {
			final Projectile proj = (Projectile) damager;
			final ProjectileSource source = proj.getShooter();
			
			if (!(source instanceof Player)) {
				return;
			}
			
			final Player killer = (Player) source;
			
			// shouldn't work if killer is in god mode, fly mode, vanished or creative mode.
			if (spec.isSpectating(killer)) {
				return;
			}
			
			final UUID killerUUID = killer.getUniqueId();
			
			combat.put(killerUUID, 25);
			
			if (!combat.containsKey(killerUUID)) {
				combatTask.put(killerUUID, new BukkitRunnable() {
					public void run() {
						combat.put(killerUUID, combat.get(killerUUID) - 1);
						
						if (combat.get(killerUUID) == 0) {
							combatTask.remove(killerUUID);
							combat.remove(killerUUID);
							
							cancel();
						}
					}
				});
				
				combatTask.get(killerUUID).runTaskTimer(Main.plugin, 20L, 20L);
			}
		}
		
		final UUID playerUUID = player.getUniqueId();
		
		combat.put(playerUUID, 25);
		
		if (!combat.containsKey(playerUUID)) {
			combatTask.put(playerUUID, new BukkitRunnable() {
				public void run() {
					combat.put(playerUUID, combat.get(playerUUID) - 1);
					
					if (combat.get(playerUUID) == 0) {
						combatTask.remove(playerUUID);
						combat.remove(playerUUID);
						
						cancel();
					}
				}
			});
			
			combatTask.get(playerUUID).runTaskTimer(Main.plugin, 20L, 20L);
		}
	}
}