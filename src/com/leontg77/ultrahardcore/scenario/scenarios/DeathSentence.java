package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * DeathSentence scenario class
 * 
 * @author LeonTG77
 */
public class DeathSentence extends Scenario implements CommandExecutor, Listener {
	private static final String PREFIX = "§7Death Sentence §8» §7";
	
	private final Main plugin;
	private final Game game;
	
	private final Map<UUID, Double> time = new HashMap<UUID, Double>();

	public DeathSentence(Main plugin, Game game) {
		super("DeathSentence", "Players are given 10 minutes of their lives. After their 10 Minutes run out, the player dies. However, if a player mines a specific ore or if they kill a player, they will gain a certain amount of time to their lives.");
	
		this.plugin = plugin;
		this.game = game;
		
		plugin.getCommand("dtime").setExecutor(this);
	}
	
	private BukkitRunnable task;

	@Override
	public void onDisable() {
		if (task != null) {
			task.cancel();
		}
		
		task = null;
	}

	@Override
	public void onEnable() {
		if (!State.isState(State.INGAME)) {
			return;
		}

		on(new GameStartEvent());
	}
	
	@EventHandler(ignoreCancelled = true)
    public void on(GameStartEvent event)  {
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : game.getPlayers()) {
					if (!time.containsKey(online.getUniqueId())) {
						time.put(online.getUniqueId(), 10.0);
					}
					
					if (time.get(online.getUniqueId()) <= 0.0) {
						online.sendMessage(PREFIX + "You had no more life minutes.");
						online.setHealth(0);
					} else {
						time.put(online.getUniqueId(), time.get(online.getUniqueId()) - 1);
					}
				}
			}
		};
		
		task.runTaskTimer(plugin, 1200L, 1200L);
    }

	@EventHandler
	public void on(BlockBreakEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (!time.containsKey(player.getUniqueId())) {
			time.put(player.getUniqueId(), 10.0);
		}
		
		switch (block.getType()) {
		case IRON_ORE:
			time.put(player.getUniqueId(), time.get(player.getUniqueId()) + 0.5);
			break;
		case GOLD_ORE:
			time.put(player.getUniqueId(), time.get(player.getUniqueId()) + 2);
			break;
		case DIAMOND_ORE:
			time.put(player.getUniqueId(), time.get(player.getUniqueId()) + 5);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}

		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (!time.containsKey(player.getUniqueId())) {
			time.put(player.getUniqueId(), 10.0);
		}
		
		switch (block.getType()) {
		case IRON_ORE:
		case GOLD_ORE:
		case DIAMOND_ORE:
			event.setCancelled(true);
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void on(PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Player player = event.getEntity();
		Player killer = player.getKiller();
		
		if (killer == null) {
			return;
		}

		if (!time.containsKey(killer.getUniqueId())) {
			time.put(killer.getUniqueId(), 10.0);
		}
		
		time.put(killer.getUniqueId(), time.get(killer.getUniqueId()) + 10);
		
		player.sendMessage(PREFIX + "Looks like your Death Sentence came unexpectedly...");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "Death Sentence is not enabled.");
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can perform /dtime.");
			return true;
		}
		
		Player player = (Player) sender;

		if (!time.containsKey(player.getUniqueId())) {
			sender.sendMessage(PREFIX + "No Death Sentence time has been set for you yet.");
			return true;
		}
		
		player.sendMessage(PREFIX + "You have §c" + time.get(player.getUniqueId()) + " minute(s)§7 remaining.");
		return true;
	}
}