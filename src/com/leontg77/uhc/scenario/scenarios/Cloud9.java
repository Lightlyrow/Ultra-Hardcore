package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Timers;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Cloud9 scenario class
 * 
 * @author LeonTG77
 */
public class Cloud9 extends Scenario {
	private BukkitRunnable task;
	
	public Cloud9() {
		super("Cloud9", "After 45 minutes, any player below y: 200 will begin to take half a heart of damage every 30 seconds, there are no starter items.");
	}

	@Override
	public void onDisable() {
		task.cancel();
	}

	@Override
	public void onEnable() {
		task = new BukkitRunnable() {
			public void run() {
				if (Timers.pvp > 0) {
					return;
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (!GameUtils.getGameWorlds().contains(online.getWorld())) {
						continue;
					}
					
					if (online.getLocation().getY() > 200) {
						continue;
					}
					
					online.damage(1);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, Timers.pvpSeconds * 20, 600);
	}
}