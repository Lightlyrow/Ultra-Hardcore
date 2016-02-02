package com.leontg77.ultrahardcore.feature.death;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DeathLightninig feature class.
 * 
 * @author LeonTG77
 */
public class DeathLightningFeature extends ToggleableFeature implements Listener {

	public DeathLightningFeature() {
		super("Death Lightning", "A fake lightning that will strike when a player dies.");
		
		icon.setType(Material.BLAZE_ROD);
		slot = 49;
	}

	@EventHandler
	public void on(PlayerDeathEvent event) {
		if (!isEnabled()) {
			return;
		}

		final Player player = event.getEntity();
		
		final List<World> worlds = GameUtils.getGameWorlds();
		final Arena arena = Arena.getInstance();
		
		// the arena has it's own way of doing deaths.
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 
	    
	    // I don't care about the rest if it hasn't started or they're not in a game world.
	    if (!State.isState(State.INGAME) || !worlds.contains(player.getWorld())) {
	    	return;
	    }
		
		player.getWorld().strikeLightningEffect(player.getLocation());
	    
	    for (Player online : PlayerUtils.getPlayers()) {
	    	if (player.getWorld().equals(online.getWorld())) {
	    		continue;
	    	}
	    	
	    	online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 1000000000, 1);
	    }
	}
}