package com.leontg77.ultrahardcore.feature.tablist;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Hearts on tab feature.
 * 
 * @author LeonTG77
 */
public class HeartsOnTabFeature extends ToggleableFeature {

	public HeartsOnTabFeature() {
		super("Hearts on tab", "Makes your health on the tab list be hearts instead of percent.");
		
		icon.setType(Material.INK_SACK);
		icon.setDurability((short) 1);
		
		slot = 7;
	}
	
	@Override
	public void onDisable() {
		final BoardManager board = BoardManager.getInstance();
		board.getTabHealthObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		for (Player online : PlayerUtils.getPlayers()) {
			double health = online.getHealth();
			
			online.setHealth(online.getHealth() == 19.0 ? 18.0 : 19.0);
			online.setHealth(health);
		}
	}
	
	@Override
	public void onEnable() {
		final BoardManager board = BoardManager.getInstance();
		board.getTabHeartsObjective().setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		for (Player online : PlayerUtils.getPlayers()) {
			double health = online.getHealth();
			
			online.setHealth(online.getHealth() == 19.0 ? 18.0 : 19.0);
			online.setHealth(health);
		}
	}
}