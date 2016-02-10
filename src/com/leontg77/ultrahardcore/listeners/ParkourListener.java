package com.leontg77.ultrahardcore.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class ParkourListener implements Listener {
	
	@EventHandler
	public void on(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location to = event.getTo();
		
		if (to.getWorld().getName().equals("lobby") && to.getY() <= 20) {
			Parkour parkour = Parkour.getInstance();
			
			if (parkour.isParkouring(player)) {
				if (parkour.getCheckpoint(player) != null) {
					int checkpoint = parkour.getCheckpoint(player);
					player.teleport(parkour.getLocation(checkpoint));
					return;
				}
				
				player.teleport(parkour.getLocation(0));
				return;
			}
			
			player.teleport(Main.getSpawn());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (parkourers.contains(player)) {
			parkourers.remove(player);
		}
		
		if (checkpoint.containsKey(player)) {
			checkpoint.remove(player);
		}
		
		if (time.containsKey(player)) {
			time.remove(player);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
			return;
		}
		
		Player player = event.getPlayer();
		Entity point;
		
		try {
			point = PlayerUtils.getNearby(event.getTo(), 0.5).get(0);
		} catch (Exception e) {
			return;
		}
		
		if (point instanceof ArmorStand) {
			ArmorStand stand = (ArmorStand) point;
			
			if (stand.getCustomName() == null) {
				return;
			}
			
			if (stand.getCustomName().contains("Start")) {
				if (parkourers.contains(player)) {
					player.sendMessage(Main.PREFIX + "The timer has been reset to §a0s§7.");
					player.playSound(player.getLocation(), "random.pop", 1, 1);
					time.put(player, 0);
					return;
				}
				
				player.sendMessage(Main.PREFIX + "Parkour started.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourers.add(player);
				checkpoint.put(player, 0);
				time.put(player, 0);
			}
			
			if (!parkourers.contains(player)) {
				return;
			}
			
			if (stand.getCustomName().contains("#1")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 1) {
					return;
				}
				
				player.sendMessage(Main.PREFIX + "You reached checkpoint §c1§7.");
				((CraftWorld) player.getWorld()).getHandle().makeSound(((CraftPlayer) player).getHandle(), "random.pop", 1, 1);
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourers.add(player);
				checkpoint.put(player, 1);
			}
			
			if (stand.getCustomName().contains("#2")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 2) {
					return;
				}
				
				player.sendMessage(Main.PREFIX + "You reached checkpoint §c2§7.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourers.add(player);
				checkpoint.put(player, 2);
			}
			
			if (stand.getCustomName().contains("#3")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 3) {
					return;
				}
				
				player.sendMessage(Main.PREFIX + "You reached checkpoint §c3§7.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				parkourers.add(player);
				checkpoint.put(player, 3);
			}
			
			if (stand.getCustomName().contains("finish")) {
				player.sendMessage(Main.PREFIX + "You finished the parkour, time used: §a" + DateUtils.ticksToString(time.get(player)));
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
				
				parkourers.remove(player);
				checkpoint.remove(player);
				time.remove(player);
			}
		}
	}
}