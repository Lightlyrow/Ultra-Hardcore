package com.leontg77.ultrahardcore.feature.food;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * SaturationFix feature class.
 * 
 * @author LeonTG77
 */
public class SaturationFixFeature extends Feature implements Listener {

	public SaturationFixFeature() {
		super("SaturationFix", "Saturation fix makes you lose less hunger over time.");
	}

	@EventHandler
	public void on(final PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final float before = player.getSaturation();

		new BukkitRunnable() {
			public void run() {
				final float change = player.getSaturation() - before;
				
				player.setSaturation((float) (before + change * 2.5D));
			}
        }.runTaskLater(Main.plugin, 1);
	}
	
	@EventHandler
	public void on(final FoodLevelChangeEvent event) {
		final Player player = (Player) event.getEntity();
		final Random rand = new Random();
		
		if (event.getFoodLevel() < player.getFoodLevel()) {
			event.setCancelled(rand.nextInt(100) < 66);
	    }
	}
}