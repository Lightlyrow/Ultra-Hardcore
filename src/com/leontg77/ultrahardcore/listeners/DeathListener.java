package com.leontg77.ultrahardcore.listeners;

import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class DeathListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void on(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Arena arena = Arena.getInstance();
		
		// the arena has it's own way of doing deaths.
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 

		final List<World> worlds = GameUtils.getGameWorlds();
		
	    String deathMessage = event.getDeathMessage();
	    event.setDeathMessage(null);
	    
	    // I don't care about the rest if it hasn't started or they're not in a game world.
	    if (!State.isState(State.INGAME) || !worlds.contains(player.getWorld())) {
	    	return;
	    }

		// they're dead, no more wl for them.
		player.setWhitelisted(false);

		final Player killer = player.getKiller();

		if (killer == null) {
			if (deathMessage == null) {
				return;
			}

			PlayerUtils.broadcast("§8» §f" + deathMessage);
			event.setDeathMessage(null);
			return;
		}
		
		if (deathMessage != null && !deathMessage.isEmpty()) {
			ItemStack item = killer.getItemInHand();
			
			if (deathMessage.contains("shot")) {
				deathMessage += " §8(§7" + NumberUtils.formatDouble(killer.getLocation().distance(player.getLocation())) + " blocks§8)";
			}
			
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && deathMessage.contains(killer.getName()) && (deathMessage.contains("slain") || deathMessage.contains("shot"))) {
				String name = item.getItemMeta().getDisplayName();
				
				ComponentBuilder builder = new ComponentBuilder("§8» §r" + deathMessage.replace("[" + name + "]", ""));
				StringBuilder colored = new StringBuilder();
				
				if (killer.getItemInHand().getEnchantments().isEmpty()) {
					for (String entry : name.split(" ")) {
						colored.append("§o" + entry).append(" ");
					}
					
					builder.append("§f[" + colored.toString().trim() + "§f]");
				} else {
					for (String entry : name.split(" ")) {
						colored.append("§b§o" + entry).append(" ");
					}
					
					builder.append("§b[" + colored.toString().trim() + "§b]");
				}
				
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent( NameUtils.convertToJson(item)) }));
				final BaseComponent[] result = builder.create();

				for (Player online : PlayerUtils.getPlayers()) {
					online.spigot().sendMessage(result);
				}
				
				Bukkit.getLogger().info("§8» §f" + deathMessage);
				
				event.setDeathMessage(null);
			} else {
				PlayerUtils.broadcast("§8» §f" + deathMessage);
				event.setDeathMessage(null);
			}
		}
		
		final Team pteam = TeamManager.getInstance().getTeam(player);
		final Team team = TeamManager.getInstance().getTeam(killer);
		
		if (pteam != null && pteam.equals(team)) {
			return;
		}
		
		if (Main.kills.containsKey(killer.getName())) {
			Main.kills.put(killer.getName(), Main.kills.get(killer.getName()) + 1);
		} else {
			Main.kills.put(killer.getName(), 1);
		}

		if (team == null) {
			return;
		}
		
		if (Main.teamKills.containsKey(team.getName())) {
			Main.teamKills.put(team.getName(), Main.teamKills.get(team.getName()) + 1);
		} else {
			Main.teamKills.put(team.getName(), 1);
		}
	}
	
	@EventHandler
	public void on(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		Arena arena = Arena.getInstance();
		
		event.setRespawnLocation(Main.getSpawn());
		player.setMaxHealth(20);
		
		if (arena.isEnabled() || !State.isState(State.INGAME) || game.isRecordedRound()) {
			return;
		}
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.hidePlayer(player);
		}
		
		player.sendMessage(Main.PREFIX + "Thanks for playing this game, it really means a lot :)");
		player.sendMessage(Main.PREFIX + "Follow us on twtter to know when our next games are: §a§o@ArcticUHC");
		player.sendMessage(Main.PREFIX + "Please do not spam, rage, spoil or be a bad sportsman.");
		
		if (!player.hasPermission("uhc.prelist")) {
			player.sendMessage(Main.PREFIX + "You may stay as long as you want (You are vanished).");
			return;
		}
		
		player.sendMessage(Main.PREFIX + "You will be put into spectator mode in 10 seconds.");
		
		new BukkitRunnable() {
			public void run() {
				Spectator spec = Spectator.getInstance();
				
				if (!State.isState(State.INGAME) || !player.isOnline() || spec.isSpectating(player)) {
					return;
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.showPlayer(player);
				}
				
				spec.enableSpecmode(player);
			}
		}.runTaskLater(Main.plugin, 200);
	}
}