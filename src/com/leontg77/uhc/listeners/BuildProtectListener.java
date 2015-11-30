package com.leontg77.uhc.listeners;

import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leontg77.uhc.State;
import com.leontg77.uhc.utils.GameUtils;

/**
 * Build protect listener class.
 * <p> 
 * All events in this class is for protecting certain worlds from being destroyed
 * 
 * @author LeonTG77
 */
public class BuildProtectListener implements Listener {
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
    	
		// if the current state is scatter mode, we want to cancel and return.
    	if (State.isState(State.SCATTER)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	List<World> worlds = GameUtils.getGameWorlds();
    	World world = block.getWorld();

        // if the world is a game world we don't want to do anything.
    	if (worlds.contains(world)) {
    		return;
    	}

    	// if the world is the arena we don't want to do anything
    	if (world.getName().equals("arena")) {
			return;
		}

        // if the player has the build permission we want to return.
		if (player.hasPermission("uhc.build")) {
			return;
		}

        // the player is in a world that isn't a game world, isn't the arena and he has build perms, cancel the event.
		event.setCancelled(true);
    }

	@EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player player = event.getPlayer();
    	
		// if the current state is scatter mode, we want to cancel and return.
    	if (State.isState(State.SCATTER)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	List<World> worlds = GameUtils.getGameWorlds();
    	World world = block.getWorld();

        // if the world is a game world we don't want to do anything.
    	if (worlds.contains(world)) {
    		return;
    	}

    	// if the world is the arena we don't want to do anything
    	if (world.getName().equals("arena")) {
			return;
		}

        // if the player has the build permission we want to return.
		if (player.hasPermission("uhc.build")) {
			return;
		}

        // the player is in a world that isn't a game world, isn't the arena and he has build perms, cancel the event.
		event.setCancelled(true);
    }
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        List<World> worlds = GameUtils.getGameWorlds();
        Action action = event.getAction();
        
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        // if the world is a game world we don't want to do anything.
    	if (worlds.contains(world)) {
    		return;
    	}
    	
    	// if the world is the arena we don't want to do anything
    	if (world.getName().equals("arena")) {
    		return;
    	}
    		
    	// if the event was physical (hopping on farms, etc..) we want to cancel and return.
        if (action == Action.PHYSICAL) {
        	event.setCancelled(true);
        	return;
    	}
        
        // if the player has the build permission we want to return.
        if (player.hasPermission("uhc.build")) {
			return;
		}
		
        // the player is in a world that isn't a game world, isn't the arena and he has build perms, cancel the event.
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
		ArmorStand stand = event.getRightClicked();
		
		List<World> worlds = GameUtils.getGameWorlds();
        World world = stand.getWorld();
        
        // if the world is a game world we don't want to do anything.
    	if (worlds.contains(world)) {
    		return;
    	}
    	
    	// if the world is the arena we don't want to do anything
    	if (world.getName().equals("arena")) {
    		return;
    	}
		
        // the player is in a world that isn't a game world, isn't the arena and he has build perms, cancel the event.
		event.setCancelled(true);
	}
}