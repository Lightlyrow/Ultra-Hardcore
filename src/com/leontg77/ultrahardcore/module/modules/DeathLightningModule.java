package com.leontg77.ultrahardcore.module.modules;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.module.Module;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

public class DeathLightningModule extends Module implements Listener {

	@EventHandler
	public void on(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		player.getWorld().strikeLightningEffect(player.getLocation());
	    
	    for (Player online : PlayerUtils.getPlayers()) {
	    	if (player.getWorld().getPlayers().contains(online)) {
	    		continue;
	    	}
	    	
	    	online.playSound(online.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1);
	    }
	}
}