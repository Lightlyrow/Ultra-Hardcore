package com.leontg77.uhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.leontg77.uhc.Main;

/**
 * Scenario super class.
 * 
 * @author LeonTG77
 */
public abstract class Scenario {
	private boolean enabled = false;

	private String name;
	private String desc;
	
	/**
	 * Scenario constructor
	 * 
	 * @param name scenario name
	 * @param desc description of the scenario
	 */
	protected Scenario(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	/**
	 * Get the name of the scenario
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the description of the scenario.
	 * 
	 * @return the description.
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * Sets the scenario to enable or disable
	 * 
	 * @param enable true to enable, false to disable.
	 */
	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			if (this instanceof Listener) {
				Bukkit.getPluginManager().registerEvents((Listener) this, Main.plugin);
			}
			
			onEnable();
		} else {
			if (this instanceof Listener) {
				HandlerList.unregisterAll((Listener) this);
			}
			
			onDisable();
		}
	}
	
	/**
	 * Check if the scenario is enabled
	 * 
	 * @return True if enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Called when the scenario is enabled.
	 */
	public abstract void onEnable();
	
	/**
	 * Called when the scenario is disabled.
	 */
	public abstract void onDisable();
}