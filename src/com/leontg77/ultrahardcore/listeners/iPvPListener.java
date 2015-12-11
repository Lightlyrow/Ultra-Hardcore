package com.leontg77.ultrahardcore.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
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
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        
        Action action = event.getAction();
        ItemStack item = event.getItem();
        
        Spectator spec = Spectator.getInstance();
        
        if (action != Action.RIGHT_CLICK_BLOCK) {
        	return;
        }
        
        if (!State.isState(State.INGAME) || game.isRecordedRound()) {
        	return;
        }
        
        if (Timers.pvp <= 0) {
        	return;
        }
        
        if (item == null) {
        	return;
        }
        
    	if (item.getType() != Material.LAVA_BUCKET && item.getType() != Material.FLINT_AND_STEEL && item.getType() != Material.CACTUS) {
        	return;
    	}
		
		Team pTeam = TeamManager.getInstance().getTeam(player);
    	
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
			
			Team nearTeam = TeamManager.getInstance().getTeam(near);
			
			if (pTeam != null && nearTeam != null) {
				if (pTeam == nearTeam) {
					continue;
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "§c" + player.getName() + " §7attempted to iPvP §c" + near.getName(), "uhc.staff");
				
				player.sendMessage(Main.PREFIX + "iPvP is not allowed before PvP.");
				player.sendMessage(Main.PREFIX + "Stop iPvPing now or staff will take action.");
				
				item.setType(Material.AIR);
				event.setCancelled(true);
				break;
			}
		}
	}
}