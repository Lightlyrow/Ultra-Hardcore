package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;

/**
 * PotentialMoles scenario class
 * 
 * @author LeonTG77
 */
public class PotentialMoles extends Scenario implements Listener {

	public PotentialMoles() {
		super("PotentialMoles", "There is a 50% chance of a team having a mole, moles on each team work together to take out the normal teams.");
	}

	@Override
	public void onDisable() {
		ScenarioManager scen = ScenarioManager.getInstance();
		scen.getScenario(Moles.class).setEnabled(false);
	}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void on(PvPEnableEvent event) {
		ScenarioManager scen = ScenarioManager.getInstance();
		Moles moles = scen.getScenario(Moles.class);
		
		moles.setEnabled(true);
		moles.on(event);
	}
}