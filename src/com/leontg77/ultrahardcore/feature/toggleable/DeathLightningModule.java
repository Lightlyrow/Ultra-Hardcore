package com.leontg77.ultrahardcore.feature.toggleable;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

public class DeathLightningModule extends Feature implements Listener {

	protected DeathLightningModule(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	public void on(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		player.getWorld().strikeLightningEffect(player.getLocation());
	    
	    for (Player online : PlayerUtils.getPlayers()) {
	    	if (player.getWorld().getPlayers().contains(online)) {
	    		continue;
	    	}
	    	
	    	online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 1000000000, 1);
	    }
	}
}