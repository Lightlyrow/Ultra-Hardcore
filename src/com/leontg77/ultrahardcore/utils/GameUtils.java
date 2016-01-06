package com.leontg77.ultrahardcore.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;

/**
 * Game utilities class.
 * <p>
 * Contains game related methods.
 * 
 * @author LeonTG77
 */
public class GameUtils {

    /**
     * Get the hof name for the given host.
     *
     * @param host The host.
     * @return The hof name.
     */
    public static String getHostName(String name) {
        Settings settings = Settings.getInstance();

        for (String path : settings.getHOF().getKeys(false)) {
            if (path.equalsIgnoreCase(name)) {
                return path;
            }
            
            String nameInHof = settings.getHOF().getString(path + ".name", "none");
            
            if (nameInHof.equalsIgnoreCase(name)) {
            	return path;
            }
        }

	    return name;
    }
	
	/**
	 * Get all the worlds being used by the game.
	 * 
	 * @return A list of game worlds.
	 */
	public static List<World> getGameWorlds() {
		ArrayList<World> worlds = new ArrayList<World>();
		Game game = Game.getInstance();
		
		World main = game.getWorld();
		
		if (main == null) {
			return worlds;
		}
		
		worlds.add(main);
		
		World nether = Bukkit.getWorld(main.getName() + "_nether");
		World end = Bukkit.getWorld(main.getName() + "_end");
		
		if (nether != null) {
			worlds.add(nether);
		}
		
		if (end != null) {
			worlds.add(end);
		}
		
		return worlds;
	}
	
	public static List<Player> getGamePlayers() {
		List<Player> list = new ArrayList<Player>();
    	Spectator spec = Spectator.getInstance();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (spec.isSpectating(online) || !getGameWorlds().contains(online.getWorld())) {
				continue;
			}
			
			list.add(online);
		}
		
		return list;
	}
	
	/**
	 * Gets a string version of the current state.
	 * 
	 * @return The string version.
	 */
	public static String getMOTDMessage() {
		State current = State.getState();
		Game game = Game.getInstance();

		if (getTeamSize(false, false).startsWith("No") || game.isRecordedRound() || game.isPrivateGame()) {
			return "No games running";
		}
		
		switch (current) {
		case SCATTER:
		case INGAME:
			return "Match in progress";
		case NOT_RUNNING:
			return "No games running";
		case CLOSED:
			return "Whitelist is on";
		case OPEN:
			if (getTeamSize(false, false).startsWith("Open")) {
				return "Open " + Game.getInstance().getScenarios();
			} 
			
			return "Whitelist is off";
		default:
			return "No games running";
		}
	}

	/**
	 * Get the teamsize in a string format.
	 * 
	 * @param advancedFFA Wether to spell it as "FFA" or "Free for all"
	 * @param seperate Wether to have the returned string end with - or not.
	 * @return The string format.
	 */
	public static String getTeamSize(boolean advancedFFA, boolean seperate) {
		Game game = Game.getInstance();
		
		String seperator = seperate ? " - " : " ";
		
		if (game.getTeamSize().startsWith("FFA")) {
			return (advancedFFA ? "Free for all" : "FFA") + seperator;
		} 
		
		if (game.getTeamSize().startsWith("rTo")) {
			return "Random " + game.getTeamSize().substring(1) + seperator;
		} 
		
		if (game.getTeamSize().startsWith("cTo") || game.getTeamSize().startsWith("To")) {
			return "Chosen " + game.getTeamSize().substring(1) + seperator;
		} 
		
		if (game.getTeamSize().startsWith("mTo")) {
			return "Mystery " + game.getTeamSize().substring(1) + seperator;
		} 
		
		if (game.getTeamSize().startsWith("pTo")) {
			return "Picked " + game.getTeamSize().substring(1) + seperator;
		} 
		
		if (game.getTeamSize().startsWith("CapTo")) {
			return "Captains " + game.getTeamSize().substring(1) + seperator;
		} 
		
		if (game.getTeamSize().startsWith("AucTo")) {
			return "Auction " + game.getTeamSize().substring(1) + seperator;
		} 
		
		if (game.getTeamSize().startsWith("No") || game.getTeamSize().startsWith("Open")) {
			return game.getTeamSize() + " ";
		}
		
		return game.getTeamSize() + seperator;
	}
}