package com.leontg77.ultrahardcore.worlds;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;

import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;

public class OldTerrain extends BlockPopulator implements Listener {
	private static OldTerrain instance = new OldTerrain();
	
    private static final int CHUNK_HEIGHT_LIMIT = 128;
    private static final int BLOCKS_PER_CHUNK = 16;
    private static final int RANDOM_BOUNDS = 100;

    /**
     * Get the instance of the class.
     * 
     * @return The instance.
     */
	public static OldTerrain getInstance() {
		return instance;
	}
    
	/**
	 * Setup the ore limiter class.
	 */
    public void setup() {
        Bukkit.getPluginManager().registerEvents(this, Main.plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldInit(final WorldInitEvent event) {
        Bukkit.getLogger().log(Level.INFO, "World init detected! OreLimiter");
        event.getWorld().getPopulators().add(this);
    }

	@Override
    public void populate(World world, Random rand, Chunk chunk) {
    	if (!Game.getInstance().oldTerrain()) {
    		return;
    	}
    	
        for (int x = 0; x < BLOCKS_PER_CHUNK; x++) {
            for (int z = 0; z < BLOCKS_PER_CHUNK; z++) {
                for (int y = CHUNK_HEIGHT_LIMIT - 1; y > 0; y--) {
                    Block block = chunk.getBlock(x, y, z);
                    OreLocation oreLocation = new OreLocation(block.getX(), block.getY(), block.getZ());
                    
                    if (block.getType() == Material.DIAMOND_ORE || block.getType() == Material.GOLD_ORE) {
                        boolean replace;
                        
                        if (block.getType() == Material.GOLD_ORE) {
                        	replace = rand.nextInt(RANDOM_BOUNDS) >= 70;
                        } else {
                        	replace = rand.nextInt(RANDOM_BOUNDS) >= 70;
                        }
                        
                        OreTraverser oreTraverser = new OreTraverser();
                        oreTraverser.traverseVein(oreLocation, block.getType(), world, replace, Material.STONE);
                    }
                }
            }
        }
    }
    
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
    
    /**
     * Ore location class.
     * 
     * @author dans1988, modified by LeonTG77.
     */
    public class OreLocation {
    	private final int x;
    	private final int y;
    	private final int z;

    	private final int hashCode;

    	public OreLocation(int x, int y, int z) {
    		this.x = x;
    		this.y = y;
    		this.z = z;

    		hashCode = Objects.hash(x, y, z);
    	}

    	/**
    	 * Get the X coord of the OreLocation.
    	 * 
    	 * @return The X coord.
    	 */
    	public int getX() {
    		return x;
    	}

    	/**
    	 * Get the Y coord of the OreLocation.
    	 * 
    	 * @return The Y coord.
    	 */
    	public int getY() {
    		return y;
    	}

    	/**
    	 * Get the Z coord of the OreLocation.
    	 * 
    	 * @return The Z coord.
    	 */
    	public int getZ() {
    		return z;
    	}

    	@Override
    	public int hashCode() {
    		return hashCode;
    	}

    	@Override
    	public boolean equals(Object obj) {
    		if (obj == this) {
    			return true;
    		}

    		if (obj == null) {
    			return false;
    		}

    		if (!(obj instanceof OreLocation)) {
    			return false;
    		}

    		OreLocation other = (OreLocation) obj;

    		return Objects.equals(this.x, other.x) && Objects.equals(this.y, other.y) && Objects.equals(this.z, other.z);
    	}
    }
}