package com.leontg77.ultrahardcore.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.GameUtils;

/**
 * Stats listener class.
 * 
 * @author LeonTG77
 */
public class StatsListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void on(final PlayerDeathEvent event) {
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
	
	@EventHandler(ignoreCancelled = true)
    public void on(EntityDeathEvent event) {
		final LivingEntity entity = event.getEntity();
    	final Player killer = entity.getKiller();
    	
    	if (killer == null) {
    		return;
    	}
    	
    	final User user = User.get(killer);
		
		if (entity instanceof Monster) {
			user.increaseStat(Stat.HOSTILEMOBKILLS);
		}
		
		if (entity instanceof Animals) {
			user.increaseStat(Stat.ANIMALKILLS);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
		final Player player = event.getPlayer();
    	final User user = User.get(player);
		
		final Block block = event.getBlock();
    	
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
	
	@EventHandler(ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent event) {
		final Entity attacked = event.getEntity();
		final Entity attacker = event.getDamager();
    	
		if (!(attacked instanceof Player) || !(attacker instanceof Arrow)) {
			return;
		}
		
		final Player player = (Player) attacked;
		final Arrow arrow = (Arrow) attacker;
		
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		
		final Player killer = (Player) arrow.getShooter();
		final double distance = killer.getLocation().distance(player.getLocation());
		
		final TeamManager manager = TeamManager.getInstance();

		final Team kTeam = manager.getTeam(killer);
		final Team pTeam = manager.getTeam(player);
		
		// no stats boosting for teammates.
		if (kTeam != null && kTeam.equals(pTeam)) {
			return;
		}
		
		final User user = User.get(killer);
		
		if (user.getStatDouble(Stat.LONGESTSHOT) <= distance) {
			user.setStat(Stat.LONGESTSHOT, distance);
		}
	}
    
    @EventHandler(ignoreCancelled = true)
    public void on(EntityTameEvent event) {
    	final Player player = (Player) event.getOwner();
    	final User user = User.get(player);
    	
    	if (event.getEntity() instanceof Wolf) {
    		user.increaseStat(Stat.WOLVESTAMED);
    		return;
    	}
    	
    	if (event.getEntity() instanceof Horse) {
    		user.increaseStat(Stat.HORSESTAMED);
    	}
    }
	
	@EventHandler(ignoreCancelled = true)
	public void on(PlayerLevelChangeEvent event) {
		final Player player = event.getPlayer();
    	final User user = User.get(player);
    	
    	int oldL = event.getOldLevel();
    	int newL = event.getNewLevel();
    	
    	if (oldL >= newL) {
    		return;
    	}
		
		user.setStat(Stat.LEVELS, user.getStat(Stat.LEVELS) + (newL - oldL));
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final User user = User.get(player);
		
		final FeatureManager manager = FeatureManager.getInstance();
		final ItemStack item = event.getItem();
		
		final GoldenHeadsFeature ghead = manager.getFeature(GoldenHeadsFeature.class);
		
		if (item.getType() == Material.GOLDEN_APPLE) {
			if (ghead.isGoldenHead(item)) {
				user.increaseStat(Stat.GOLDENHEADSEATEN);
			} else {
				user.increaseStat(Stat.GOLDENAPPLESEATEN);
			}
			return;
		}
		
		if (item.getType() == Material.POTION && item.getDurability() != 0) {
			user.increaseStat(Stat.POTIONS);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		final Player player = event.getPlayer();

		final User user = User.get(player);
    	final Location to = event.getTo();
		
		if (to == null) {
			return;
		}
		
		switch (to.getWorld().getEnvironment()) {
		case NETHER:
	    	user.increaseStat(Stat.NETHER);
			break;
		case THE_END:
	    	user.increaseStat(Stat.END);
			break;
		default:
			break;
		}
    }
}