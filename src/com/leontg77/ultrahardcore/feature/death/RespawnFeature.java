package com.leontg77.ultrahardcore.feature.death;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Rspawn feature class.
 * 
 * @author LeonTG77
 */
public class RespawnFeature extends Feature implements Listener {

	public RespawnFeature() {
		super("Respawn", "Messages and auto spec mode when someone respawn!");
	}
	
	@EventHandler
	public void on(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final Arena arena = Arena.getInstance();
		
		event.setRespawnLocation(Main.getSpawn());
		player.setMaxHealth(20);
		
		if (arena.isEnabled() || !State.isState(State.INGAME) || game.isRecordedRound()) {
			return;
		}
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.hidePlayer(player);
		}
		
		player.sendMessage(Main.PREFIX + "Thanks for playing a game provided by Arctic UHC!");
		player.sendMessage(Main.PREFIX + "Follow us on twtter to know when our next games are: §a§o@ArcticUHC");
		player.sendMessage(Main.PREFIX + "Please do not spam, rage, spoil or be a bad sportsman.");
		
		if (!player.hasPermission("uhc.prelist")) {
			player.sendMessage(Main.PREFIX + "You may stay as long as you want (You are vanished).");
			return;
		}

		player.sendMessage(Main.PREFIX + "Found death spectator permission...");
		player.sendMessage(Main.PREFIX + "Enabling your spectator mode in 10 seconds.");
		
		new BukkitRunnable() {
			public void run() {
				final SpecManager spec = SpecManager.getInstance();
				
				if (!State.isState(State.INGAME) || !player.isOnline() || spec.isSpectating(player)) {
					return;
				}
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					online.showPlayer(player);
				}
				
				spec.enableSpecmode(player);
			}
		}.runTaskLater(Main.plugin, 200);
	}
}