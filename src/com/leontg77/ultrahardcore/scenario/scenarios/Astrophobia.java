package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Astrophobia scenario class
 * 
 * @author Bergasms
 */
public class Astrophobia extends Scenario implements Listener {
	private Game game = Game.getInstance();
	
	private long tickInterval = 100L;
	private int astroTask = -1;
	private int frequency = 5;
	private int fuse = 150;
	
	private float chancePerDiamond = 10.0F;
	private float chancePerGold = 30.0F;
	private float chancePerIron = 60.0F;

	public Astrophobia() {
		super("Astrophobia", "The sun is gone, and the world is in eternal night. Deadly meteors impact the surface at random, leaving craters and flames, and sometimes ores. Aliens arrive from the sky wearing protective armor and shooting powerful weapons. Their tracking bombs (charged creepers) are fired onto the world to target any players that come near them. After defeating one, players can spawn a charged creeper of their own.");
	}

	@Override
	public void onDisable() {
		Game game = Game.getInstance();
		game.getWorld().setGameRuleValue("doDaylightCycle", "true");

		if (astroTask != -1) {
			Bukkit.getScheduler().cancelTask(astroTask);
		}
		
		astroTask = -1;
	}
	
	@Override
	public void onEnable() {
		Game game = Game.getInstance();
		
		game.getWorld().setGameRuleValue("doDaylightCycle", "false");
		game.getWorld().setTime(18000);
		
		astroTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for (int i = 0; i < frequency; i++) {
					dropAstroItem();
				}
			}
		}, 12000, tickInterval);
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		IgniteCause cause = event.getCause();

		if (cause == IgniteCause.SPREAD) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityShootBowEvent(EntityShootBowEvent event) {
		Entity proj = event.getProjectile();
		Entity entity = event.getEntity();
		
		if (event.isCancelled()) {
			return;
		}
		
		if (game.getWorld() == null) {
			return;
		}
		
		if (!(proj instanceof Arrow)) {
			return;
		}
		
		if (!(entity instanceof Skeleton)) {
			return;
		}
		
		Skeleton skelly = (Skeleton) entity;
		
		if (skelly.getEquipment().getHelmet().getType() != Material.GLASS) {
			return;
		}
		
		Firework f = (Firework) game.getWorld().spawnEntity(event.getEntity().getEyeLocation(), EntityType.FIREWORK);
		FireworkMeta fwm = f.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder().withTrail().with(FireworkEffect.Type.STAR).withColor(new Color[] { Color.GREEN, Color.GREEN }).with(FireworkEffect.Type.BURST).build();
		
		fwm.addEffect(effect);
		f.setFireworkMeta(fwm);
		proj.setPassenger(f);
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		
		if (!(damager instanceof Arrow)) {
			return;
		}
		
		if (damager.getPassenger() == null) {
			return;
		}
		
		Firework fw = (Firework) damager.getPassenger();
		FireworkMeta fwMeta = fw.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder().withTrail().with(FireworkEffect.Type.BALL_LARGE).withColor(new Color[] { Color.RED, Color.RED }).with(FireworkEffect.Type.BURST).build();
		
		fwMeta.addEffect(effect);
		fw.setFireworkMeta(fwMeta);
		fw.detonate();
		
		event.setDamage(6.0D);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		
		if (!entity.hasMetadata("meteor")) {
			return;
		}
		
		final Location loc = event.getLocation();

		new BukkitRunnable() {
			public void run() {
				for (Entity near : PlayerUtils.getNearby(loc, 10)) {
					if (near.getType() != EntityType.DROPPED_ITEM) {
						continue;
					}
					
					Item item = (Item) near;
					Material type = item.getItemStack().getType();
					
					if (type == Material.COBBLESTONE || type == Material.SAND || type == Material.SANDSTONE || type == Material.DIRT || type == Material.GRAVEL || type == Material.SAPLING || type == Material.LOG || type == Material.LOG_2 || type == Material.SEEDS) {
						near.remove();
					}
				}
				
				Random r = new Random();
				
				if (r.nextBoolean()) {
					return;
				}
				
				float fr = r.nextFloat() * 100.0F;
				
				if (fr < chancePerDiamond) {
					BlockUtils.dropItem(loc, new ItemStack(Material.DIAMOND, 1));
					return;
				} 
				
				if (fr < chancePerDiamond + chancePerGold) {
					BlockUtils.dropItem(loc, new ItemStack(Material.GOLD_ORE, 2));
					return;
				} 
				
				if (fr < chancePerDiamond + chancePerGold + chancePerIron) {
					BlockUtils.dropItem(loc, new ItemStack(Material.IRON_ORE, 2));
				}
			}
		}.runTaskLater(Main.plugin, 2L);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Monster && Timers.time < 10 && entity.getLocation().getY() > 60) {
			event.setCancelled(true);
			return;
		}
		
		if (!(entity instanceof Creeper)) {
			return;
		}
		
		if (event.getSpawnReason() != SpawnReason.SPAWNER_EGG) {
			return;
		}
		
		Creeper creep = (Creeper) event.getEntity();
		
		creep.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 10), true);
		creep.setPowered(true);

		creep.getEquipment().setItemInHand(new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
		creep.setCanPickupItems(false);
		creep.getEquipment().setItemInHandDropChance(100.0F);
	}

	private void dropAstroItem() {
		Chunk[] chunks = game.getWorld().getLoadedChunks();

		Random r = new Random();
		
		if (chunks.length == 0) {
			return;
		}
			
		Chunk dropChunk = chunks[r.nextInt(chunks.length)];
		Location dropFrom = new Location(dropChunk.getWorld(), dropChunk.getX() * 16 + r.nextInt(16), 255.0D, dropChunk.getZ() * 16 + r.nextInt(15));

		int chance = r.nextInt(100);

		if (chance < 70) {
			TNTPrimed tnt = (TNTPrimed) game.getWorld().spawn(dropFrom, TNTPrimed.class);
			
			tnt.setVelocity(new Vector((r.nextFloat() * 10.0F - 5.0F) / 2.0F, -1.0F * r.nextFloat(), (r.nextFloat() * 10.0F - 5.0F) / 2.0F));
			tnt.setFallDistance(150.0F);
			tnt.setFuseTicks(fuse);
			
			tnt.setMetadata("meteor", new FixedMetadataValue(Main.plugin, "isMeteor"));
			
			tnt.setIsIncendiary(r.nextBoolean());
			tnt.setYield(Math.max((float) ((1000.0D - dropFrom.distance(new Location(game.getWorld(), 0.0D, 63.0D, 0.0D))) / 100.0D), 7.0F));
			return;
		} 
		
		if (chance < 85) {
			Creeper creep = (Creeper) game.getWorld().spawnEntity(dropFrom.subtract(0.0D, 155.0D, 0.0D), EntityType.CREEPER);
			creep.setPowered(true);
			creep.setFallDistance(-500.0F);
			creep.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 10), true);
			creep.setVelocity(new Vector(r.nextFloat() * 10.0F - 5.0F, -1.0F * r.nextFloat(), r.nextFloat() * 10.0F - 5.0F));

			creep.getEquipment().setItemInHand(new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
			creep.setCanPickupItems(false);
			creep.getEquipment().setItemInHandDropChance(100.0F);
			return;
		} 
		
		if (chance < 100) {
			Vector cel = new Vector(r.nextFloat() * 10.0F - 5.0F, -1.0F * r.nextFloat(), r.nextFloat() * 10.0F - 5.0F);
			
			for (int i = 2; i < 8; i++) {
				Skeleton creep = (Skeleton) game.getWorld().spawnEntity(dropFrom.subtract(0.0D, 155.0D, 0.0D), EntityType.SKELETON);
				creep.setFallDistance(-500.0F);
				creep.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999, 10), true);
				creep.setCanPickupItems(false);

				ItemStack entitychest = new ItemStack(Material.LEATHER_CHESTPLATE);
				ItemStack entityleg = new ItemStack(Material.LEATHER_LEGGINGS);
				ItemStack entityboots = new ItemStack(Material.LEATHER_BOOTS);

				ItemStack[] items = { entitychest, entityleg, entityboots };
				String[] names = { "Space Suit", "Space Pants", "Space Boots" };
				Color[] colours = { Color.GRAY, Color.GRAY, Color.GRAY };
				
				int ind = 0;
				
				for (ItemStack item : items) {
					LeatherArmorMeta lma = (LeatherArmorMeta) item.getItemMeta();
					lma.setColor(colours[ind]);
					lma.setDisplayName(names[ind]);
					item.setItemMeta(lma);
					ind++;
				}
				
				creep.getEquipment().setArmorContents(items);

				ItemStack entityhelm = new ItemStack(Material.GLASS);
				creep.getEquipment().setHelmet(entityhelm);
				creep.setVelocity(cel);

				creep.getEquipment().setItemInHand(new ItemStack(Material.BOW));
			}
		}
	}
}