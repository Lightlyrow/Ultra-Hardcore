package com.leontg77.ultrahardcore.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.leontg77.pregenner.events.WorldBorderFillFinishedEvent;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.scenario.scenarios.ChunkApocalypse;
import com.leontg77.ultrahardcore.scenario.scenarios.Voidscape;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * World listener class.
 * <p>
 * Contains all eventhandlers for world releated events.
 * 
 * @author LeonTG77
 */
public class WorldListener implements Listener {

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
    	if (!event.toWeatherState()) {
    		return;
    	}
    	
    	List<World> worlds = Game.getInstance().getWorlds();
    	World world = event.getWorld();
    	
    	if (!worlds.contains(world)) {
			event.setCancelled(true);
			return;
		}

		if (!State.isState(State.INGAME)) {
			event.setCancelled(true);
			return;
		}
		
		final Timers timer = Timers.getInstance();

		if (timer.getMeetup() <= 0) {
			event.setCancelled(true);
			return;
		}

		if (timer.getPvP() > 0) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onThunderChange(ThunderChangeEvent event) {
    	if (!event.toThunderState()) {
    		return;
		}
    	
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (State.isState(State.SCATTER) || Voidscape.task != null || ChunkApocalypse.task != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWorldBorderFillFinished(WorldBorderFillFinishedEvent event) {
		Arena arena = Arena.getInstance();
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
}