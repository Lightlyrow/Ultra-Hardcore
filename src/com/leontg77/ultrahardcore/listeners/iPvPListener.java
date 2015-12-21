package com.leontg77.ultrahardcore.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
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

        ItemStack item = event.getItemInHand();
		Block block = event.getBlock();
        
        TeamManager manager = TeamManager.getInstance();
        Spectator spec = Spectator.getInstance();
        
        // silly, no spectators should trigger this (since they have a lava bucket in their inv)
        if (spec.isSpectating(player)) {
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
        
    	if (isIPvP(event)) {
        	event.setCancelled(true);
    	}
		
		Team playerTeam = manager.getTeam(player);
		playerTeam.getClass();
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
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isSpleef(BlockEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}