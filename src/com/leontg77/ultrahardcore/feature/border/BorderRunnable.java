package com.leontg77.ultrahardcore.feature.border;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Runnable for the border shrinking.
 * 
 * @author LeonTG77
 */
public class BorderRunnable extends BukkitRunnable {
	private int next;
	
	protected BorderRunnable(int next) {
		this.next = next;
	}
	
	@Override
	public void run() {
		next--;

		int borderSize = 0;

		for (World world : GameUtils.getGameWorlds()) {
			borderSize = (int) world.getWorldBorder().getSize();
		}
		
		switch (next) {
		case 600:
		case 300:
		case 60:
		case 30:
		case 10:
		case 5:
		case 4:
		case 3:
		case 2:
		case 1:
			PlayerUtils.broadcast(Main.PREFIX + "The border starts shrinking in §a" + DateUtils.advancedTicksToString(next) + "§7.");
			break;
		case 0:
			int size = 300;
			
			switch (borderSize) {
			case 300:
				size = 200;
				break;
			case 200:
				size = 100;
				break;
			}
				
			PlayerUtils.broadcast(Main.PREFIX + "Border will now shrink to §6" + size + "x" + size + " §7over §a10 §7minutes.");
			
			for (Player online : PlayerUtils.getPlayers()) {
				online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
			}

			for (World world : GameUtils.getGameWorlds()) {
				world.getWorldBorder().setSize(size, 600);
			}
			break;
		case -600:
			for (Player online : PlayerUtils.getPlayers()) {
				online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Border has stopped shrinking.");
			
			if (borderSize == 100) {
				cancel();
				return;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Next shrink is in §a20§7 minutes.");
			
			next = 1200;
			break;
		}
	}
}