package com.leontg77.ultrahardcore.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
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
	private static final BiMap<UUID, String> HOSTS_BY_UUID = ImmutableBiMap.<UUID, String>builder()
            .put(UUID.fromString("8b2b2e07-b694-4bd0-8f1b-ba99a267be41"), "Leon")
            .put(UUID.fromString("02dc5178-f7ec-4254-8401-1a57a7442a2f"), "Polar")
            .put(UUID.fromString("eb4ac2dc-1459-4025-9741-37834dd514e2"), "Isaac")
            .put(UUID.fromString("3be33527-be7e-4eb2-8b66-5b76d3d7ecdc"), "Axlur")
            .put(UUID.fromString("37d49a6e-d4f9-4f89-84f6-fef07e55847f"), "Limit")
            .put(UUID.fromString("dad4224c-287b-4475-b4b2-68d3509e9e42"), "Badfan")
            .put(UUID.fromString("9c1feada-07b9-4880-81c3-196248ce6a73"), "BLA2K14")
            .put(UUID.fromString("395b6cdd-6500-4a6d-a133-61d89b506512"), "MajorWoof")
            .put(UUID.fromString("01f4fabc-beeb-46ea-8858-c593711a5688"), "Fazed")
            .put(UUID.fromString("afd0f4b1-9102-46bd-b6b4-030528dab5a8"), "Pyro")
            .put(UUID.fromString("3ed75695-83e1-48cc-9c1c-70a97b710843"), "Cubehh")
            .put(UUID.fromString("573dd0a7-5303-4cd9-9e04-d31ae79403b6"), "FSP")
            .build();

    private static final BiMap<String, UUID> HOSTS_BY_NAME = HOSTS_BY_UUID.inverse();

    /**
     * Get the hof name for the given host.
     *
     * @param host The host.
     * @return The hof name.
     */
    public static String getHostConfigName(String name) {
        Settings settings = Settings.getInstance();

        for (String path : settings.getHOF().getKeys(false)) {
            if (path.equalsIgnoreCase(name.toLowerCase())) {
                return name;
            }
        }

        UUID uuid = PlayerUtils.getOfflinePlayer(name).getUniqueId();
        return Optional.fromNullable(HOSTS_BY_UUID.get(uuid)).or(name);
    }

    /**
     * Get the hof name for the given host.
     *
     * @param host The host.
     * @return The hof name.
     */
    public static UUID getHostConfigUUID(String name) {
        return HOSTS_BY_NAME.get(name);
    }
	
	public static String getHostName(UUID hostUUID) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(hostUUID);
		
		return player.getName() == null ? "None" : player.getName();
	}
	
	public static UUID getHostUUID(String name) {
		// the utils "bypasses" the deprecation.
		OfflinePlayer player = PlayerUtils.getOfflinePlayer(name);
		
		return player.getUniqueId();
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

		if (game.isRecordedRound() || getHostName(game.getHost()).equalsIgnoreCase("LeonsPrivate")) {
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
}