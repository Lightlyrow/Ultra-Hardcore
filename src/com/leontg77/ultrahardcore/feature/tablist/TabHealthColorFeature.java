package com.leontg77.ultrahardcore.feature.tablist;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Tab health color feature.
 * 
 * @author LeonTG77
 */
public class TabHealthColorFeature extends ToggleableFeature {
	private BukkitRunnable task;

	public TabHealthColorFeature() {
		super("Tab Health Color", "Your tab name is colored after how high you are on health.");
		
		icon.setType(Material.INK_SACK);
		icon.setDurability((short) 10);
		
		slot = 8;
	}
	
	@Override
	public void onDisable() {
		if (task == null) {
			return;
		}
		
		task.cancel();
		task = null;
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.setPlayerListName(null);
		}
	}
	
	@Override
	public void onEnable() {
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					final String percentString = NumberUtils.makePercent(online.getHealth());
					
					if (Spectator.getInstance().isSpectating(online)) {
						continue;
					}
					
					final String percentColor = percentString.substring(0, 2);
				    online.setPlayerListName(percentColor + online.getName());
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 1, 1);
	}
}