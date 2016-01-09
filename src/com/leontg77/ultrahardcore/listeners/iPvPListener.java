package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class iPvPListener implements Listener {
	private Game game = Game.getInstance();
    
	private TeamManager manager = TeamManager.getInstance();
    private Spectator spec = Spectator.getInstance();
	
	/* Not needed for now.
	 * 
	 * @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Action action = event.getAction();
        Player player = event.getPlayer();
        
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();
        
        TeamManager manager = TeamManager.getInstance();
        Spectator spec = Spectator.getInstance();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
        	return;
        }
        
        if (action != Action.RIGHT_CLICK_BLOCK) {
        	return;
        }
        
        if (!State.isState(State.INGAME) || game.isRecordedRound()) {
        	return;
        }
        
        if (Timers.pvp <= 0) {
        	return;
        }
        
    	if (item == null || block == null) {
        	return;
    	}
        
    	if (!isIPvP(event)) {
        	return;
    	}
		
		Team playerTeam = manager.getTeam(player);
    	
    	for (Entity nearby : PlayerUtils.getNearby(event.getClickedBlock().getLocation(), 5)) {
			if (!(nearby instanceof Player)) {
				continue;
			}
			
			Player near = (Player) nearby;
			
			if (near == player) {
				continue;
			}
			
			if (spec.isSpectating(near)) {
				continue;
			}
			
			Team nearTeam = manager.getTeam(near);
			
			if (playerTeam != null && nearTeam != null) {
				if (playerTeam == nearTeam) {
					continue;
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "§c" + player.getName() + " §7attempted to iPvP �c" + near.getName(), "uhc.staff");
				
				player.sendMessage(Main.PREFIX + "iPvP is not allowed before PvP.");
				player.sendMessage(Main.PREFIX + "Stop iPvPing now or staff will take action.");
				
				item.setType(Material.AIR);
				event.setCancelled(true);
				break;
			}
		}
	}*/
	
	@EventHandler
	public void on(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
        	return;
        }
        
        // do not care before start or if its an RR
        if (game.isRecordedRound() || !State.isState(State.INGAME)) {
        	return;
        }
        
        // if pvp is enabled we want them to be able to iPvP
        if (!player.getWorld().getPVP()) {
        	return;
        }
        
    	if (block == null) {
        	return;
    	}
        
    	if (isIPvP(event) && isAnyoneNearby(player, block.getLocation())) {
			player.sendMessage(Main.PREFIX + "iPvP is not allowed before PvP.");
			player.sendMessage(Main.PREFIX + "Stop iPvPing now or staff will take action.");
			
        	event.setCancelled(true);
    	}
	}

	private boolean isIPvP(BlockEvent event) {
		return isSpleef(event) || isSuffocation(event) || isDamageBlock(event);
	}

	private boolean isDamageBlock(BlockEvent event) {
		Block block = event.getBlock();
		
		switch (block.getType()) {
		case LAVA_BUCKET:
		case CACTUS:
		case FIRE:
			return true;
		default:
			return false;
		}
	}

	private boolean isSuffocation(BlockEvent event) {
		Block block = event.getBlock();
		boolean isFallingBlock = false;
		
		switch (block.getType()) {
		case SAND:
		case GRAVEL:
		case ANVIL:
			isFallingBlock = true;
		default:
			isFallingBlock = false;
		}
		
		return block.getRelative(BlockFace.DOWN).getType() == null && isFallingBlock;
	}

	private boolean isSpleef(BlockEvent event) {
		// TODO: Code this.
		return false;
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
			
			if (playerTeam == null || !playerTeam.equals(nearTeam)) {
				PlayerUtils.broadcast(Main.PREFIX + "§c" + iPvPer.getName() + " §7attempted to iPvP §c" + near.getName(), "uhc.staff");
				return true;
			}
		}
    	
    	return false;
	}
}