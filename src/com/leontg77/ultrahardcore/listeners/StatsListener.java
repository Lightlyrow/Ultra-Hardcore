package com.leontg77.ultrahardcore.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.GameUtils;

public class StatsListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Arena arena = Arena.getInstance();
		
		// the arena has it's own way of doing deaths.
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 

		final List<World> worlds = GameUtils.getGameWorlds();
		
	    // I don't care about the rest they're not in a game world.
	    if (!worlds.contains(player.getWorld())) {
	    	return;
	    }

		final BoardManager board = BoardManager.getInstance();
		final User user = User.get(player);
		
		final Player killer = player.getKiller();

		if (killer == null) {
			user.increaseStat(Stat.DEATHS);
			return;
		}
		
		final Team pteam = TeamManager.getInstance().getTeam(player);
		final Team team = TeamManager.getInstance().getTeam(killer);
		
		if (pteam != null && pteam.equals(team)) {
			return;
		}
		
		user.increaseStat(Stat.DEATHS);
		
		final User killUser = User.get(killer);
		killUser.increaseStat(Stat.KILLS);
		
		if (killUser.getStat(Stat.KILLSTREAK) < board.getScore(killer.getName())) {
			killUser.setStat(Stat.KILLSTREAK, board.getScore(killer.getName()));
		}
	}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
		User user = User.get(player);
		
		Block block = event.getBlock();
    	
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (block.getType() == Material.DIAMOND_ORE) {
			user.increaseStat(Stat.DIAMONDS);
			return;
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			user.increaseStat(Stat.GOLD);
		}
    }
}