package com.leontg77.ultrahardcore.feature.entity;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.events.uhc.MeetupEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.EntityUtils;

/**
 * Mob Rates feature class.
 *  
 * @author LeonTG77
 */
public class MobRatesFeature extends Feature implements Listener {

	public MobRatesFeature() {
		super("Mob Rates", "Lower mob rates by how many people are online, lower mob rates in jungles and fix meetup mobs.");
	}

	@EventHandler
	public void on(final MeetupEvent event) {
		for (World world : game.getWorlds()) {
    		for (Entity mob : world.getEntities()) {
    			final EntityType type = mob.getType();
    			
    			if (type == EntityType.DROPPED_ITEM || type == EntityType.EXPERIENCE_ORB) {
    				continue;
    			}
    			
				if (!EntityUtils.isButcherable(type)) {
					continue;
				}
				
				mob.remove();
    		}
       	}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(final CreatureSpawnEvent event) {
		final Entity entity = event.getEntity();
		final EntityType type = entity.getType();
		
		final Location loc = event.getLocation();
		final World world = loc.getWorld();
		
		final SpawnReason reason = event.getSpawnReason();
		final Biome biome = loc.getBlock().getBiome();
		
		if (world.getName().equals("lobby") || world.getName().equals("arena")) {
			switch (reason) {
			case BUILD_SNOWMAN:
			case CUSTOM:
			case SPAWNER:
			case SPAWNER_EGG:
				return;
			default:
				event.setCancelled(true);
				break;
			}
			return;
		}
		
		// vanilla doesn't do a good job on this.
		if (world.getGameRuleValue("doMobSpawning").equals("false")) {
			switch (reason) {
			case CHUNK_GEN:
			case DEFAULT:
			case DISPENSE_EGG:
			case EGG:
			case NATURAL:
			case OCELOT_BABY:
			case VILLAGE_DEFENSE:
			case VILLAGE_INVASION:
				event.setCancelled(true);
				return;
			default:
				break;
			}
			return;
		}
		
		final Timers timer = Timers.getInstance();
		final Random rand = new Random();
		
		if (timer.getMeetup() <= 0) {
			switch (reason) {
			case CHUNK_GEN:
			case DEFAULT:
			case DISPENSE_EGG:
			case EGG:
			case INFECTION:
			case NATURAL:
			case NETHER_PORTAL:
			case OCELOT_BABY:
			case REINFORCEMENTS:
			case SILVERFISH_BLOCK:
			case SLIME_SPLIT:
			case SPAWNER:
			case VILLAGE_DEFENSE:
			case VILLAGE_INVASION:
				event.setCancelled(true);
				return;
			default:
				break;
			}
			return;
		}
		
		switch (reason) {
		case BREEDING:
		case BUILD_IRONGOLEM:
		case BUILD_SNOWMAN:
		case BUILD_WITHER:
		case CUSTOM:
		case INFECTION:
		case JOCKEY:
		case LIGHTNING:
		case MOUNT:
		case SLIME_SPLIT:
		case SPAWNER:
		case SPAWNER_EGG:
			return;
		default:
			break;
		}
		
		// since all jungles are replaced by jungle edge it spawns A LOT more mobs...
		if (biome.equals(Biome.JUNGLE_EDGE) && rand.nextBoolean()) {
			event.setCancelled(true);
			return;
		}

		// limit mob spawning by how many are online.
		
		final int count = Bukkit.getOnlinePlayers().size();
		boolean shouldCancel = false;
		
		switch (type) {
		case SQUID:
		case BAT:
			if (count > 60) {
				shouldCancel = rand.nextBoolean();
			}
			
			if (count > 80) {
				shouldCancel = true;
			}
			break;
		case BLAZE:
			if (count > 90) {
				shouldCancel = rand.nextBoolean();
			}
			
			if (count > 110) {
				shouldCancel = true;
			}
			break;
		case ENDERMITE:
		case HORSE:
		case PIG:
			if (count > 100) {
				shouldCancel = rand.nextBoolean();
			}
			
			if (count > 120) {
				shouldCancel = true;
			}
			break;
		case RABBIT:
		case SHEEP:
			if (count > 80) {
				shouldCancel = rand.nextBoolean();
			}
			
			if (count > 100) {
				shouldCancel = true;
			}
			break;
		case MAGMA_CUBE:
		case SLIME:
			if (count > 80) {
				shouldCancel = rand.nextBoolean();
			}
			
			if (count > 100) {
				shouldCancel = true;
			}
			break;
		case CREEPER:
		case ENDERMAN:
		case PIG_ZOMBIE:
		case SKELETON:
		case GUARDIAN:
		case GHAST:
		case SPIDER:
		case WITCH:
		case CAVE_SPIDER:
		case ZOMBIE:
			if (count > 80) {
				shouldCancel = rand.nextBoolean();
			}
			
			if (count > 110) {
				shouldCancel = true;
			}
			break;
		default:
			shouldCancel = false;
			break;
		}
		
		event.setCancelled(shouldCancel);
	}
}