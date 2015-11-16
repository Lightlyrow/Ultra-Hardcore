package com.leontg77.uhc.scenario.types;

import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * GoToHell scenario class
 * 
 * @author LeonTG77
 */
public class GoToHell extends Scenario {
	private BukkitRunnable task;
	
	public GoToHell() {
		super("GoToHell", "After 45 minutes you have to be in the nether or else you take 0.5 hearts of damage every 30 seconds");
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
					Environment env = online.getWorld().getEnvironment();
					
					if (env == Environment.NETHER) {
						continue;
					}
					
					online.damage(1.0);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 600, 600);
		
		Game game = Game.getInstance();
		game.setNether(true);
	}
}