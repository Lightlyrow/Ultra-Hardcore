package com.leontg77.ultrahardcore.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.msg.MsgCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

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
		
		PermissionsManager.removePermissions(player);
		event.setQuitMessage(null);
		
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}
		
		if (!spec.isSpectating(player)) {
			PlayerUtils.broadcast("§8[§c-§8] §7" + player.getName() + " has left.");
		}
		
		InvGUI inv = InvGUI.getInstance();

		// clear ALL data from the player, incase of a memory leak.
		
		if (InvGUI.invsee.containsKey(inv)) {
			InvGUI.invsee.get(inv).cancel();
			InvGUI.invsee.remove(inv);
		}
		
		if (inv.pagesForPlayer.containsKey(player)) {
			inv.pagesForPlayer.remove(player);
		}
		
		if (inv.currentPage.containsKey(player)) {
			inv.currentPage.remove(player);
		}
		
		if (Main.rainbow.containsKey(player)) {
			Main.rainbow.remove(player);
		}
		
		if (MsgCommand.msg.containsKey(player)) {
			MsgCommand.msg.remove(player);
		}
		
		HashSet<CommandSender> temp = new HashSet<CommandSender>();
		
		for (CommandSender key : MsgCommand.msg.keySet()) {
			temp.add(key);
		}
		
		for (CommandSender key : temp) {
			if (MsgCommand.msg.get(key).equals(player)) {
				MsgCommand.msg.remove(key);
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