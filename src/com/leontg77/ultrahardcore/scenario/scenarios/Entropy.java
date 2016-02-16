package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.uhc.GameStartEvent;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Entropy scenario class
 * 
 * @author LeonTG77
 */
public class Entropy extends Scenario implements Listener {
	private BukkitRunnable task;
	
	public Entropy() {
		super("Entropy", "Every 10 min 1XP level is \"drained\" from you. If you don't have any levels when the drain occurs, you die.");
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
				for (Player online : Bukkit.getOnlinePlayers()) {
					if (SpecManager.getInstance().isSpectating(online)) {
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