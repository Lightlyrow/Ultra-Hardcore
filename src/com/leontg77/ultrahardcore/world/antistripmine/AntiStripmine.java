package com.leontg77.ultrahardcore.world.antistripmine;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Main;

/**
 * Anti stripmine class.
 * 
 * @author XHawk87, modified by LeonTG77.
 */
public class AntiStripmine {
	private final Main plugin;
	
	private final int removalFactor;
	private final int maxHeight;
	
	private final Material oreReplacer;

	/**
	 * Anti stripmine class constructor.
	 * 
	 * @param plugin The main class.
	 */
	public AntiStripmine(Main plugin) {
		this.plugin = plugin;
		
		this.removalFactor = 100;
		this.maxHeight = 32;
		
		this.oreReplacer = Material.STONE;
	}

	/**
	 * All ores that should be removed outside of caves.
	 */
	private static final Set<Material> ORES_TO_REMOVE = ImmutableSet.of(
			Material.DIAMOND_ORE, Material.GOLD_ORE, Material.LAPIS_ORE
	);
	
	private Deque<ChunkOreRemover> queue = new ArrayDeque<ChunkOreRemover>();
	
	private Set<ChunkOreRemover> checked = new HashSet<ChunkOreRemover>();
	private Map<String, WorldData> worlds = new HashMap<String, WorldData>();

	private BukkitTask tick = null;

	/**
	 * Queue a check for the given chunk ore remover.
	 * 
	 * @param remover The chunk ore remover to use.
	 */
	public void queue(ChunkOreRemover remover) {
		queue.add(remover);
		
		if (tick == null) {
			tick = Bukkit.getScheduler().runTask(plugin, new Runnable() {
				public void run() {
					long started = System.nanoTime();
					
					while (!queue.isEmpty() && (System.nanoTime() - started < 45000000L)) {
						queue.pop().run();
					}
					
					if (queue.isEmpty()) {
						tick = null;
					} else {
						tick = Bukkit.getScheduler().runTask(plugin, this);
					}
				}
			});
		}
		
		checked.add(remover);
	}

	/**
	 * Register the given world and make it remove all ores outside of caves.
	 * 
	 * @param world The world to register.
	 * @return The newly created world data.
	 */
	public WorldData registerWorld(World world) {
		WorldData worldData = new WorldData(plugin, this, world);
		worldData.load();
		
		worlds.put(world.getName(), worldData);
		return worldData;
	}

	/**
	 * Get the world data for the given world.
	 * 
	 * @param world The world to get for.
	 * @return The data, it will register a new data if not found.
	 */
	public WorldData getWorldData(World world) {
		WorldData data = worlds.get(world.getName());
		
		if (data == null) {
			data = registerWorld(world);
		}
		
		return data;
	}

	/**
	 * Display all world data stats to the given sender.
	 * 
	 * @param sender The sender to display for.
	 */
	public void displayStats(CommandSender sender) {
		for (WorldData data : worlds.values()) {
			data.displayStats(sender);
		}
	}

	/**
	 * Check if the given chunk ore remover has been checked for ores.
	 * 
	 * @param remover The chunk ore remover to check for.
	 * @return True if it has, false otherwise.
	 */
	public boolean wasChecked(ChunkOreRemover remover) {
		return checked.contains(remover);
	}

	/**
	 * Get the max height the ore checker will use.
	 * 
	 * @return The max height.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}
	
	/**
	 * Get the removal factor the ore checker will use.
	 * 
	 * @return The removal factor.
	 */
	public int getRemovalFactor() {
		return removalFactor;
	}

	/**
	 * Get the block type to replace ores outside of caves with.
	 * 
	 * @return The block type.
	 */
	public Material getOreReplacer() {
		return oreReplacer;
	}

	/**
	 * Get a list of all ores that should be removed if they're not in a cave.
	 * 
	 * @return A list of ores to remove.
	 */
	public Set<Material> getOresToRemove() {
		return ORES_TO_REMOVE;
	}
}