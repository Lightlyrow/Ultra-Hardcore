package com.leontg77.uhc.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Settings;
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

		if (game.isRecordedRound() || Bukkit.getOfflinePlayer(game.getHost()).getName().equalsIgnoreCase("LeonsPrivate")) {
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
			if (getTeamSize().startsWith("Open")) {
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
	 * Get the hof name for the given host.
	 * 
	 * @param host The host.
	 * @return The hof name.
	 */
	public static String getHost(String name) {
		Settings settings = Settings.getInstance();
		
		String host = PlayerUtils.getOfflinePlayer(name).getUniqueId().toString();
		
		for (String path : settings.getHOF().getKeys(false)) {
			if (path.equalsIgnoreCase(name)) {
				return name;
			}
		}
		
		if (host.equals("8b2b2e07-b694-4bd0-8f1b-ba99a267be41")) {
			return "Leon";
		} 
		
		if (host.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
			return "Polar";
		} 
		
		if (host.equals("eb4ac2dc-1459-4025-9741-37834dd514e2")) {
			return "Isaac";
		}
		
		if (host.equals("3be33527-be7e-4eb2-8b66-5b76d3d7ecdc")) {
			return "Axlur";
		}
		
		if (host.equals("37d49a6e-d4f9-4f89-84f6-fef07e55847f")) {
			return "Limit";
		}
		
		if (host.equals("dad4224c-287b-4475-b4b2-68d3509e9e42")) {
			return "Badfan";
		}
		
		if (host.equals("9c1feada-07b9-4880-81c3-196248ce6a73")) {
			return "BLA2K14";
		}
		
		if (host.equals("395b6cdd-6500-4a6d-a133-61d89b506512")) {
			return "MajorWoof";
		}
		
		if (host.equals("01f4fabc-beeb-46ea-8858-c593711a5688")) {
			return "Fazed";
		}
		
		if (host.equals("afd0f4b1-9102-46bd-b6b4-030528dab5a8")) {
			return "Pyro";
		}
		
		if (host.equals("3ed75695-83e1-48cc-9c1c-70a97b710843")) {
			return "Cubehh";
		}
		
		if (host.equals("573dd0a7-5303-4cd9-9e04-d31ae79403b6")) {
			return "FSP";
		}
		
		return name;
	}

	/**
	 * Get the hof name for the given host.
	 * 
	 * @param host The host.
	 * @return The hof name.
	 */
	public static String getHostUUID(String name) {
		if (name.equalsIgnoreCase("Leon")) {
			return "8b2b2e07-b694-4bd0-8f1b-ba99a267be41";
		} 
		
		if (name.equalsIgnoreCase("Polar")) {
			return "02dc5178-f7ec-4254-8401-1a57a7442a2f";
		} 
		
		if (name.equalsIgnoreCase("Isaac")) {
			return "eb4ac2dc-1459-4025-9741-37834dd514e2";
		}
		
		if (name.equalsIgnoreCase("Axlur")) {
			return "3be33527-be7e-4eb2-8b66-5b76d3d7ecdc";
		}
		
		if (name.equalsIgnoreCase("Limit")) {
			return "37d49a6e-d4f9-4f89-84f6-fef07e55847f";
		}
		
		if (name.equalsIgnoreCase("Badfan")) {
			return "dad4224c-287b-4475-b4b2-68d3509e9e42";
		}
		
		if (name.equalsIgnoreCase("BLA2K14")) {
			return "9c1feada-07b9-4880-81c3-196248ce6a73";
		}
		
		if (name.equalsIgnoreCase("MajorWoof")) {
			return "395b6cdd-6500-4a6d-a133-61d89b506512";
		}
		
		if (name.equalsIgnoreCase("Fazed")) {
			return "01f4fabc-beeb-46ea-8858-c593711a5688";
		}
		
		if (name.equalsIgnoreCase("Pyro")) {
			return "afd0f4b1-9102-46bd-b6b4-030528dab5a8";
		}
		
		if (name.equalsIgnoreCase("Cubehh")) {
			return "3ed75695-83e1-48cc-9c1c-70a97b710843";
		}
		
		if (name.equalsIgnoreCase("FSP")) {
			return "573dd0a7-5303-4cd9-9e04-d31ae79403b6";
		}
		
		return name;
	}
}