package com.leontg77.ultrahardcore.feature.scoreboard;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * Sidebar reset feature class.
 * 
 * @author LeonTG77
 */
public class SidebarResetFeature extends ToggleableFeature implements Listener {

	public SidebarResetFeature() {
		super("Sidebar Reset", "Make dead players sidebar score get reset when they die.");
		
		icon.setType(Material.SIGN);
		slot = 5;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void on(final PlayerDeathEvent event) {
		if (!isEnabled() || game.isRecordedRound()) {
			return;
		}
		
		final Player player = event.getEntity();
		final Arena arena = Arena.getInstance();
		
		// the arena has it's own way of doing deaths.
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 

		final BoardManager board = BoardManager.getInstance();
		final List<World> worlds = game.getWorlds();
	    
	    // I don't care about the rest if it hasn't started or they're not in a game world.
	    if (!State.isState(State.INGAME) || !worlds.contains(player.getWorld())) {
	    	return;
	    }

		final Player killer = player.getKiller();

		if (killer == null) {
	        board.resetScore(player.getName());
			return;
		}
		
		final Team pteam = TeamManager.getInstance().getTeam(player);
		final Team team = TeamManager.getInstance().getTeam(killer);
		
		if (pteam != null && pteam.equals(team)) {
			return;
		}
		
		board.resetScore(player.getName());
	}
}