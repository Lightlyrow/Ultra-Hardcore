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
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * World listener class.
 * <p>
 * Contains all eventhandlers for world releated events.
 * 
 * @author LeonTG77
 */
public class WorldListener implements Listener {
	private final Game game;
	
	private final Timer timer;
	private final Arena arena;
	
	/**
	 * World listener class constructor.
	 * 
	 * @param game The game class.
	 * @param timer The timer class.
	 * @param arena The arena class.
	 */
	public WorldListener(Game game, Timer timer, Arena arena) {
		this.game = game;
		
		this.timer = timer;
		this.arena = arena;
	}

	@EventHandler
	public void on(WeatherChangeEvent event) {
    	if (!event.toWeatherState()) {
    		return;
    	}
    	
    	List<World> worlds = game.getWorlds();
    	World world = event.getWorld();
    	
    	if (!worlds.contains(world)) {
			event.setCancelled(true);
			return;
		}

		if (!State.isState(State.INGAME)) {
			event.setCancelled(true);
			return;
		}

		if (timer.getMeetup() <= 0) {
			event.setCancelled(true);
			return;
		}

		if (timer.getPvP() > 0) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void on(ThunderChangeEvent event) {
    	if (!event.toThunderState()) {
    		return;
		}
    	
		event.setCancelled(true);
	}
	
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
}