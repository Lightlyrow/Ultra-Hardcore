package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * PotentialMoles scenario class
 * 
 * @author LeonTG77
 */
public class PotentialMoles extends Scenario implements Listener {
	private final Moles moles;
	private final Main plugin;
	
	public PotentialMoles(Main plugin, Moles moles) {
		super("PotentialMoles", "There is a 50% chance of a team having a mole, moles on each team work together to take out the normal teams.");

		this.plugin = plugin;
		this.moles = moles;
	}

	@Override
	public void onDisable() {
		moles.disable();
	}

	@Override
	public void onEnable() {
		moles.enable(plugin);
	}
	
	@EventHandler
	public void on(PvPEnableEvent event) {
		moles.on(event); // we need make the moles plugin run the onpvpenableevent as well.
	}
}