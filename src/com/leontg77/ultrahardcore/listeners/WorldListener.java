package com.leontg77.ultrahardcore.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.leontg77.pregenner.events.WorldBorderFillFinishedEvent;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * World listener class.
 * <p>
 * Contains all eventhandlers for world releated events.
 * 
 * @author LeonTG77
 */
public class WorldListener implements Listener {
	private final Arena arena;

	/**
	 * World listener class constructor.
	 *
	 * @param arena The arena class.
	 */
	public WorldListener(Arena arena) {
		this.arena = arena;
	}
	
	private final Random rand = new Random();

	@EventHandler
	public void on(ChunkUnloadEvent event) {
		if (!State.isState(State.SCATTER)) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void on(WorldBorderFillFinishedEvent event) {
		World world = event.getWorld();

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " clear");

		if (arena.isResetting) {
			PlayerUtils.broadcast(Arena.PREFIX + "Arena reset complete.");

			if (arena.wasEnabled) {
				arena.enable();
			}

			arena.wasEnabled = false;
			arena.isResetting = false;
			return;
		}

		PlayerUtils.broadcast(Main.PREFIX + "Pregen of world '§a" + world.getName() + "§7' finished.");
	}
	
	@EventHandler
	public void on(ChunkPopulateEvent event) {
		World world = event.getWorld();
		Chunk chunk = event.getChunk();
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Block block = chunk.getBlock(x, world.getHighestBlockYAt(chunk.getX() * x, chunk.getZ() * z) + 1, z);
				
				if (block.getBiome() != Biome.FOREST && block.getBiome() != Biome.FOREST_HILLS) {
					return;
				}
				
				if (rand.nextInt(100) >= 30) {
					return;
				}
				
				Location loc = block.getLocation();
				loc.getWorld().generateTree(loc, TreeType.BIG_TREE);
			}
		}
	}
}