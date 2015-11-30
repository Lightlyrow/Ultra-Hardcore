package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;

/**
 * Moved 0,0 scenario class
 * 
 * @author LeonTG77
 */
public class MovedMiddle extends Scenario implements Listener {
	
	public MovedMiddle() {
		super("Moved0,0", "There is no 0,0 on the map, meetup coords are announced after 80 minutes.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
}