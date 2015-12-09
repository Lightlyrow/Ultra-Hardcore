package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Entropy scenario class
 * 
 * @author LeonTG77
 */
public class Entropy extends Scenario {
	private BukkitRunnable task;
	
	public Entropy() {
		super("Entropy", "Every 10 min 1XP level is \"drained\" from you. If you don't have any levels when the drain occurs, you die.");
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
					if (Spectator.getInstance().isSpectating(online)) {
						continue;
					}
					
					if (online.getLevel() == 0) {
						PlayerUtils.broadcast("§c" + online.getName() + " ran out of energy!");
						
						// damage them so they hear the damage sound then kill them.
						online.damage(0);
						online.setHealth(0);
						continue;
					}
					
					if (online.getLevel() > 0) {
						online.sendMessage("§6You survived the drain! The next drain is in 10 minutes!");
						online.setLevel(online.getLevel() - 1);
					}
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 12000, 12000);
	}
}