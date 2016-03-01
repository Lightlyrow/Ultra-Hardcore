package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Cloud9 scenario class
 * 
 * @author LeonTG77
 */
public class TommySX extends Scenario implements Listener {
	private BukkitRunnable task;
	
	public TommySX() {
		super("Cloud9", "After 45 minutes, any player below y: 200 will begin to take half a heart of damage every 30 seconds, there are no starter items.");
	}

	@Override
	public void onDisable() {
		task.cancel();
	}

	@Override
	public void onEnable() {}
	
//	@EventHandler
//	public void on(PvPEnableEvent event) {
//		task = new BukkitRunnable() {
//			public void run() {
//				for (Player online : game.getPlayers()) {
//					if (online.getLocation().getY() > 200) {
//						continue;
//					}
//
//					PlayerUtils.damage(online, 1);
//				}
//			}
//		};
//		
//		task.runTaskTimer(Main.plugin, 600, 600);
//	}
}