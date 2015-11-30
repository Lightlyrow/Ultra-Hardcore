package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;

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
	public void onEnable() {
		ScenarioManager scen = ScenarioManager.getInstance();
		scen.getScenario(Moles.class).setEnabled(true);
	}
}