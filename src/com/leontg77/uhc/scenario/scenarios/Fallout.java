package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

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
	}

	@Override
	public void onEnable() {
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					if (!GameUtils.getGameWorlds().contains(online.getWorld())) {
						continue;
					}
					
					if (online.getLocation().getBlockY() <= 60) {
						continue;
					}
					
					online.damage(1.0);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 600, 600);
	}
}