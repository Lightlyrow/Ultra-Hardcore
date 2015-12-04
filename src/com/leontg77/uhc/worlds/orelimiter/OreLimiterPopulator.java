package com.leontg77.uhc.worlds.orelimiter;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import com.google.common.collect.Lists;

public class OreLimiterPopulator extends BlockPopulator {
    private static final int CHUNK_HEIGHT_LIMIT = 128;
    private static final int BLOCKS_PER_CHUNK = 16;
    private static final int RANDOM_BOUNDS = 100;
    
    private final List<OreLocation> markedOres;

    public OreLimiterPopulator() {
        this.markedOres = Lists.newArrayList();
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int x = 0; x < BLOCKS_PER_CHUNK; x++) {
            for (int z = 0; z < BLOCKS_PER_CHUNK; z++) {
                for (int y = CHUNK_HEIGHT_LIMIT - 1; y > 0; y--) {
                    Block block = world.getBlockAt(chunk.getX() * 16 + x, y, chunk.getZ() * 16 + z);
                    
                    Map<Material, Integer> rates = oreLimiter.getOreRates();
                    OreLocation oreLocation = new OreLocation(block.getX(), block.getY(), block.getZ());
                    
                    if (rates.keySet().contains(block.getType()) && rates.get(block.getType()) < 100 && !markedOres.contains(oreLocation)) {
                        Map<Material, Material> replacements = oreLimiter.getReplacements();
                        boolean replace = random.nextInt(RANDOM_BOUNDS) >= oreLimiter.getOreRates().get(block.getType());
                        
                        OreTraverser oreTraverser = new OreTraverser();
                        oreTraverser.traverseVein(oreLocation, block.getType(), world, replace, replacements.get(block.getType()));
                        List<OreLocation> vein = oreTraverser.getVein();
                        markedOres.addAll(vein);
                    }
                }
            }
        }
    }
}