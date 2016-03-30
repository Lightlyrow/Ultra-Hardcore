package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.TeamJoinEvent;
import com.leontg77.ultrahardcore.events.TeamLeaveEvent;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * AloneTogether scenario class.
 * 
 * @author LeonTG77
 */
public class AloneTogether extends Scenario implements Listener {
	private final TeamManager teams;
	private final Game game;

	/**
	 * AloneTogether class constructor.
	 * 
	 * @param game The game class.
	 * @param teams The team manager.
	 */
	public AloneTogether(Game game, TeamManager teams) {
		super("AloneTogether", "Your teammates are vanished.");

		this.teams = teams;
		this.game = game;
	}
	
	@Override
	public void onEnable() {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		on(new GameStartEvent());
	}
	
	@EventHandler
	public void on(GameStartEvent event) {
		for (Player online : game.getPlayers()) {
			update(online);
		}
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!game.getPlayers().contains(player)) {
			return;
		}
		
		update(player);
	}
	
	@EventHandler
	public void on(TeamJoinEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		for (Player online : game.getPlayers()) {
			update(online);
		}
	}
	
	@EventHandler
	public void on(TeamLeaveEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		for (Player online : game.getPlayers()) {
			update(online);
		}
	}
	
	/**
	 * Update the given player and hide his teammates if any.
	 * 
	 * @param player The player to update for.
	 */
	@SuppressWarnings("deprecation")
	private void update(Player player) {
		Team team = teams.getTeam(player);
		
		if (team == null) {
			for (Player online : game.getPlayers()) {
				player.showPlayer(online);
			}
			return;
		}
		
		for (Player online : game.getPlayers()) {
			if (team.hasPlayer(online)) {
				player.hidePlayer(online);
			} else {
				player.showPlayer(online);
			}
		}
	}
}