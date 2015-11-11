package com.leontg77.uhc.scenario.types;

import org.bukkit.event.Listener;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;

/**
 * PotentialMoles scenario class
 * 
 * @author Bergasms, modified by LeonTG77
 */
public class PotentialMoles extends Scenario implements Listener {
	private boolean enabled = false;

	public PotentialMoles() {
		super("PotentialMoles", "There is a 50% chance of a team having a mole, moles on each team work together to take out the normal teams.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		ScenarioManager scen = ScenarioManager.getInstance();
		
		if (enable) {
			scen.getScenario("Moles").enable();
		} else {
			scen.getScenario("Moles").disable();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}