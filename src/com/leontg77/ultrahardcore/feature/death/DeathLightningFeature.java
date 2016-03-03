package com.leontg77.ultrahardcore.feature.death;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * DeathLightninig feature class.
 * 
 * @author LeonTG77
 */
public class DeathLightningFeature extends ToggleableFeature implements Listener {
	private final Arena arena;

	public DeathLightningFeature(Arena arena) {
		super("Death Lightning", "A fake lightning that will strike when a player dies.");
		
		icon.setType(Material.BLAZE_ROD);
		slot = 49;
		
		this.arena = arena;
	}

	@EventHandler
	public void on(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}

		final Player player = event.getEntity();
		final User user = User.get(player);
		
		final List<World> worlds = game.getWorlds();
		
		// the arena has it's own way of doing deaths.
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 
	    
		// I know this isnt a death lightning thing, but they're dead, no more wl for them.
		player.setWhitelisted(false);
		user.setDeathLocation(player.getLocation());
	    
	    // I don't care about the rest if it hasn't started or they're not in a game world.
	    if (!State.isState(State.INGAME) || !worlds.contains(player.getWorld())) {
	    	return;
	    }
		
		player.getWorld().strikeLightningEffect(player.getLocation());
	    
	    for (Player online : Bukkit.getOnlinePlayers()) {
	    	if (player.getWorld().equals(online.getWorld())) {
	    		continue;
	    	}
	    	
	    	online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 1000000000, 1);
	    }
	}
}