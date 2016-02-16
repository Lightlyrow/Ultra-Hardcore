package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Birds scenario class
 * 
 * @author LeonTG77
 */
public class Birds extends Scenario implements Listener {
	
	public Birds() {
		super("Birds", "All players can fly!");
	}
	
	@Override
	public void onDisable() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setAllowFlight(false);
			online.setFlying(false);
		}
	}

	@Override
	public void onEnable() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setAllowFlight(true);
		}
	}
	
	@EventHandler
	public void on(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		player.setAllowFlight(true);
	}
}