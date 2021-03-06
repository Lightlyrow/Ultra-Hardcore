package com.leontg77.ultrahardcore.feature.world;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Weather feature class.
 * 
 * @author LeonTG77
 */
public class WeatherFeature extends Feature implements Listener {
	private final Game game;

	private final Timer timer;

	public WeatherFeature(Timer timer, Game game) {
		super("Weather", "Disable thunder storms completly and disable rain before pvp and after meetup.");

		this.timer = timer;
		this.game = game;
	}

	@EventHandler
	public void on(WeatherChangeEvent event) {
		if (!event.toWeatherState()) {
			return;
		}
		
		World world = event.getWorld();

		if (!game.getWorlds().contains(world)) {
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
}