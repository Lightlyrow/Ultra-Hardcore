package com.leontg77.ultrahardcore.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.utils.GameUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class PlayerListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMotd("§4§lArctic UHC §8» §6" + GameUtils.getMOTDMessage() + " §8« [§71.8§8] [§7EU§8]\n§8» §7§oFollow us on twitter, §a§o@ArcticUHC§7§o!");
		event.setMaxPlayers(game.getMaxPlayers());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
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
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        Spectator spec = Spectator.getInstance();
        InvGUI inv = InvGUI.getInstance();
        
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			inv.openSelector(player);
			return;
		} 
		
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			List<Player> list = GameUtils.getGamePlayers();
			
			if (list.isEmpty()) {
				player.sendMessage(Main.PREFIX + "Couldn't find any players.");
				return;
			}
			
			Random rand = new Random();
			Player target = list.get(rand.nextInt(list.size()));
			
			player.sendMessage(Main.PREFIX + "Teleported to §a" + target.getName() + "§7.");
			player.teleport(target.getLocation());
		}
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity clicked = event.getRightClicked();
		Player player = event.getPlayer();
		
		if (!(clicked instanceof Player)) {
			return;
		}
	    	
		Player interacted = (Player) clicked;
				
		Spectator spec = Spectator.getInstance();
		InvGUI inv = InvGUI.getInstance();
		
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (spec.isSpectating(interacted)) {
			return;
		}
		
		inv.openPlayerInventory(player, interacted);
    }
	
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		Spectator spec = Spectator.getInstance();
		Player player = event.getPlayer();
		
		if (!spec.isSpectating(player) && State.isState(State.INGAME)) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		World world = player.getWorld();
		
		if (world.getName().equals("lobby")) {
			event.setCancelled(true);
			event.setFoodLevel(20);
			return;
		}
	}
	
	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		
		if (player.getAllowFlight() && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}
}