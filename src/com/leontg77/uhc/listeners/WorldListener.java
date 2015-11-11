package com.leontg77.uhc.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.State;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.scenario.types.ChunkApocalypse;
import com.leontg77.uhc.scenario.types.Voidscape;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.worlds.AntiStripmine;
import com.leontg77.uhc.worlds.AntiStripmine.ChunkOreRemover;
import com.leontg77.uhc.worlds.AntiStripmine.WorldData;
import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;

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
    	
    	List<World> worlds = GameUtils.getGameWorlds();
    	World world = event.getWorld();
    	
    	if (!worlds.contains(world)) {
			event.setCancelled(true);
			return;
		}

		if (!State.isState(State.INGAME)) {
			event.setCancelled(true);
			return;
		}

		if (Timers.meetup <= 0) {
			event.setCancelled(true);
			return;
		}

		if (Timers.pvp > 0) {
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
	public void onChunkPopulate(ChunkPopulateEvent event) {
		Game game = Game.getInstance();
		
		if (!game.antiStripmine()) {
			return;
		}
		
		AntiStripmine strip = AntiStripmine.getInstance();
		World world = event.getWorld();
		
		WorldData worldData = strip.getWorldData(world);
		
		if (!worldData.isEnabled()) {
			return;
		}
		
		ChunkOreRemover remover = new ChunkOreRemover(worldData, event.getChunk());
		
		if (AntiStripmine.getInstance().wasChecked(remover)) {
			Main.plugin.getLogger().warning("Populated " + worldData.getWorld().getName() + " " + remover.getChunkX() + "," + remover.getChunkZ() + " again");
			AntiStripmine.getInstance().queue(remover);
		} else {
			worldData.logQueued(remover);
			AntiStripmine.getInstance().queue(remover);
		}
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		Game game = Game.getInstance();
		
		if (!game.antiStripmine()) {
			return;
		}
		
		AntiStripmine strip = AntiStripmine.getInstance();
		World world = event.getWorld();
		
		strip.registerWorld(world);
	}

	@EventHandler
	public void onWorldBorderFillFinished(WorldBorderFillFinishedEvent event) {
		Arena arena = Arena.getInstance();
		World world = event.getWorld();
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + world.getName() + " clear");
		
		if (arena.reset) {
			PlayerUtils.broadcast(Main.PREFIX + "Arena reset complete");
			
			if (arena.wasEnabled) {
				arena.enable();
			}
			
			arena.wasEnabled = false;
			arena.reset = false;
			return;
		}

		PlayerUtils.broadcast(Main.PREFIX + "Pregen of world '§a" + world.getName() + "§7' finished.");
	}
}