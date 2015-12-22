package com.leontg77.ultrahardcore.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main;

/**
 * Team management class.
 * <p>
 * Contains methods for adding players to a team, removing players from a team, getting the team for a player and setting up teams.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class TeamManager {
	private Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
	private static TeamManager manager = new TeamManager();

	public static HashMap<String, Set<String>> savedTeams = new HashMap<String, Set<String>>();
	private List<Team> teams = new ArrayList<Team>();
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static TeamManager getInstance() {
		return manager;
	}
	
	/**
	 * Set up all the teams.
	 */
	public void setup() {
		teams.clear();
		ArrayList<String> list = new ArrayList<String>();

		list.add(ChatColor.BLACK.toString());
		list.add(ChatColor.DARK_BLUE.toString());
		list.add(ChatColor.DARK_GREEN.toString());
		list.add(ChatColor.DARK_AQUA.toString());
		list.add(ChatColor.DARK_RED.toString());
		list.add(ChatColor.DARK_PURPLE.toString());
		list.add(ChatColor.GOLD.toString());
		list.add(ChatColor.GRAY.toString());
		list.add(ChatColor.DARK_GRAY.toString());
		list.add(ChatColor.BLUE.toString());
		list.add(ChatColor.GREEN.toString());
		list.add(ChatColor.AQUA.toString());
		list.add(ChatColor.RED.toString());
		list.add(ChatColor.LIGHT_PURPLE.toString());
		list.add(ChatColor.YELLOW.toString());
		list.add(ChatColor.WHITE.toString());

		Collections.shuffle(list);
		
		ArrayList<String> tempList = new ArrayList<String>();
		
		for (String li : list) {
			tempList.add(li + ChatColor.ITALIC);
		}
		
		for (String li : list) {
			tempList.add(li + ChatColor.BOLD);
		}
		
		for (String li : list) {
			tempList.add(li + ChatColor.UNDERLINE);
		}
		
		for (String li : list) {
			tempList.add(li + ChatColor.STRIKETHROUGH);
		}
		
		tempList.remove(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString());

		list.addAll(tempList);
		
		Team spec = (getTeam("spec") == null ? sb.registerNewTeam("spec") : sb.getTeam("spec"));
		spec.setDisplayName("spec");
		spec.setPrefix("§7§o");
		spec.setSuffix("§f");
		
		spec.setAllowFriendlyFire(false);
		spec.setCanSeeFriendlyInvisibles(true);	
		spec.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		
		for (int i = 0; i < list.size(); i++) {
			String teamName = "UHC" + (i + 1);
			
			Team team = (sb.getTeam(teamName) == null ? sb.registerNewTeam(teamName) : sb.getTeam(teamName));
			team.setDisplayName(teamName);
			team.setPrefix(list.get(i));
			team.setSuffix("§f");
			
			team.setAllowFriendlyFire(true);
			team.setCanSeeFriendlyInvisibles(true);
			team.setNameTagVisibility(NameTagVisibility.ALWAYS);
			
			teams.add(team);
		}

		Bukkit.getLogger().info("[UHC] Setup " + (teams.size() + 1) + " teams.");
	}
	
	/**
	 * Find the first available team.
	 * 
	 * @return The available team, null if not found.
	 */
	public Team findAvailableTeam() {
		for (Team team : getTeams()) {
			if (team.getSize() == 0) {
				return team;
			}
		}
		return null;
	}

	/**
	 * Joins the specified team for the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param team the team joining.
	 * @param player the player joining.
	 */
	public void joinTeam(Team team, OfflinePlayer player) {	
		if (team == null) {
			return;
		}
		
		if (!savedTeams.containsKey(team.getName())) {
			savedTeams.put(team.getName(), team.getEntries());
		}
		
		team.addPlayer(player);
		savedTeams.get(team.getName()).add(player.getName());
	}

	/**
	 * Joins the specified team for the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param name the name of the team joining.
	 * @param player the player joining.
	 */
	public void joinTeam(String name, OfflinePlayer player) {	
		Team team = getTeam(name);
		joinTeam(team, player);
	}
	
	/**
	 * Leaves the current team of the given player.
	 * <p>
	 * If the team is null nothing will happen.
	 * 
	 * @param player the player thats leaving the team.
	 */
	public void leaveTeam(final OfflinePlayer player) {
		// wait a tick, incase they got removed after death but other events with higher priority wants to use the team.
		new BukkitRunnable() {
			public void run() {
				Team team = getTeam(player);
				
				if (team != null) {
					team.removePlayer(player);
					
					if (!savedTeams.containsKey(team.getName())) {
						savedTeams.put(team.getName(), team.getEntries());
					} else {
						savedTeams.get(team.getName()).remove(player.getName());
					}
				}
			}
		}.runTaskLater(Main.plugin, 1);
	}
	
	/**
	 * Get the players on the given team.
	 * 
	 * @param team The given team.
	 * @return A list of OfflinePlayer's on the team.
	 */
	public Set<OfflinePlayer> getPlayers(Team team) {
		return team.getPlayers();
	}
	
	/**
	 * Sends a message to everyone on the given team.
	 * 
	 * @param team the team.
	 * @param message the message to send.
	 */
	public void sendMessage(Team team, String message) {
		for (String entry : team.getEntries()) {
			Player teammate = Bukkit.getServer().getPlayer(entry);
			
			if (teammate != null) {
				teammate.sendMessage(message);
			}
		}
	}

	/**
	 * Gets the team of the given player player.
	 * 
	 * @param player the player in the team.
	 * @return The team, null if the player isn't on a team.
	 */
	public Team getTeam(OfflinePlayer player) {
		return sb.getPlayerTeam(player);
	}

	/**
	 * Gets the team by a name.
	 * 
	 * @param name the name.
	 * @return The team.
	 */
	public Team getTeam(String name) {
		for (Team team : sb.getTeams()) {
			if (team.getName().equalsIgnoreCase(name)) {
				return team;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a list of all teams.
	 * 
	 * @return A list of all teams.
	 */
	public HashMap<String, Set<String>> getSavedTeams() {
		return savedTeams;
	}
	
	/**
	 * Get a list of all teams.
	 * 
	 * @return A list of all teams.
	 */
	public List<Team> getTeams() {
		return teams;
	}
	
	/**
	 * Get a list of all teams that has players on it.
	 * 
	 * @return A list of teams with players.
	 */
	public List<Team> getTeamsWithPlayers() {
		List<Team> teamsWithPlayers = new ArrayList<Team>();
		
		for (Team team : teams) {
			if (team.getSize() > 0) {
				teamsWithPlayers.add(team);
			}
		}
		
		return teamsWithPlayers;
	}
}