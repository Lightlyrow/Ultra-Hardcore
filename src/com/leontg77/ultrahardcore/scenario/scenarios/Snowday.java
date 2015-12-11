package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Snowday scenario class
 * 
 * @author LeonTG77
 */
public class Snowday extends Scenario {
	
	public Snowday() {
		super("Snowday", "The entire world is a snow biome!");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {
		Game game = Game.getInstance();
		
		World world = game.getWorld();
		WorldBorder border = world.getWorldBorder();
		
		int size = (int) border.getSize();
		
		int radius = size / 2;
		
		size = size - radius;
		
		for (int x = (size * -1); x < size; x++) {
			for (int z = (size * -1); z < size; z++) {
				world.getBlockAt(x, 64, z).setBiome(Biome.COLD_TAIGA);
			}
		}
	}
}