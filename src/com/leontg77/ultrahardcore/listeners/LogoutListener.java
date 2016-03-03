package com.leontg77.ultrahardcore.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.msg.MsgCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Logout listener class.
 * <p> 
 * Contains all eventhandlers for logout releated events.
 * 
 * @author LeonTG77
 */
public class LogoutListener implements Listener {
	private final Main plugin;
	
	private final InvGUI inv;
	private final Game game;

	private final SpecManager spec;
	private final PermissionsManager perm;
	
	/**
	 * Logout listener class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 * @param inv The inv gui class.
	 * @param spec The spectator manager class.
	 * @param perm The permission manager class.
	 */
	public LogoutListener(Main plugin, Game game, InvGUI inv, SpecManager spec, PermissionsManager perm) {
		this.plugin = plugin;
		
		this.game = game;
		this.inv = inv;
		
		this.spec = spec;
		this.perm = perm;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		User user = User.get(player);
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		
		user.getFile().set("lastlogout", date.getTime());
		user.saveFile();
		
		perm.removePermissions(player);
		event.setQuitMessage(null);
		
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}
		
		if (!spec.isSpectating(player)) {
			PlayerUtils.broadcast("§8[§c-§8] " + user.getRankColor() + player.getName() + " §7left. §8(§a" + (plugin.getOnlineCount() - 1) + "§8/§a" + game.getMaxPlayers() + "§8)");
		}

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
		
		if (MsgCommand.msg.containsKey(player)) {
			MsgCommand.msg.remove(player);
		}
		
		Set<String> temp = new HashSet<String>(MsgCommand.msg.keySet());
		
		for (String key : temp) {
			if (MsgCommand.msg.get(key).equals(player.getName())) {
				MsgCommand.msg.remove(key);
			}
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getReason().equals("disconnect.spam")) {
			event.setReason("Kicked for spamming");
		}
		
		if (event.getReason().startsWith("§8»")) {
			return;
		}
		
		event.setReason("§8» §7" + event.getReason() + " §8«");
	}
}