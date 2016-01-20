package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Fallout scenario class
 * 
 * @author LeonTG77
 */
public class Fallout extends Scenario {
	private BukkitRunnable task;
	
	public Fallout() {
		super("Fallout", "After a certain amount of time, any player above y: 60 will begin to take half a heart of damage every 30 seconds.");
	}

	@Override
	public void onDisable() {
		task.cancel();
		task = null;
	}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void on(GameStartEvent event) {
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					if (!GameUtils.getGameWorlds().contains(online.getWorld())) {
						continue;
					}
					
					if (online.getLocation().getBlockY() <= 60) {
						continue;
					}

					PlayerUtils.damage(online, 1);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 600, 600);
	}
}