package com.leontg77.ultrahardcore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.PacketUtils;

/**
 * Game management class.
 * <p>
 * This class contains all setters and getters for all togglable features.
 * 
 * @author LeonTG77
 */
public class Game {
	private final Settings settings;
	private final GUIManager gui;
	
	private final BoardManager board;
	private final SpecManager spec;
	
	/**
	 * Game class constructor.
	 * 
	 * @param settings The settings class.
	 * @param gui The InvGUI class.
	 * @param board The board manager class.
	 * @param spec The spectator manager clas.
	 */
	public Game(Settings settings, GUIManager gui, BoardManager board, SpecManager spec) {
		this.settings = settings;
		this.gui = gui;

		this.board = board;
		this.spec = spec;
	}
	
	private Timer timer;
	
	/**
	 * Set the instance of the timer to the givne instance.
	 * 
	 * @param game The timer instance.
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	/**
	 * Get all the worlds being used by the game.
	 * 
	 * @return A list of game worlds.
	 */
	public List<World> getWorlds() {
		List<World> worlds = new ArrayList<World>();
		World main = getWorld();
		
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
	
	/**
	 * Get a list of all the players playing the game.
	 * 
	 * @return List of game players.
	 */
	public List<Player> getPlayers() {
		final List<Player> list = new ArrayList<Player>();
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (spec.isSpectating(online) || !getWorlds().contains(online.getWorld())) {
				continue;
			}
			
			list.add(online);
		}
		
		return list;
	}
	
	/**
     * Get the hof name for the current host.
    
     * @return The hof name.
     */
    public String getHostHOFName() {
    	return getHostHOFName(getHost());
    }
	
	/**
     * Get the hof name for the given host.
     *
     * @param host The host.
     * @return The hof name.
     */
    public String getHostHOFName(String name) {
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
	 * Get the teamsize in a string format.
	 * 
	 * @param advancedFFA Wether to spell it as "FFA" or "Free for all"
	 * @param seperate Wether to have the returned string end with - or not.
	 * @return The string format.
	 */
	public String getAdvancedTeamSize(boolean advancedFFA, boolean seperate) {
		return getAdvancedTeamSize(getTeamSize(), advancedFFA, seperate);
	}

	/**
	 * Get the teamsize in a string format.
	 * 
	 * @param teamsize The teamsize to use.
	 * @param advancedFFA Wether to spell it as "FFA" or "Free for all"
	 * @param seperate Wether to have the returned string end with - or not.
	 * @return The string format.
	 */
	public String getAdvancedTeamSize(String teamsize, boolean advancedFFA, boolean seperate) {
		if (teamsize.startsWith("To")) {
			setTeamSize("c" + getTeamSize());
		}
		
		final String seperator = seperate ? " - " : " ";
		
		if (teamsize.startsWith("FFA")) {
			return (advancedFFA ? "Free for all" : "FFA") + seperator;
		} 
		
		if (teamsize.startsWith("rTo")) {
			return "Random " + teamsize.substring(1) + seperator;
		} 
		
		if (teamsize.startsWith("cTo")) {
			return "Chosen " + teamsize.substring(1) + seperator;
		} 
		
		if (teamsize.startsWith("mTo")) {
			return "Mystery " + teamsize.substring(1) + seperator;
		} 
		
		if (teamsize.startsWith("pTo")) {
			return "Picked " + teamsize.substring(1) + seperator;
		} 
		
		if (teamsize.startsWith("CapTo")) {
			return "Captains " + teamsize.substring(3) + seperator;
		} 
		
		if (teamsize.startsWith("AucTo")) {
			return "Auction " + teamsize.substring(3) + seperator;
		} 
		
		if (teamsize.startsWith("No") || teamsize.startsWith("Open")) {
			return teamsize + " ";
		}
		
		return null;
	}

	/**
	 * Enable or disable pre whitelists.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setPreWhitelists(boolean enable) {
		settings.getConfig().set("misc.prewl", enable);
		settings.saveConfig();
	}

	/**
	 * Check if pre whitelists are enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean preWhitelists() {
		return settings.getConfig().getBoolean("misc.prewl", true);
	}
	
	/**
	 * Set the team size of the game.
	 * 
	 * @param teamSize the new teamsize.
	 */
	public void setTeamSize(String teamSize) {
		if (pregameBoard()) {
			board.resetScore("§8» §7" + getAdvancedTeamSize(true, false));
		}
		
		settings.getConfig().set("teamsize", teamSize);
		settings.saveConfig();
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			PacketUtils.setTabList(online, this);
		}
		
		if (pregameBoard()) {
			board.setScore("§8» §7" + getAdvancedTeamSize(true, false), 6);
		}
		
		gui.getGUI(GameInfoGUI.class).update();
	}
	
	/**
	 * Get the game teamsize.
	 * 
	 * @return The teamsize.
	 */
	public String getTeamSize() {
		return settings.getConfig().getString("teamsize", "No");
	}
	
	/**
	 * Set the scenarios of the game.
	 * 
	 * @param scenarios The new scenarios.
	 */
	public void setScenarios(String scenarios) {
		if (pregameBoard()) {
			for (String scen : getScenarios().split(", ")) {
				board.resetScore("§8» §7" + scen);
			}
			
			for (String scen : scenarios.split(", ")) {
				board.setScore("§8» §7" + scen, 3);
			}
		}
		
		settings.getConfig().set("scenarios", scenarios);
		settings.saveConfig();
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			PacketUtils.setTabList(online, this);
		}
		
		gui.getGUI(GameInfoGUI.class).update();
	}

	public String getScenarios() {
		return settings.getConfig().getString("scenarios", "games running");
	}
	
	/**
	 * Set the host of the game.
	 * 
	 * @param name The new host.
	 */
	public void setHost(String name) {
		settings.getConfig().set("host", name);
		settings.saveConfig();
		
		if (!isRecordedRound()) {
			board.getKillsObjective().setDisplayName("§4Arctic §8» §7§o" + name.substring(0, Math.min(name.length(), 13)) + "§r");
		}
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			PacketUtils.setTabList(online, this);
		}

		gui.getGUI(GameInfoGUI.class).update();
	}

	/**
	 * Get the host of the game.
	 * 
	 * @return The host.
	 */
	public String getHost() {
		return settings.getConfig().getString("host", "None");
	}

	/**
	 * Set the max players that will be able to join.
	 * 
	 * @param maxplayers The max player limit.
	 */
	public void setMaxPlayers(int maxplayers) {
		settings.getConfig().set("maxplayers", maxplayers);
		settings.saveConfig();

		gui.getGUI(GameInfoGUI.class).update();
	}
	
	/**
	 * Get the max players that can join the server.
	 * 
	 * @return The max player limit.
	 */
	public int getMaxPlayers() {
		return settings.getConfig().getInt("maxplayers", 150);
	}

	/**
	 * Set the matchpost for the game.
	 * 
	 * @param matchpost The new matchpost.
	 */
	public void setMatchPost(String matchpost) {
		settings.getConfig().set("matchpost", matchpost);
		settings.saveConfig();

		gui.getGUI(GameInfoGUI.class).update();
	}

	/**
	 * Get the matchpost for the game.
	 * 
	 * @return The game's matchpost.
	 */
	public String getMatchPost() {
		return settings.getConfig().getString("matchpost", "No_Post_Set");
	}
	
	/**
	 * Set the world to be used for the game.
	 * <p> 
	 * This will automaticly use all the same worlds 
	 * that has the world name with _end or _nether at the end.
	 * 
	 * @param name The new world name.
	 */
	public void setWorld(String name) {
		settings.getConfig().set("world", name);
		settings.saveConfig();

		gui.getGUI(GameInfoGUI.class).update();
	}

	/**
	 * Get the world to be used for the game.
	 * 
	 * @return The game world
	 */
	public World getWorld() {
		return Bukkit.getWorld(settings.getConfig().getString("world", "girhgqeruiogh"));
	}

	/**
	 * Set the time of the pvp for the game.
	 * 
	 * @param meetup The time in minutes.
	 */
	public void setPvP(int pvp) {
		settings.getConfig().set("timer.pvp", pvp);
		settings.saveConfig();

		timer.setPvP(pvp);
		gui.getGUI(GameInfoGUI.class).update();
	}

	/**
	 * Get the time when pvp will be enabled after the start.
	 * 
	 * @return The time in minutes.
	 */
	public int getPvP() {
		return settings.getConfig().getInt("timer.pvp", 15);
	}

	/**
	 * Set the time of the meetup of the game.
	 * 
	 * @param meetup The time in minutes.
	 */
	public void setMeetup(int meetup) {
		settings.getConfig().set("timer.meetup", meetup);
		settings.saveConfig();
		
		timer.setMeetup(meetup);
		gui.getGUI(GameInfoGUI.class).update();
	}

	/**
	 * Get the time when meetup will occur after the start.
	 * 
	 * @return The time in minutes.
	 */
	public int getMeetup() {
		return settings.getConfig().getInt("timer.meetup", 90);
	}

	/**
	 * Enable or disable team management.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setTeamManagement(boolean enable) {
		settings.getConfig().set("misc.team", enable);
		settings.saveConfig();
	}
	
	/**
	 * Get if team management is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean teamManagement() {
		return settings.getConfig().getBoolean("misc.team", false);
	}

	/**
	 * Enable or disable the pregame board.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setPregameBoard(boolean enable) {
		settings.getConfig().set("misc.board.pregame", enable);
		settings.saveConfig();
	}

	/**
	 * Get if the pregame board is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean pregameBoard() {
		return settings.getConfig().getBoolean("misc.board.pregame", false);
	}

	/**
	 * Enable or disable the arena board.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setArenaBoard(boolean enable) {
		settings.getConfig().set("misc.board.arena", enable);
		settings.saveConfig();
	}

	/**
	 * Get if the arena board is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean arenaBoard() {
		return settings.getConfig().getBoolean("misc.board.arena", false);
	}

	/**
	 * Enable or disable recordedround mode.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setRecordedRound(boolean enable) {
		settings.getConfig().set("recordedround", enable);
		settings.saveConfig();
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			PacketUtils.setTabList(online, this);
		}
		
		if (enable) {
			board.getKillsObjective().setDisplayName("Kills");
		} else {
			board.getKillsObjective().setDisplayName("§4Arctic §8» §7§o" + getHost().substring(0, Math.min(getHost().length(), 13)) + "§r");
		}
	}

	/**
	 * Get if recordedround mode is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isRecordedRound() {
		return settings.getConfig().getBoolean("recordedround", false);
	}

	/**
	 * Get if the game is a private game.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isPrivateGame() {
		return getHost().equals("PrivateGame");
	}

	/**
	 * Mute or unmute the chat
	 * 
	 * @param mute True to mute, false to unmute.
	 */
	public void setMuted(boolean mute) {
		settings.getConfig().set("misc.muted", mute);
		settings.saveConfig();
	}

	/**
	 * Check if the chat is muted.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isMuted() {
		return settings.getConfig().getBoolean("misc.muted", false);
	}
}