package com.leontg77.uhc.scenario.scenarios;

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

import com.leontg77.uhc.Main;
import com.leontg77.uhc.managers.BoardManager;
import com.leontg77.uhc.managers.TeamManager;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

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
				for (Player online : PlayerUtils.getPlayers()) {	
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
    public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Team team = TeamManager.getInstance().getTeam(player);

        if (team == null) {
        	return;
        }
        
        team.removeEntry(player.getName());
    }
}