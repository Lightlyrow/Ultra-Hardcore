package com.leontg77.uhc.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.User;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PermsUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Logout listener class.
 * <p> 
 * Contains all eventhandlers for logout releated events.
 * 
 * @author LeonTG77
 */
public class LogoutListener implements Listener {
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		User user = User.get(player);
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		
		user.getFile().set("lastlogout", date.getTime());
		user.saveFile();
		
		Spectator spec = Spectator.getInstance();
		PacketUtils.removeTabList(player);
		
		PermsUtils.removePermissions(player);
		event.setQuitMessage(null);
		
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}
		
		if (!spec.isSpectating(player)) {
			PlayerUtils.broadcast("§8[§c-§8] §7" + player.getName() + " has left.");
		}
		
		if (Main.msg.containsKey(player)) {
			Main.msg.remove(player);
		}
		
		HashSet<CommandSender> temp = new HashSet<CommandSender>();
		
		for (CommandSender key : Main.msg.keySet()) {
			temp.add(key);
		}
		
		for (CommandSender key : temp) {
			if (Main.msg.get(key).equals(player)) {
				Main.msg.remove(key);
			}
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (!event.getReason().equals("disconnect.spam")) {
			return;
		}
		
		event.setReason("§8» §7Kicked for spamming §8«");
	}
}