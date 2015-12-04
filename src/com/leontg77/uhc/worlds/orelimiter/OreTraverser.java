package com.leontg77.uhc.worlds.orelimiter;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.google.common.collect.Lists;

/**
 * Ore Traverser class.
 * 
 * @author dans1988
 */
public class OreTraverser {
	private final List<OreLocation> vein;

	public OreTraverser() {
		this.vein = Lists.newArrayList();
	}

	/**
	 * Traverser the vein.
	 * 
	 * @param ore The ore location of the vein.
	 * @param material The vein material.
	 * @param world The world of the vein.
	 * @param replace Wether to replace the vein or not.
	 * @param replacement If replacing, this is what we're replacing with.
	 */
	public void traverseVein(OreLocation ore, Material material, World world, boolean replace, Material replacement) {
		Block block = world.getBlockAt(ore.getX(), ore.getY(), ore.getZ());

		if (material.equals(block.getType()) && !vein.contains(ore)) {
			vein.add(ore);
			if (replace) {
				block.setType(replacement);
			}

			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						OreLocation newLocation = new OreLocation(ore.getX() + x, ore.getY() + y, ore.getZ() + z);

						traverseVein(newLocation, material, world, replace, replacement);
					}
				}
			}
		}
	}

	/**
	 * Get the list of all the ore locations in the vein.
	 * 
	 * @return A list of ore locs.
	 */
	public List<OreLocation> getVein() {
		return vein;
	}
}