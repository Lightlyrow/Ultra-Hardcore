package com.leontg77.ultrahardcore.world.antistripmine;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.leontg77.ultrahardcore.world.antistripmine.AntiStripmine.ChunkOreRemover;

public class ChunkOreRemover implements Runnable {
	private static final Random random = new Random();
	private WorldData worldData;
	private int chunkX;
	private int chunkZ;

	public ChunkOreRemover(WorldData worldData, Chunk chunk) {
		this.worldData = worldData;
		this.chunkX = chunk.getX();
		this.chunkZ = chunk.getZ();
	}

	private Block getBlock(Chunk chunk, int dx, int y, int dz) {
		if ((y < 0) || (y > 255)) {
			return null;
		}
		
		if ((dx >= 0) && (dx <= 15) && (dz >= 0) && (dz <= 15)) {
			return chunk.getBlock(dx, y, dz);
		}
		
		int x = chunk.getX() * 16 + dx;
		int z = chunk.getZ() * 16 + dz;
		
		if (chunk.getWorld().isChunkLoaded(x >> 4, z >> 4)) {
			return chunk.getWorld().getBlockAt(x, y, z);
		}
		return null;
	}

	private void allowLinked(Block block, HashMap<Block, HashSet<Block>> toRemove, HashSet<Block> allowed) {
		if (toRemove.containsKey(block)) {
			HashSet<Block> linked = toRemove.get(block);
			toRemove.remove(block);
			allowed.add(block);
			
			for (Block link : linked) {
				allowLinked(link, toRemove, allowed);
			}
		}
	}

	private void addLinked(Block block, HashMap<Block, HashSet<Block>> toRemove, HashSet<Block> vein) {
		if ((toRemove.containsKey(block)) && (!vein.contains(block))) {
			vein.add(block);
			HashSet<Block> linked = toRemove.get(block);
			
			for (Block link : linked) {
				if (link.getType().equals(block.getType())) {
					addLinked(link, toRemove, vein);
				}
			}
		}
	}

	private void addRemainingOres(EnumMap<Material, Integer> remaining, Material type, int size) {
		if (!remaining.containsKey(type)) {
			remaining.put(type, Integer.valueOf(size));
		} else {
			remaining.put(type, Integer.valueOf(((Integer) remaining.get(type)).intValue() + size));
		}
	}

	public void run() {
		long started = System.nanoTime();

		HashMap<Block, HashSet<Block>> toRemove = new HashMap<Block, HashSet<Block>>();
		HashSet<Block> allowed = new HashSet<Block>();
		World world = this.worldData.getWorld();
		Chunk chunk = world.getChunkAt(this.chunkX, this.chunkZ);
		int maxHeight = this.worldData.getMaxHeight();
		int removalFactor = this.worldData.getRemovalFactor();
		EnumSet<Material> excludedOres = this.worldData.getExcluded();
		Material oreReplacer = this.worldData.getOreReplacer();
		EnumSet<Material> ores = this.worldData.getOres();
		boolean hasNoOres = true;
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y <= maxHeight; y++) {
					Block block = getBlock(chunk, x, y, z);
					if (block != null) {
						if (ores.contains(block.getType())) {
							hasNoOres = false;
							HashSet<Block> nearOres = new HashSet<Block>();
							
							for (int dx = -1; dx <= 1; dx++) {
								for (int dy = -1; dy <= 1; dy++) {
									for (int dz = -1; dz <= 1; dz++) {
										if ((dx != 0) || (dy != 0) || (dz != 0)) {
											Block near = getBlock(chunk, x + dx, y + dy, z + dz);
											
											if (near != null) {
												if (ores.contains(near.getType())) {
													nearOres.add(near);
												}
												if ((allowed.contains(near)) || (near.isEmpty()) || (near.isLiquid())) {
													allowed.add(block);
												}
											}
										}
									}
								}
							}
							if (allowed.contains(block)) {
								for (Block near : nearOres) {
									allowLinked(near, toRemove, allowed);
								}
								toRemove.remove(block);
							} else {
								toRemove.put(block, nearOres);
							}
						}
					}
				}
			}
		}
		
		EnumMap<Material, Integer> remaining = new EnumMap<Material, Integer>(Material.class);
		
		while (!toRemove.isEmpty()) {
			HashSet<Block> vein = new HashSet<Block>();
			Block next = (Block) toRemove.keySet().iterator().next();
			addLinked(next, toRemove, vein);
			Material type = vein.iterator().next().getType();
			if ((excludedOres.contains(type)) || ((removalFactor < 100) && (random.nextInt(100) >= removalFactor))) {
				addRemainingOres(remaining, type, vein.size());
			} else {
				for (Block block : vein) {
					block.setType(oreReplacer);
				}
			}
			toRemove.keySet().removeAll(vein);
		}
		
		for (Block block : allowed) {
			addRemainingOres(remaining, block.getType(), 1);
		}
		
		long duration = System.nanoTime() - started;
		
		if ((hasNoOres) && (!this.worldData.hasNoOres(this))) {
			worldData.doASecondCheck(this);
		} else {
			worldData.logCompleted(this, duration, remaining);
		}
	}

	public UUID getWorldName() {
		return this.worldData.getWorldId();
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ChunkOreRemover)) {
			ChunkOreRemover other = (ChunkOreRemover) obj;
			return (other.chunkX == this.chunkX) && (other.chunkZ == this.chunkZ) && (other.worldData.equals(this.worldData));
		}
		
		ChunkOreRemover other = (ChunkOreRemover) obj;
		return (other.chunkX == this.chunkX) && (other.chunkZ == this.chunkZ) && (other.worldData.equals(this.worldData));
		
		if ((obj instanceof ChunkOreRemover)) {
		}
		
	}

	public int hashCode() {
		int hash = 5;
		
		hash = 13 * hash + Objects.hashCode(this.worldData);
		hash = 13 * hash + this.chunkX;
		hash = 13 * hash + this.chunkZ;
		
		return hash;
	}

	public String toString() {
		return this.chunkX + "," + this.chunkZ;
	}
}