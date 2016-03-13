package com.leontg77.ultrahardcore.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.gui.guis.ConfigGUI;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.gui.guis.HallOfFameGUI;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;

/**
 * GUI manager class.
 * 
 * @author LeonTG77
 */
public class GUIManager {
	private final Main plugin;
	
	/**
	 * GUI manager class constructor.
	 * 
	 * @param plugin The main class.
	 */
	public GUIManager(Main plugin) {
		this.plugin = plugin;
	}

	private final List<GUI> guis = new ArrayList<GUI>();
	
	/**
	 * Get a GUI by the given name.
	 * 
	 * @param name The name to check with.
	 * @return The GUI, null if not found.
	 */
	public GUI getGUI(String name) {
		for (GUI gui : guis) {
			if (gui.getName().equalsIgnoreCase(name)) {
				return gui;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a GUI by the given name.
	 * 
	 * @param name The class to check with.
	 * @return The GUI, null if not found.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getGUI(Class<T> clazz) {
		for (GUI gui : guis) {
			if (gui.getClass().equals(clazz)) {
				return (T) gui;
			}
		}
		
		return null;
	}

	/**
	 * Get a list of all GUI's.
	 * 
	 * @return A list of GUI's.
	 */
	public List<GUI> getGUIS() {
		return ImmutableList.copyOf(guis);
	}
	
	/**
	 * Setup all the GUI inventories.
	 */
	public void registerGUIs(Game game, Timer timer, Settings settings, FeatureManager feat, ScenarioManager scen) {
		addGUI(new ConfigGUI(settings, feat));
		addGUI(new GameInfoGUI(plugin, game, timer, feat, scen));
		addGUI(new HallOfFameGUI(settings));
		
		plugin.getLogger().info("All inventories has been setup.");
	}
	
	/**
	 * Add the given GUI to the storage, call it's setup method and register listeners if any.
	 * 
	 * @param gui The GUI to use.
	 */
	private void addGUI(GUI gui) {
		guis.add(gui);
		gui.onSetup();
		
		if (gui instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) gui, plugin);
		}
	}
}