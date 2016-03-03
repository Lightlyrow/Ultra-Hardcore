package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * TeamHealth scenario class
 * 
 * @author LeonTG77
 */
public class TeamHealth extends Scenario implements Listener {
	public BukkitRunnable task = null;

	private Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	private Objective teamHealthName = board.getObjective("teamHealthName");
	private Objective teamHealth = board.getObjective("teamHealth");
	
	public TeamHealth() {
		super("TeamHealth", "The percent health shown in tab/bellow the name is all the teammates health combined.");
		
		teamHealthName = board.getObjective("teamHealthName");
		teamHealth = board.getObjective("teamHealth");
	}

	@Override
	public void onDisable() {
		if (teamHealthName != null) {
			teamHealthName.unregister();
		}
		
		if (teamHealth != null) {
			teamHealth.unregister();
		}
		
		task.cancel();
		task = null;
		
		// setup the original scoreboard.
		BoardManager.getInstance().setup();
	}

	@Override
	public void onEnable() {
		if (State.isState(State.INGAME)) {
			on(new GameStartEvent());
		}
	}
	
	@EventHandler
    public void on(GameStartEvent event) {
		if (teamHealthName == null) {
			teamHealthName = board.registerNewObjective("teamHealthName", "dummy");
		}
		
		if (teamHealth == null) {
			teamHealth = board.registerNewObjective("teamHealth", "dummy");
		}
		
		teamHealthName.setDisplaySlot(DisplaySlot.BELOW_NAME);
		teamHealthName.setDisplayName("§4♥");
		
		teamHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : Bukkit.getOnlinePlayers()) {	
					Team team = TeamManager.getInstance().getTeam(online);
					
					double health = 0;

					if (team == null) {
						health = online.getHealth();
					} else {
						for (String entry : team.getEntries()) {
							Player target = Bukkit.getServer().getPlayer(entry);
							
							if (target != null) {
								health = health + target.getHealth();
							}
						}
					}
					
					int percent = Integer.parseInt(NumberUtils.makePercent(health).substring(2));
					
					if (teamHealth != null) {
						Score score = teamHealth.getScore(online.getName());
						score.setScore(percent);
					}
					
					if (teamHealthName != null) {
						Score score = teamHealthName.getScore(online.getName());
						score.setScore(percent);
					}
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 1, 1);
    }
	
	@EventHandler
    public void on(PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getEntity();
		
		TeamManager manager = TeamManager.getInstance();
		Team team = manager.getTeam(player);

        if (team == null) {
        	return;
        }
        
        manager.leaveTeam(player, false);
    }
}