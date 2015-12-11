package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * PotentialHearts scenario class
 * 
 * @author LeonTG77
 */
public class PotentialHearts extends Scenario implements Listener {

	public PotentialHearts() {
		super("PotentialHearts", "Everyone starts off with 10 hearts and 10 unhealed potential hearts you need to heal by yourself.");
	}

	@Override
	public void onDisable() {
		for (Player online : PlayerUtils.getPlayers()) {
			online.setMaxHealth(20);
		}
	}

	@Override
	public void onEnable() {
		for (Player online : PlayerUtils.getPlayers()) {
			online.setMaxHealth(40);
		}
	}
	
	@EventHandler
	public void on(GameStartEvent event) {
		Game game = Game.getInstance();
		
		game.getWorld().setGameRuleValue("doDaylightCycle", "false");
		game.getWorld().setTime(6000);
	}
}