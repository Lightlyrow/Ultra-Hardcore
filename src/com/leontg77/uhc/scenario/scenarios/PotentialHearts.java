package com.leontg77.uhc.scenario.scenarios;

import org.bukkit.entity.Player;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * PotentialHearts scenario class
 * 
 * @author LeonTG77
 */
public class PotentialHearts extends Scenario {

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
}