package com.leontg77.ultrahardcore.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.commands.Parser;

/**
 * Scenario super class.
 * 
 * @author LeonTG77
 */
public abstract class Scenario extends Parser {
	private boolean enabled = false;

	protected final Timers timer = Timers.getInstance();
	protected final Game game = Game.getInstance();

	private String description;
	private String name;
	
	/**
	 * The scenario class constructor
	 * 
	 * @param name The name of the scenario.
	 * @param description The description of the scenario.
	 */
	protected Scenario(String name, String description) {
		this.description = description;
		this.name = name;
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
		return description;
	}
	
	/**
	 * Sets the scenario to enable or disable
	 * 
	 * @param enable true to enable, false to disable.
	 */
	public void setEnabled(boolean enable) {
		this.enabled = enable;
		
		if (enable) {
			if (this instanceof Listener) {
				Bukkit.getPluginManager().registerEvents((Listener) this, Main.plugin);
			}
			
			onEnable();
			return;
		}
		
		if (this instanceof Listener) {
			HandlerList.unregisterAll((Listener) this);
		}
		
		onDisable();
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
	 * Called when the scenario is disabled.
	 */
	public abstract void onDisable();
	
	/**
	 * Called when the scenario is enabled.
	 */
	public abstract void onEnable();
}