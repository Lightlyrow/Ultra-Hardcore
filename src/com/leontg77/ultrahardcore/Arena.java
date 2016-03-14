package com.leontg77.ultrahardcore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.listeners.ArenaListener;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.WorldManager;

/**
 * PvP Arena class.
 * <p>
 * This class contains methods for enabling/disabling the arena, removing and adding players, giving the kit, scores and getting the players in the arena.
 * 
 * @author LeonTG77
 */
public class Arena {
	public static final String PREFIX = "§4Arena §8» §7";
	
	private final Main plugin;
	
	private final BoardManager board;
	private final Game game;
	
	private final ScatterManager scatter;
	private final WorldManager manager;

	private final ArenaListener listener;
	private final Set<UUID> players;
	
	public Scoreboard sb;
	public Objective killboard;
	
	public Arena(Main plugin, Game game, BoardManager board, ScatterManager scatter, WorldManager manager) {
		this.plugin = plugin;
		
		this.board = board;
		this.game = game;
		
		sb = board.getBoard(); 
		killboard = sb.getObjective("arenaKills");
		
		this.scatter = scatter;
		this.manager = manager;

		listener = new ArenaListener(this);
		players = new HashSet<UUID>();
	}
	
	private BukkitRunnable regen;
	
	private boolean enabled = false;
	
	public boolean isResetting = false;
	public boolean wasEnabled = false;

	/**
	 * List of all verified arena world seeds.
	 */
	private static final List<Long> SEEDS = ImmutableList.of(
			-4978851967201209985l, 
			-4967553104279076810l, 
			-8429542510557232451l, 
			-3176074841184868038l, 
			-397143620226990283l, 
			-6555102318239067639l, 
			6776651824076158879l, 
			4542866204785804909l
	);

	/**
	 * Setup the arena class.
	 */
	public void setup() {
		if (sb.getObjective("arenaKills") == null) {
			killboard = sb.registerNewObjective("arenaKills", "dummy");
		}
		
		killboard.setDisplayName("§4Arena §8» §7§oUse /a to join§r");
		
		if (game.arenaBoard()) {
			killboard.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		plugin.getLogger().info("The arena has been setup.");
	}
	
	/**
	 * Enable the arena
	 */
	public void enable() {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		this.enabled = true;
		
		if (game.pregameBoard()) {
			board.setScore("§a ", 11);
			board.setScore("§8» §cArena:", 10);
			board.setScore("§8» §7/a ", 9);
		}
		
		if (game.arenaBoard()) {
			killboard.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			game.setPregameBoard(false);
			game.setArenaBoard(true);
		}
		
		regen = new BukkitRunnable() {
			int time = 30;
			
			public void run() {
				time--;
				
				switch (time) {
				case 15:
				case 10:
				case 5:
					PlayerUtils.broadcast(PREFIX + "The arena will reset in §a" + time + " §7minutes.");
					break;
				case 1:
					PlayerUtils.broadcast(PREFIX + "The arena will reset in §a1 §7minute.");
					break;
				case 0:
					time = 30;
					reset();
					break;
				}
				
				if (time == 15 || time == 10 || time == 5 || time == 1) {
					PlayerUtils.broadcast(PREFIX + "The arena will reset in §a" + time + " §7minute" + (time == 1 ? "" : "s") + ".");
					return;
				}
			}
		};
		
		regen.runTaskTimer(plugin, 1200, 1200);
	}
	
	/**
	 * Disable the arena
	 */
	public void disable() {
		HandlerList.unregisterAll(listener);
		this.enabled = false;
		
		for (Player player : getPlayers()) {
			User user = User.get(player);
			user.reset();
			
			if (player.isDead()) {
				player.spigot().respawn();
			}

			player.teleport(plugin.getSpawn());
			sb.resetScores(player.getName());
		}
		
		if (game.pregameBoard()) {
			board.resetScore("§a ");
			board.resetScore("§8» §cArena:");
			board.resetScore("§8» §7/a ");
		}
		
		if (game.arenaBoard()) {
			for (String entry : sb.getEntries()) {
				resetScore(entry);
			}
			
			PlayerUtils.broadcast(PREFIX + "The arena board has been disabled.");
			board.getKillsObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
			
			if (!isResetting) {
				game.setArenaBoard(false);
			}
		}
		
		players.clear();

		regen.cancel();
		regen = null;
	}

	/**
	 * Reset the arena.
	 */
	public void reset() {
		isResetting = true;
		wasEnabled = isEnabled();
		
		if (wasEnabled) {
			disable();
		}
		
		PlayerUtils.broadcast(PREFIX + "The arena is resetting, lag incoming.");
		
		World world = Bukkit.getServer().getWorld("arena");
		
		manager.deleteWorld(world);
		manager.createWorld("arena", 200, SEEDS.get(new Random().nextInt(SEEDS.size())), Environment.NORMAL, WorldType.NORMAL, false, false, false);
		
		PlayerUtils.broadcast(PREFIX + "World reset done, setting up world options...");

		world.setGameRuleValue("doDaylightCycle", "false");
		world.setTime(6000);
		
		PlayerUtils.broadcast(PREFIX + "Options setup, pregenning arena world.");
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " set 200 0 0");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " fill 420");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill confirm");
	}

	/**
	 * Check if the arena is enabled.
	 * 
	 * @return True if the arena is enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Get a list of players in the arena.
	 * 
	 * @return A list of players in the arena.
	 */
	public List<Player> getPlayers() {
		List<Player> list = new ArrayList<Player>();
		
		for (UUID uuid : players) {
			Player player = Bukkit.getPlayer(uuid);
			
			if (player != null) {
				list.add(player);
			}
		}
		
		return list;
	}
	
	/**
	 * Adds the given player to the arena.
	 * 
	 * @param player the player.
	 */
	public void addPlayer(Player player) {
		Location loc;
		
		try {
			loc = scatter.findScatterLocations(Bukkit.getWorld("arena"), (int) Bukkit.getWorld("arena").getWorldBorder().getSize() / 3, 1).get(0);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Could not teleport you to the arena.");
			return;
		}

		player.sendMessage(PREFIX + "You joined the arena.");
		
		players.add(player.getUniqueId());
		giveKit(player);
		
		loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);

		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 7));
		player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 7));
		
		player.setGameMode(GameMode.SURVIVAL);
		player.teleport(loc);
	}
	
	/**
	 * Removes the given player from the arena.
	 * 
	 * @param player The player.
	 * @param death If the removal was caused by dying
	 */
	public void removePlayer(Player player, boolean death) {
		players.remove(player.getUniqueId());
		
		if (death) {
			return;
		}

		player.sendMessage(PREFIX + "You left the arena.");

		if (getScore(player.getName()) > 4) {
			PlayerUtils.broadcast(PREFIX + "§6" + player.getName() + "§7's killstreak of §a" + getScore(player.getName()) + " §7was shut down from leaving!");
		}
		
		User user = User.get(player);
		user.reset();

		player.teleport(plugin.getSpawn());
		sb.resetScores(player.getName());
		
		if (player.isDead()) {
			player.spigot().respawn();
		}
	}
	
	/**
	 * Check if the arena contains a player.
	 * 
	 * @param player the player.
	 * @return <code>True</code> if the player is in the arena, <code>false</code> otherwise.
	 */
	public boolean hasPlayer(Player player) {
		return players.contains(player.getUniqueId());
	}
	
	/**
	 * Gives the arena kit to the given player.
	 * 
	 * @param player the player.
	 */
	private void giveKit(Player player) {
		User user = User.get(player);
		
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemStack bow = new ItemStack(Material.BOW);
		
		ItemStack cobble = new ItemStack(Material.COBBLESTONE, 64);
		ItemStack bucket = new ItemStack(Material.WATER_BUCKET);
		
		ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
		pickaxe.addEnchantment(Enchantment.DIG_SPEED, 2);
		
		ItemStack axe = new ItemStack(Material.IRON_AXE);
		axe.addEnchantment(Enchantment.DIG_SPEED, 2);
		
		ItemStack shovel = new ItemStack(Material.IRON_SPADE);
		shovel.addEnchantment(Enchantment.DIG_SPEED, 2);
		
		ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE);
		ItemStack food = new ItemStack(Material.COOKED_BEEF, 32);
		
		ItemStack helmet = new ItemStack(Material.IRON_HELMET);
		ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
		ItemStack boots = new ItemStack(Material.IRON_BOOTS);
		
		player.getInventory().setItem(user.getFile().getInt("hotar.sword", 0), sword);
		player.getInventory().setItem(user.getFile().getInt("hotar.bow", 1), bow);
		player.getInventory().setItem(user.getFile().getInt("hotar.bucket", 2), bucket);
		player.getInventory().setItem(user.getFile().getInt("hotar.pickaxe", 3), pickaxe);
		player.getInventory().setItem(user.getFile().getInt("hotar.cobble", 4), cobble);
		player.getInventory().setItem(user.getFile().getInt("hotar.gapple", 5), gapple);
		player.getInventory().setItem(user.getFile().getInt("hotar.shovel", 6), shovel);
		player.getInventory().setItem(user.getFile().getInt("hotar.axe", 7), axe);
		player.getInventory().setItem(user.getFile().getInt("hotar.food", 8), food);
		
		player.getInventory().setItem(27, new ItemStack(Material.ARROW, 64));
		player.getInventory().addItem(new ItemStack(Material.WORKBENCH, 16));
		player.getInventory().addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 4));
		
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
	}
	
	/**
	 * Sets the score of the given player.
	 * 
	 * @param player the player setting it for.
	 * @param newScore the new score.
	 */
	public void setScore(String player, int newScore) {
		Score score = killboard.getScore(player);
		
		score.setScore(newScore);
	}

	/**
	 * Gets the given score for the given string.
	 * 
	 * @param string the wanted string.
	 * @return The score of the string.
	 */
	public int getScore(String string) {
		return killboard.getScore(string).getScore();
	}

	/**
	 * Reset the score of the given string.
	 * 
	 * @param string the string resetting.
	 */
	public void resetScore(String string) {
		sb.resetScores(string);
	}
}