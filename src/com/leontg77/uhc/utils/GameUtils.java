package com.leontg77.uhc.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.State;

/**
 * Game utilities class.
 * <p>
 * Contains game related methods.
 * 
 * @author LeonTG77
 */
public class GameUtils {
	
	/**
	 * Get all the worlds being used by the game.
	 * 
	 * @return A list of game worlds.
	 */
	public static List<World> getGameWorlds() {
		ArrayList<World> worlds = new ArrayList<World>();
		Game game = Game.getInstance();
		
		World main = game.getWorld();
		
		if (main != null) {
			worlds.add(main);
			
			World nether = Bukkit.getWorld(main.getName() + "_nether");
			
			if (nether != null) {
				worlds.add(nether);
			}
			
			World end = Bukkit.getWorld(main.getName() + "_end");
			
			if (end != null) {
				worlds.add(end);
			}
		}
		
		return worlds;
	}
	
	public static List<Player> getGamePlayers() {
		List<Player> list = new ArrayList<Player>();
    	Spectator spec = Spectator.getInstance();
		
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			if (spec.isSpectating(online) || !GameUtils.getGameWorlds().contains(online.getWorld())) {
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
		
		switch (current) {
		case SCATTER:
		case INGAME:
			if (GameUtils.getTeamSize().startsWith("No") || game.isRecordedRound() || game.getHost().equalsIgnoreCase("LeonsPrivate")) {
				return "No games running";
			} 
			else if (getTeamSize().startsWith("Open")) {
				return "Open " + game.getScenarios();
			} 
			else {
				return "Match in progress";
			}
		case LOBBY:
			if (Bukkit.getServer().hasWhitelist()) {
				if (GameUtils.getTeamSize().startsWith("No") || game.isRecordedRound() || game.getHost().equalsIgnoreCase("LeonsPrivate")) {
					return "No games running";
				} 
				else if (getTeamSize().startsWith("Open")) {
					return "Open " + Game.getInstance().getScenarios();
				} 
				else {
					return "Whitelist is on";
				}
			} else {
				if (getTeamSize().startsWith("Open")) {
					return "Open " + Game.getInstance().getScenarios();
				} 
				
				return "Whitelist is off";
			}
		default:
			return "No games running";
		}
	}
	
	/**
	 * Get the teamsize in a string format.
	 * 
	 * @return The string format.
	 */
	public static String getTeamSize() {
		Game game = Game.getInstance();
		
		if (game.isFFA()) {
			if (game.getTeamSize() == 1) {
				return "FFA ";
			} 
			else if (game.getTeamSize() == 0) {
				return "No ";
			}
			else if (game.getTeamSize() == -1) {
				return "Open ";
			} 
			else if (game.getTeamSize() == -2) {
				return "";
			}
			else {
				return "rTo" + (game.getTeamSize() > 0 ? game.getTeamSize() : "X") + " ";
			}
		} 
		else {
			return "cTo" + (game.getTeamSize() > 0 ? game.getTeamSize() : "X") + " ";
		}
	}
	
	/**
	 * Get the teamsize in a more advanced string format.
	 * 
	 * @return The string in advanced format.
	 */
	public static String getAdvancedTeamSize() {
		Game game = Game.getInstance();
		
		if (game.isFFA()) {
			if (game.getTeamSize() == 1) {
				return "Free for all ";
			} 
			else if (game.getTeamSize() == 0) {
				return "No ";
			}
			else if (game.getTeamSize() == -1) {
				return "Open ";
			} 
			else if (game.getTeamSize() == -2) {
				return "";
			}
			else {
				return "Random To" + (game.getTeamSize() > 0 ? game.getTeamSize() : "X") + " ";
			}
		} 
		else {
			return "Chosen To" + (game.getTeamSize() > 0 ? game.getTeamSize() : "X") + " ";
		}
	}
	
	/**
	 * Get the current host hof name.
	 * 
	 * @return The hof name.
	 */
	public static String getCurrentHost() {
		Game game = Game.getInstance();
		String host = game.getHost();
		
		if (host.equalsIgnoreCase("LeonTG77")) {
			return "Leon";
		} 
		else if (host.equalsIgnoreCase("PolarBlunk")) {
			return "Polar";
		} 
		else if (host.equalsIgnoreCase("Itz_Isaac")) {
			return "Isaac";
		}
		else if (host.equalsIgnoreCase("Badfan")) {
			return "Badfan";
		}
		else if (host.equalsIgnoreCase("AxlurUSC")) {
			return "Axlur";
		}
		else if (host.equalsIgnoreCase("LimitDTW")) {
			return "Limit";
		}
		else if (host.equalsIgnoreCase("BLA2K14")) {
			return "BLA2K14";
		}
		else if (host.equalsIgnoreCase("MajorWoof")) {
			return "MajorWoof";
		}
		return host;
	}

	/**
	 * Get the hof name for the given host.
	 * 
	 * @param host The host.
	 * @return The hof name.
	 */
	public static String getHost(String host) {
		if (host.equalsIgnoreCase("LeonTG77") || host.equalsIgnoreCase("Leon")) {
			return "Leon";
		} 
		else if (host.equalsIgnoreCase("Polar") || host.equalsIgnoreCase("PolarBlunk")) {
			return "Polar";
		} 
		else if (host.equalsIgnoreCase("Itz_Isaac") || host.equalsIgnoreCase("Isaac")) {
			return "Isaac";
		}
		else if (host.equalsIgnoreCase("axlur") || host.equalsIgnoreCase("AxlurUHC") || host.equalsIgnoreCase("AxlurUSC")) {
			return "Axlur";
		}
		else if (host.equalsIgnoreCase("Limit") || host.equalsIgnoreCase("LimitDTW")) {
			return "Limit";
		}
		else if (host.equalsIgnoreCase("Badfan") || host.equalsIgnoreCase("BadfanMC")) {
			return "Badfan";
		}
		else if (host.equalsIgnoreCase("BLA2K14")) {
			return "BLA2K14";
		}
		else if (host.equalsIgnoreCase("MajorWoof") || host.equalsIgnoreCase("Major")) {
			return "MajorWoof";
		}
		return host;
	}

	/**
	 * Get the host name for the given alt name.
	 * 
	 * @param host The host.
	 * @return The hof name.
	 */
	public static String getHostName(String host) {
		if (host.equalsIgnoreCase("Leon")) {
			return "LeonTG77";
		} 
		else if (host.equalsIgnoreCase("Polar")) {
			return "PolarBlunk";
		} 
		else if (host.equalsIgnoreCase("Isaac")) {
			return "Itz_Isaac";
		}
		else if (host.equalsIgnoreCase("Badfan")) {
			return "Badfan";
		}
		else if (host.equalsIgnoreCase("Axlur")) {
			return "AxlurUSC";
		}
		else if (host.equalsIgnoreCase("BLA2K14")) {
			return "BLA2K14";
		}
		else if (host.equalsIgnoreCase("Limit")) {
			return "LimitDTW";
		}
		else if (host.equalsIgnoreCase("MajorWoof")) {
			return "MajorWoof";
		}
		return host;
	}
}