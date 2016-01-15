package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class iPvPListener implements Listener {
	private static final String PREFIX = "§8[§4§liPvP§8] §7";
	
	private TeamManager manager = TeamManager.getInstance();
    private Spectator spec = Spectator.getInstance();
    
    @EventHandler
    public void on(final PlayerBucketEmptyEvent event) {
    	Block clicked = event.getBlockClicked();
    	BlockFace face = event.getBlockFace();
    	
    	Block block = clicked.getRelative(face);
    	Player player = event.getPlayer();
    
		if (event.getBucket() != Material.LAVA_BUCKET) {
			return;
		}
		
		if (!isAnyoneNearby(player, block.getLocation())) {
			return;
    	}
		
		player.sendMessage(PREFIX + "iPvP is not allowed before PvP.");
		player.sendMessage(PREFIX + "Stop iPvPing now or staff will take action.");
		
    	event.setCancelled(true);
    }
	
	@EventHandler
	public void on(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
        	return;
        }
        
        // if pvp is enabled we want them to be able to iPvP
        if (player.getWorld().getPVP()) {
        	return;
        }
        
    	if (block == null) {
        	return;
    	}
    	
    	if (isSuffocation(player, event) || isDamageBlock(player, event)) {
			player.sendMessage(PREFIX + "iPvP is not allowed before PvP.");
			player.sendMessage(PREFIX + "Stop iPvPing now or staff will take action.");
			
        	event.setCancelled(true);
    	}
	}
	
	@EventHandler
	public void on(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
        	return;
        }
        
        // if pvp is enabled we want them to be able to iPvP
        if (player.getWorld().getPVP()) {
        	return;
        }
        
    	if (block == null) {
        	return;
    	}
        
    	if (isSpleef(player, event) || isBreakSuffocation(player, event)) {
			player.sendMessage(PREFIX + "iPvP is not allowed before PvP.");
			player.sendMessage(PREFIX + "Stop iPvPing now or staff will take action.");
			
        	event.setCancelled(true);
    	}
	}

	private boolean isDamageBlock(Player iPvPer, BlockPlaceEvent event) {
		Block block = event.getBlock();
		boolean isiPvPBlock = false;
		
		switch (block.getType()) {
		case CACTUS:
		case FIRE:
			isiPvPBlock = true;
			break;
		default:
			isiPvPBlock = false;
			break;
		}
		
		return isiPvPBlock && isAnyoneNearby(iPvPer, block.getLocation());
	}

	private boolean isBreakSuffocation(Player iPvPer, BlockBreakEvent event) {
		Block block = event.getBlock();
		Location loc = block.getLocation();
		
		boolean isPlayerBelow = false;
		boolean isFallingBlock;

		Team playerTeam = manager.getTeam(iPvPer);
		
		Block above = block.getRelative(BlockFace.UP, 1);
		
		switch (above.getType()) {
		case SAND:
		case GRAVEL:
		case ANVIL:
			isFallingBlock = true;
			break;
		default:
			isFallingBlock = false;
			return false;
		}
		
		for (Player inWorld : iPvPer.getWorld().getPlayers()) {
			if (inWorld == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(inWorld)) {
				continue;
			}
			
			Team nearTeam = manager.getTeam(inWorld);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}
			
			Location pLoc = inWorld.getLocation();
			
			if (pLoc.getBlockX() == loc.getBlockX() && pLoc.getBlockY() < loc.getBlockY() && loc.getBlockZ() == pLoc.getBlockZ()) {
				PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-» §a" + inWorld.getName() + " §8[§7Suffocation§8]", "uhc.staff");
				isPlayerBelow = true;
				break;
			}
		}
		
		return isFallingBlock && isPlayerBelow;
	}
	
	private boolean isSuffocation(Player iPvPer, BlockPlaceEvent event) {
		Block block = event.getBlock();
		Location loc = block.getLocation();
		
		boolean isPlayerBelow = false;
		boolean isFallingBlock;

		Team playerTeam = manager.getTeam(iPvPer);
		
		switch (block.getType()) {
		case SAND:
		case GRAVEL:
		case ANVIL:
			isFallingBlock = true;
			break;
		default:
			isFallingBlock = false;
			return false;
		}
		
		Block below = block.getRelative(BlockFace.DOWN, 1);
		
		for (Player inWorld : iPvPer.getWorld().getPlayers()) {
			if (inWorld == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(inWorld)) {
				continue;
			}
			
			Team nearTeam = manager.getTeam(inWorld);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}
			
			Location pLoc = inWorld.getLocation();
			
			if (below.getType() == Material.AIR && pLoc.getBlockX() == loc.getBlockX() && pLoc.getBlockY() < loc.getBlockY() && loc.getBlockZ() == pLoc.getBlockZ()) {
				PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-» §a" + inWorld.getName() + " §8[§7" + NameUtils.capitalizeString(block.getType().name(), true) + "§8]", "uhc.staff");
				isPlayerBelow = true;
				break;
			}
		}
		
		return below.getType() == Material.AIR && isFallingBlock && isPlayerBelow;
	}

	private boolean isSpleef(Player iPvPer, BlockBreakEvent event) {
		Block block = event.getBlock();
		Location loc = block.getLocation();

		Team playerTeam = manager.getTeam(iPvPer);
		boolean isPlayerAbove = false;
		
		for (Player inWorld : iPvPer.getWorld().getPlayers()) {
			if (inWorld == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(inWorld)) {
				continue;
			}
			
			Team nearTeam = manager.getTeam(inWorld);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}
			
			Location pLoc = inWorld.getLocation();
			
			if (pLoc.getBlockX() == loc.getBlockX() && pLoc.getBlockY() == loc.clone().add(0, 1, 0).getBlockY() && loc.getBlockZ() == pLoc.getBlockZ()) {
				PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-» §a" + inWorld.getName() + " §8[§7Spleef§8]", "uhc.staff");
				isPlayerAbove = true;
				break;
			}
		}
		
		return isPlayerAbove;
	}

	private boolean isAnyoneNearby(Player iPvPer, Location loc) {
		Team playerTeam = manager.getTeam(iPvPer);
    	
    	for (Entity near : PlayerUtils.getNearby(loc, 10)) {
			if (!(near instanceof Player)) {
				continue;
			}
			
			Player nearby = (Player) near;
			
			if (nearby == iPvPer) {
				continue;
			}
			
			if (spec.isSpectating(nearby)) {
				continue;
			}
			
			Team nearTeam = manager.getTeam(nearby);
			
			if (playerTeam != null && playerTeam.equals(nearTeam)) {
				continue;
			}

			PlayerUtils.broadcast(PREFIX + "§c" + iPvPer.getName() + " §8-» §a" + nearby.getName() + " §8[§7" + NameUtils.capitalizeString(iPvPer.getItemInHand().getType().name(), true) + "§8]", "uhc.staff");
			return true;
		}
    	
    	return false;
	}
}