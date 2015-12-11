package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Christmas Listener.
 * <p>
 * Class used for christmas releated events like disabling ice/snow melt and having snow in the lobby.
 * 
 * @author LeonTG77
 */
public class ChristmasListener implements Listener {
	
	@EventHandler
	public void on(BlockFadeEvent event) {
		Block block = event.getBlock();
		
		if (!block.getWorld().getName().equals("lobby")) {
    		return;
    	}
		
		if (block.getType() == Material.ICE) {
			event.setCancelled(true);
		}
		
		if (block.getType() == Material.SNOW) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (!player.getWorld().getName().equals("lobby")) {
    		return;
    	}
		
		player.setPlayerWeather(WeatherType.DOWNFALL);
	}
	
	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (world.getName().equals("lobby")) {
			player.setPlayerWeather(WeatherType.DOWNFALL);
		} else {
			player.resetPlayerWeather();
		}
	}
}