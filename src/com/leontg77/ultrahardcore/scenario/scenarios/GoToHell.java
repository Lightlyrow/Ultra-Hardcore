package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * GoToHell scenario class
 * 
 * @author LeonTG77
 */
public class GoToHell extends Scenario implements Listener {
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
		Game game = Game.getInstance();
		game.setNether(true);
	}
	
	@EventHandler
	public void on(PvPEnableEvent event) {
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
	}
}