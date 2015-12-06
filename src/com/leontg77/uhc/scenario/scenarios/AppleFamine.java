package com.leontg77.uhc.scenario.scenarios;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.scenario.Scenario;

/**
 * AppleFamine scenario class.
 * 
 * @author LeonTG77
 */
public class AppleFamine extends Scenario {

	public AppleFamine() {
		super("AppleFamine", "Apples do not drop, meaning you can only heal with potions and golden heads.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {
		Game.getInstance().setAppleRates(0);
	}
}