package com.leontg77.ultrahardcore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.BestBTC;
import com.leontg77.ultrahardcore.scenario.scenarios.BestPvE;

/**
 * The data storage system class.
 * 
 * @author LeonTG77
 */
public class Data {
	private final Settings settings;
	private final Main plugin;
	
	public Data(Main plugin, Settings settings) {
		this.settings = settings;
		this.plugin = plugin;
	}

	public void store(TeamManager teams, ScenarioManager scens) {
		saveSet(scens.getScenario(BestBTC.class).getList());	
		saveSet(scens.getScenario(BestPvE.class).getList());	
		
		List<String> scenarios = new ArrayList<String>();
		
		for (Scenario scen : scens.getEnabledScenarios()) {
			scenarios.add(scen.getName());
		}
		
		settings.getData().set("scenarios", scenarios);
		
		for (Entry<String, Set<String>> entry : teams.getSavedTeams().entrySet()) {
			settings.getData().set("teams.data." + entry.getKey(), new ArrayList<String>(entry.getValue()));
		}
		
		settings.saveData();
	}
	
	public void restore(TeamManager teams, ScenarioManager scens) {
		restoreSet(scens.getScenario(BestBTC.class).getList());	
		restoreSet(scens.getScenario(BestPvE.class).getList());
		
		try {
			for (String name : settings.getData().getConfigurationSection("teams.data").getKeys(false)) {
				teams.getSavedTeams().put(name, new HashSet<String>(settings.getData().getStringList("teams.data." + name)));
			}
		} catch (Exception e) {
			plugin.getLogger().warning("Could not recover team data.");
		}
		
		try {
			for (String scen : settings.getData().getStringList("scenarios")) {
				scens.getScenario(scen).enable(plugin);
			}
		} catch (Exception e) {
			plugin.getLogger().warning("Could not recover scenario data.");
		}
	}
	
	private void saveList(List<String> list) {
		
	}
	
	private void restoreList(List<String> list) {
		
	}
	
	private void saveSet(Set<String> list) {
		saveList(new ArrayList<String>(list));
	}
	
	private void restoreSet(Set<String> list) {
		restoreList(new ArrayList<String>(list));
	}
}