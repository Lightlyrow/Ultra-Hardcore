package com.leontg77.ultrahardcore.feature;

import static com.leontg77.ultrahardcore.Main.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Scenario management class.
 * 
 * @author LeonTG77
 */
public class FeatureManager {
	private static final FeatureManager INSTANCE = new FeatureManager();
	private final List<Feature> features = new ArrayList<Feature>();
	
	/**
	 * Get the instance of this class.
	 * 
	 * @return The class instance.
	 */
	public static FeatureManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Get a scenario by a name.
	 * 
	 * @param name the name.
	 * @return The scenario, null if not found.
	 */
	public Feature getScenario(String name) {
		for (Feature feat : features) {
			if (feat.getName().equalsIgnoreCase(name)) {
				return feat;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a scenario by the class.
	 * 
	 * @param featureClass The class.
	 * @return The scenario, null if not found.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getScenario(Class<T> featureClass) {
		for (Feature feat : features) {
			if (feat.getClass().equals(featureClass)) {
				return (T) feat;
			}
		}
		
		return null;
	}

	/**
	 * Get a list of all scenarios.
	 * 
	 * @return the list of scenarios.
	 */
	public List<Feature> getScenarios() {
		return features;
	}

	/**
	 * Get a list of all enabled scenarios.
	 * 
	 * @return the list of enabled scenarios.
	 */
	public List<ToggleableFeature> getEnabledScenarios() {
		List<ToggleableFeature> list = new ArrayList<ToggleableFeature>();
		
		for (Feature s : features) {
			if (((ToggleableFeature) s).isEnabled()) {
				list.add(((ToggleableFeature) s));
			}
		}
		
		return list;
	}

	/**
	 * Get a list of all enabled scenarios.
	 * 
	 * @return the list of enabled scenarios.
	 */
	public List<ToggleableFeature> getDisabledScenarios() {
		List<ToggleableFeature> list = new ArrayList<ToggleableFeature>();
		
		for (Feature s : features) {
			if (!((ToggleableFeature) s).isEnabled()) {
				list.add(((ToggleableFeature) s));
			}
		}
		
		return list;
	}
	
	/**
	 * Setup all the scenario classes.
	 */
	public void setup() {
		
		plugin.getLogger().info("All features has been setup.");
	}
}