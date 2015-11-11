package com.leontg77.uhc.scenario.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.EntityUtils;

/**
 * Cryophobia scenario class
 * 
 * @author Bergasms
 */
public class Cryophobia extends Scenario implements Listener {
	private int heightCountdown = 150;
	private int levelHeight = 0;
	private int maxheight = 80;
	
	private int heightUpdateTimer;
	private int processTimer;
	
	private boolean[][] biomeLookup = new boolean[500][500];
	private int[][] existingHeights = new int[500][500];
	private Material replaceMaterial = Material.ICE;
	
	private HashMap<String, ChunkProcess> chunkQueue = new HashMap<String, ChunkProcess>();
	private LinkedList<String> priorityQueue = new LinkedList<String>();;
	
	private World world = Game.getInstance().getWorld();

	public Cryophobia() {
		super("Cryophobia", "A layer of ice will rise slowly from the bottom of the map, faster as the game goes on, filling caves and eventually reaching high up above the surface. Breaking ice causes damage and ill effects. Creepers explode into a ball of ice, while skeletons have geared up for the winter. The biome of the entire map has also been switched to a cold taiga, meaning snow falls and water freezes everywhere on the map.");
	}

	@Override
	public void onDisable() {
		if (heightUpdateTimer != -1) {
			Bukkit.getServer().getScheduler().cancelTask(this.heightUpdateTimer);
			heightUpdateTimer = -1;
		}
		
		if (processTimer != -1) {
			Bukkit.getServer().getScheduler().cancelTask(this.processTimer);
			processTimer = -1;
		}
	}

	@Override
	public void onEnable() {
		for (int i = 0; i < 500; i++) {
			for (int j = 0; j < 500; j++) {
				this.existingHeights[i][j] = this.levelHeight;
				this.biomeLookup[i][j] = false;
			}
		}
		
		for (Chunk chunk : world.getLoadedChunks()) {
			chunkLoaded(chunk);
		}
		
		processTimer = -1;
		heightUpdateTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				heightCountdown -= 1;
				
				if (heightCountdown <= 0) {
					if (levelHeight < maxheight) {
						levelHeight += 1;
					} else {
						onDisable();
					}
					
					didRaiseHeight();
					
					if (levelHeight <= 12) {
						heightCountdown = 150;
					} else if (levelHeight <= 20) {
						heightCountdown = 75;
					} else if (levelHeight <= 40) {
						heightCountdown = 60;
					} else {
						heightCountdown = 60;
					}
				}
			}
		}, 20L, 20L);

		didRaiseHeight();
	}

	@EventHandler
	public void onChunkLoadEvent(ChunkLoadEvent event) {
		chunkLoaded(event.getChunk());
	}

	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		chunkUnloaded(event.getChunk());
	}

	@EventHandler
	public void onEntityShootBowEvent(EntityShootBowEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		LivingEntity entity = event.getEntity();
		Entity proj = event.getProjectile();
		
		if (!(entity instanceof Skeleton)) {
			return;
		}
		
		if (!(proj instanceof Arrow)) {
			return;
		}
		
		event.setCancelled(true);
		entity.launchProjectile(Snowball.class, proj.getVelocity());
	}

	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if (event.isCancelled()) {
			return;
		}

		LivingEntity entity = event.getEntity();
		Random rand = new Random();
		
		if (entity instanceof Animals) {
			if (rand.nextInt(10) <= 1) {
				entity.getWorld().spawn(event.getLocation(), Snowman.class);
			}
			
			event.setCancelled(rand.nextBoolean());
			return;
		}

		if (!(entity instanceof Skeleton)) {
			return;
		}
		
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

		ItemStack[] items = { helmet, chestplate, leggings, boots };
		String[] names = { "Beanie", "Parka", "Ski Pants", "Snowboard Boots" };
		Color[] colours = { Color.BLACK, Color.BLUE, Color.BLUE, Color.RED };
		
		int ind = 0;
		
		for (ItemStack item : items) {
			LeatherArmorMeta lma = (LeatherArmorMeta) item.getItemMeta();
			lma.setColor(colours[ind]);
			lma.setDisplayName(names[ind]);
			item.setItemMeta(lma);
			ind++;
		}
		
		entity.getEquipment().setArmorContents(items);
	}

	@EventHandler
	public void onExplodeDeath(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Creeper)) {
			return;
		}
		
		final Location loc = event.getEntity().getLocation();
		final World world = loc.getWorld();

		if (world != this.world) {
			return;
		}
		
		new BukkitRunnable() {
			public void run() {
				Location l = loc;
				
				world.getBlockAt(l).setType(Material.PACKED_ICE);
				world.getBlockAt(l.add(0.0D, 1.0D, 0.0D)).setType(Material.PACKED_ICE);
				world.getBlockAt(l.add(0.0D, -2.0D, 0.0D)).setType(Material.PACKED_ICE);
				world.getBlockAt(l.add(1.0D, 1.0D, 0.0D)).setType(Material.PACKED_ICE);
				world.getBlockAt(l.add(-2.0D, 0.0D, 0.0D)).setType(Material.PACKED_ICE);
				world.getBlockAt(l.add(1.0D, 0.0D, 1.0D)).setType(Material.PACKED_ICE);
				world.getBlockAt(l.add(0.0D, 0.0D, -2.0D)).setType(Material.PACKED_ICE);
				
				l = loc;
				
				world.getBlockAt(l.add(1.0D, -1.0D, 1.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(-1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(-1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(0.0D, 0.0D, -1.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(0.0D, 0.0D, -1.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(1.0D, 0.0D, 0.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(0.0D, 0.0D, 1.0D)).setType(Material.ICE);
				world.getBlockAt(l.add(0.0D, 0.0D, 1.0D)).setType(Material.ICE);
				
				Random rand = new Random();
				int times = 3 + rand.nextInt(2);
				
				for (int i = 0; i < times; i++) {
					l = loc;
					world.getBlockAt(l.add(rand.nextInt(4) - 2, rand.nextInt(2), rand.nextInt(4) - 2)).setType(Material.ICE);
				}
			}
		}.runTaskLater(Main.plugin, 1);
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();

		if (!(damager instanceof Snowball)) {
			return;
		}
		
		if (!(entity instanceof Player)) {
			return;
		}
		
		Snowball snowball = (Snowball) damager;
		ProjectileSource shooter = snowball.getShooter();
		
		if (!(shooter instanceof Skeleton)) {
			return;
		}
		
		event.setDamage(1.0D + new Random().nextFloat());
	}

	@EventHandler
	public void onLeavesDecayEvent(LeavesDecayEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlock();

		if (block.getType() != Material.LEAVES) {
			return;
		}

		short damage = block.getState().getData().toItemStack().getDurability();
		
		if (damage != 1 && damage != 5 && damage != 9 && damage != 13) {
			return;
		}
		
		Random rand = new Random();
		
		if (rand.nextInt(40) == 0) {
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
			item.setVelocity(EntityUtils.randomOffset());
		}
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if (event.getBlock().getType() == Material.ICE && event.getToBlock().getType() == Material.WATER) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		if ((event.getBlock().getType() == Material.ICE) || (event.getBlock().getType() == Material.PACKED_ICE)) {
			Random r = new Random();
			if (r.nextInt(4) == 0) {
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 1));
			}
			if (r.nextInt(8) == 0) {
				event.getPlayer().damage(1.0D);
			}
		}
	}

	protected void didRaiseHeight() {
		priorityQueue.remove("sentinel");
		priorityQueue.addLast("sentinel");
		if (processTimer == -1) {
			processTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
				public void run() {
					processChunkIfRequired();
				}
			}, 1L, 1L);
		}
	}

	protected void processChunkIfRequired() {
		int attempts = 10;
		for (int a = 0; a < attempts; a++) {
			String pQueue = (String) this.priorityQueue.getFirst();
			if (pQueue.equalsIgnoreCase("sentinel")) {
				this.priorityQueue.remove("sentinel");
				a = 100;
				if (this.processTimer != -1) {
					Bukkit.getServer().getScheduler().cancelTask(this.processTimer);
					this.processTimer = -1;
				}
				return;
			}
			ChunkProcess cp = (ChunkProcess) this.chunkQueue.get(pQueue);

			boolean addToMiddle = false;
			if ((cp != null) && (cp.x >= 0) && (cp.x < 500) && (cp.z >= 0) && (cp.z < 500) && (this.existingHeights[cp.x][cp.z] < this.levelHeight)) {
				World w = world;
				if (w != null) {
					Chunk c = w.getChunkAt(cp.x - 250, cp.z - 250);
					if (c != null) {
						int upToHeight = this.existingHeights[cp.x][cp.z];
						if ((cp.stopHeight < this.levelHeight) && (this.levelHeight - upToHeight < 8)) {
							cp.stopHeight = this.levelHeight;
						}
						for (int y = upToHeight; y <= cp.stopHeight; y++) {
							for (int i = 0; i < 16; i++) {
								for (int j = 0; j < 16; j++) {
									if (!c.getBlock(i, y, j).getType().isSolid()) {
										c.getBlock(i, y, j).setType(this.replaceMaterial);
									}
								}
							}
						}
						a = 100;
						this.existingHeights[cp.x][cp.z] = cp.stopHeight;
						int stopAt = cp.stopHeight + 8;
						if (stopAt > this.levelHeight) {
							stopAt = this.levelHeight;
						}
						cp.stopHeight = stopAt;
						if (cp.stopHeight < this.levelHeight) {
							addToMiddle = true;
						}
					}
				}
			}
			this.priorityQueue.removeFirst();
			if (addToMiddle) {
				int idx = this.priorityQueue.size() / 2;
				this.priorityQueue.add(idx, pQueue);
			} else {
				this.priorityQueue.addLast(pQueue);
			}
		}
	}

	public void chunkLoaded(Chunk chunk) {
		if (this.priorityQueue == null) {
			return;
		}
		if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) {
			return;
		}
		if (!chunk.getWorld().getName().equals(this.world.getName())) {
			return;
		}
		int xp = chunk.getX() + 250;
		int zp = chunk.getZ() + 250;
		if (!this.biomeLookup[xp][zp]) {
			this.biomeLookup[zp][zp] = true;
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					chunk.getBlock(i, 128, j).setBiome(Biome.COLD_TAIGA);
				}
			}
		}
		int stopAt = this.existingHeights[xp][zp] + 8;
		if (stopAt > this.levelHeight) {
			stopAt = this.levelHeight;
		}
		ChunkProcess cp = new ChunkProcess(xp, zp, stopAt);
		this.chunkQueue.put(cp.stringRep(), cp);
		this.priorityQueue.add(cp.stringRep());

		this.priorityQueue.remove("sentinel");
		this.priorityQueue.addLast("sentinel");
	}

	public void chunkUnloaded(Chunk chunk) {
		if (this.priorityQueue == null) {
			return;
		}
		if (chunk.getWorld().getEnvironment() != World.Environment.NORMAL) {
			return;
		}
		int xp = chunk.getX() + 250;
		int zp = chunk.getZ() + 250;
		ChunkProcess cp = new ChunkProcess(xp, zp, 0);
		this.chunkQueue.remove(cp.stringRep());
		this.priorityQueue.remove(cp.stringRep());
	}

	private class ChunkProcess {
		public int x;
		public int z;
		public int stopHeight;

		public ChunkProcess(int x, int z, int stopHeight) {
			this.x = x;
			this.z = z;
			this.stopHeight = stopHeight;
		}

		public String stringRep() {
			return this.x + ":" + this.z;
		}
	}
}