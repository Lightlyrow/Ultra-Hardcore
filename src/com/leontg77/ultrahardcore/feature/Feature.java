package com.leontg77.ultrahardcore.feature;

/**
 * A feature of the plugin.
 * 
 * @author LeonTG77
 */
public abstract class Feature {
	private String description;
	private String name;
	
	/**
	 * Feature class constructor
	 * 
	 * @param name The name of the feature.
	 * @param description A short description of the feature.
	 */
	public Feature(String name, String description) {
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