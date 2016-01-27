package com.leontg77.ultrahardcore.feature;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Settings;

public abstract class Feature {
	protected final Settings settings = Settings.getInstance();
	protected final Game game = Game.getInstance();

	private String description;
	private String name;
	
	/**
	 * The feature class constructor
	 * 
	 * @param name The name of the feature.
	 * @param description The description of the feature.
	 */
	protected Feature(String name, String description) {
		this.description = description;
		this.name = name;
	}
	
	/**
	 * Get the name of the feature
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the description of the feature.
	 * 
	 * @return the description.
	 */
	public String getDescription() {
		return description;
	}
}
