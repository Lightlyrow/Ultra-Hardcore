package com.leontg77.ultrahardcore.feature.pvp;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshots;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshotsPlus;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Longshot feature class.
 * 
 * @author LeonTG77
 */
public class LongshotFeature extends Feature implements Listener {

	public LongshotFeature() {
		super("Longshot", "Displays in the chat if someone gets a long shot of 50+ blocks.");
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent event) {
		final Entity attacked = event.getEntity();
		final Entity attacker = event.getDamager();
		
		final ScenarioManager scen = ScenarioManager.getInstance();
		
    	if (game.isRecordedRound() || scen.getScenario(RewardingLongshots.class).isEnabled() || scen.getScenario(RewardingLongshotsPlus.class).isEnabled()) {
			return;
		}
    	
    	if (!(attacked instanceof Player) || !(attacker instanceof Arrow)) {
			return;
		}
		
		Player player = (Player) attacked;
		Arrow arrow = (Arrow) attacker;
		
		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}
		
		Player killer = (Player) arrow.getShooter();
		double distance = killer.getLocation().distance(player.getLocation());
		
		if (distance < 50) {
			return;
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "§6" + killer.getName() + " §7got a longshot of §a" + NumberUtils.formatDouble(distance) + " §7blocks on §6" + player.getName() + "§7.");
	}
}