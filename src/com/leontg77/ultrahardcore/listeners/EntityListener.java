package com.leontg77.ultrahardcore.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityMountEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Paranoia;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshots;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshotsPlus;
import com.leontg77.ultrahardcore.scenario.scenarios.TeamHealth;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Entity listener class.
 * <p> 
 * Contains all eventhandlers for entity releated events.
 * 
 * @author LeonTG77
 */
public class EntityListener implements Listener {
	private Game game = Game.getInstance();
	 
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		SpawnReason reason = event.getSpawnReason();
		Entity entity = event.getEntity();
		
		Location loc = event.getLocation();
		World world = loc.getWorld();
		
		Biome biome = loc.getBlock().getBiome();
		
		if (world.getName().equals("lobby") || world.getName().equals("arena")) {
			if (reason != SpawnReason.CUSTOM) {
				event.setCancelled(true);
			}
			return;
		}
		
		if (world.getGameRuleValue("doMobSpawning").equals("false") && reason == SpawnReason.NATURAL) {
			event.setCancelled(true);
			return;
		}
		
		if (biome.equals(Biome.JUNGLE_EDGE)) {
			event.setCancelled(new Random().nextBoolean());
		}
		
		if (entity instanceof Wolf) {
			entity.setCustomName("Wolf");
			return;
		}
		
		if (entity instanceof Rabbit || entity instanceof Sheep) {
			if (biome.equals(Biome.FOREST) || biome.equals(Biome.FOREST_HILLS)) {
				Wolf wolf = loc.getWorld().spawn(loc, Wolf.class);
				wolf.setCustomName("Wolf");
				
				event.setCancelled(true);
			}
			return;
		}
	}
	
	@EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
    	LivingEntity entity = event.getEntity();
    	Player killer = entity.getKiller();
    	
    	if (killer != null) {
    		User user = User.get(killer);
    		
    		if (entity instanceof Monster) {
    			user.increaseStat(Stat.HOSTILEMOBKILLS);
    		}
    		
    		if (entity instanceof Animals) {
    			user.increaseStat(Stat.ANIMALKILLS);
    		}
    	}
    	
    	if (entity instanceof Ghast && game.ghastDropGold()) {
    		for (ItemStack drop : event.getDrops()) {
    			if (drop.getType() == Material.GHAST_TEAR) {
    				drop.setType(Material.GOLD_INGOT);
    			}
    		}
    		return;
        }
    	
    	if (entity instanceof Creeper) {
    		Creeper creeper = (Creeper) entity;
    		
    		if (creeper.isPowered()) {
    			event.setDroppedExp(0);
    		}
    		return;
    	}
    	
    	if (game.isRecordedRound()) {
    		return;
    	}
        	
    	ItemStack potion = new ItemStack (Material.POTION, 1, (short) 8261);
    	List<ItemStack> drops = event.getDrops();
    	
    	if (!(entity instanceof Witch)) {
    		return;
        }
		
		if (killer == null) {
			return;
		}
		
		drops.remove(potion);
		
		if (killer.hasPotionEffect(PotionEffectType.POISON)) {
			drops.add(potion);
		} 
		else {
			Random rand = new Random();
			
			if (rand.nextInt(99) < 30) {
				drops.add(potion);
			}
		}
	}
	
	@EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
		RegainReason reason = event.getRegainReason();
		Entity entity = event.getEntity();
		
        if (entity instanceof Player) {
            if (reason == RegainReason.REGEN || reason == RegainReason.SATIATED) {
                event.setCancelled(true);
            }
        }
    }
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		
		if (State.isState(State.SCATTER)) {
			event.setCancelled(true);
			return;
		}
    	
		if (entity instanceof Player || entity instanceof Minecart || entity instanceof ArmorStand || entity instanceof Painting || entity instanceof ItemFrame) {
			if (entity.getWorld().getName().equals("lobby")) {
	    		event.setCancelled(true);
	    		return;
	    	}
			
			if (entity instanceof Player) {
				final Player player = (Player) entity;
				final double olddamage = player.getHealth();

				new BukkitRunnable() {
					public void run() {
						double damage = olddamage - player.getHealth();
						
						User user = User.get(player);
						Game game = Game.getInstance();
						
						if (game.isRecordedRound() || GameUtils.getHostName(game.getHost()).equalsIgnoreCase("LeonsPrivate")) {
							return;
						}
						
						if (!State.isState(State.INGAME)) {
							return;
						}
						
						String statName = Stat.DAMAGETAKEN.name().toLowerCase();
						int current = user.getFile().getInt("stats." + statName, 0);
						
						user.getFile().set("stats." + statName, current + damage);
						user.saveFile();
					}
				}.runTaskLater(Main.plugin, 1);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity attacker = event.getDamager();
		
		if (!(attacker instanceof EnderPearl)) {
			return;
		}

		if (game.pearlDamage()) {
			event.setDamage(2);
			return;
		}
		
		event.setCancelled(true);
	}
	
	/**
	 * @author D4mnX
	 */
	@EventHandler
    public void onStrengthDamage(EntityDamageByEntityEvent event) {
		Entity attacker = event.getDamager();
		
        if (attacker instanceof Player) {
        	Player damager = (Player) attacker;
        	
        	if (game.nerfedStrength()) {
		        if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			        int amplifier = 0;
			        
		        	for (PotionEffect effect : damager.getActivePotionEffects()) {
			            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
			                amplifier = effect.getAmplifier() + 1;
			                break;
			            }
			        }

			        double damageWOS = event.getDamage() / (1 + (amplifier * 1.3));
			        double damageWNS = damageWOS + amplifier * 3;

			        event.setDamage(damageWNS);
		        }
	        }
		}
    }
	
	@EventHandler(ignoreCancelled = true)
	public void onShot(EntityDamageByEntityEvent event) {
		Entity attacked = event.getEntity();
		Entity attacker = event.getDamager();
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
    	if (game.isRecordedRound() || scen.getScenario(TeamHealth.class).isEnabled() || scen.getScenario(Paranoia.class).isEnabled()) {
			return;
		}
    	
		if (attacked instanceof Player && attacker instanceof Arrow) {
			final Player player = (Player) attacked;
			final Arrow arrow = (Arrow) attacker;
			
			if (arrow.getShooter() instanceof Player) {
				new BukkitRunnable() {
					public void run() {
						Player killer = (Player) arrow.getShooter();
						
						double health = player.getHealth();
						String percent = NumberUtils.makePercent(health);
						
						if (health > 0.0000) {
							killer.sendMessage(Main.PREFIX + "§6" + player.getName() + " §7is now at §a" + percent + "%");
						}
					}
				}.runTaskLater(Main.plugin, 1);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLongshot(EntityDamageByEntityEvent event) {
		Entity attacked = event.getEntity();
		Entity attacker = event.getDamager();
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
    	if (game.isRecordedRound() || scen.getScenario(RewardingLongshots.class).isEnabled() || scen.getScenario(RewardingLongshotsPlus.class).isEnabled()) {
			return;
		}
    	
		if (attacked instanceof Player && attacker instanceof Arrow) {
			Player player = (Player) attacked;
			Arrow arrow = (Arrow) attacker;
			
			if (arrow.getShooter() instanceof Player) {
				Player killer = (Player) arrow.getShooter();
				double distance = killer.getLocation().distance(player.getLocation());
				
				if (distance >= 50) {
					PlayerUtils.broadcast(Main.PREFIX + "§6" + killer.getName() + " §7got a longshot of §6" + NumberUtils.convertDouble(distance) + " §7blocks.");
				}
			}
		}
	}

	/**
	 * @author ghowden
	 */
    @EventHandler(ignoreCancelled = true)
    public void onEntityMount(EntityMountEvent event) {
    	if (game.horseArmor()) {
    		return;
    	}
    	
        if (event.getEntityType() != EntityType.PLAYER || event.getMount().getType() != EntityType.HORSE) {
        	return;
        }

        Horse horse = (Horse) event.getMount();
        ItemStack armor = horse.getInventory().getArmor();

        if (armor != null && armor.getType() != Material.AIR) {
            event.getEntity().sendMessage(Main.PREFIX + "Dropped horse's armour on the ground as it is disabled.");
            horse.getInventory().setArmor(null);
            
            BlockUtils.dropItem(horse.getLocation(), armor);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
    	Player player = (Player) event.getOwner();
    	User user = User.get(player);
    	
    	if (event.getEntity() instanceof Wolf) {
    		user.increaseStat(Stat.WOLVESTAMED);
    		return;
    	}
    	
    	if (event.getEntity() instanceof Horse) {
    		user.increaseStat(Stat.HORSESTAMED);
    	}
    }
}