package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DragonRush scenario class
 * 
 * @author LeonTG77
 */
public class DragonRush extends Scenario implements Listener {
	private int placed = 0;
	
	public DragonRush() {
		super("DragonRush", "The first team to kill the dragon wins the game.");
	}

	@Override
	public void onDisable() {
		placed = 0;
	}

	@Override
	public void onEnable() {
		placed = 0;
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		ItemStack item = event.getItem();

        if (block == null) {
        	return;
        }
        
        if (item == null) {
        	return;
        }
		
		Action action = event.getAction();
        Player player = event.getPlayer();
    	
    	Location loc = new Location(block.getWorld(), 0, block.getLocation().getY(), 0);
        Spectator spec = Spectator.getInstance();
        
        if (spec.isSpectating(player)) {
        	return;
        }
        
        if (action != Action.RIGHT_CLICK_BLOCK) {
        	return;
        }
        
        if (block.getType() != Material.ENDER_PORTAL_FRAME) {
        	return;
        }
        
        if (block.getLocation().distance(loc) > 15) {
        	return;
        }
        
        if (BlockUtils.getDurability(block) > 3) {
        	return;
        }
        
        if (item.getType() != Material.EYE_OF_ENDER) {
        	return;
        }
        
        placed++;
    	
    	if (placed == 3) {
        	PlayerUtils.broadcast(Main.PREFIX + "§d§l§oThe portal has been activated.");
        	
        	for (Player online : PlayerUtils.getPlayers()) {
				online.playSound(online.getLocation(), Sound.PORTAL_TRAVEL, 1, 1);
			}
        	
        	for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
				for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
					block.getWorld().getBlockAt(x, loc.getBlockY(), z).setType(Material.ENDER_PORTAL);
				}
        	}
    	} else if (placed < 3) {
        	PlayerUtils.broadcast(Main.PREFIX + "An eye has been placed (§a" + placed + "§7/§a3§7)");
        	
        	for (Player online : PlayerUtils.getPlayers()) {
				online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
			}
    	}
    }
	
	@EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof EnderDragon)) {
        	return;
        }
        
        Player killer = entity.getKiller();
    	
    	if (killer == null) {
    		return;
    	}
    	
    	PlayerUtils.broadcast(Main.PREFIX + "The dragon was defeated by §a" + killer.getName() + "§7.");
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.getWorld() == killer.getWorld()) {
				continue;
			}
			
			online.playSound(online.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 1);
		}
    }
	
	@EventHandler
    public void on(MeetupEvent event) {
        PlayerUtils.broadcast(Main.PREFIX + "The dragon won, the time ran out.");
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.sendTitle(online, "§cTIMES UP!", "§7The time ran out and the dragon won", 5, 30, 5);
		}
    }
}