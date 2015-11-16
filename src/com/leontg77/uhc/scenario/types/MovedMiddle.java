package com.leontg77.uhc.scenario.types;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.leontg77.uhc.scenario.Scenario;

/**
 * No fall scenario class
 * 
 * @author LeonTG77
 */
public class MovedMiddle extends Scenario implements Listener {
	private boolean enabled = false;
	
	public MovedMiddle() {
		super("Moved0,0", "There is no 0,0 on the map, meetup coords are announced after 80 minutes.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (event.getCause() != DamageCause.FALL) {
			return;
		}
		
    	event.setCancelled(true);
    }
}