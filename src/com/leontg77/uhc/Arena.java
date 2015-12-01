package com.leontg77.uhc;

import static com.leontg77.uhc.Main.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.listeners.ArenaListener;
import com.leontg77.uhc.managers.BoardManager;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.utils.ScatterUtils;
import com.leontg77.uhc.worlds.WorldManager;

/**
 * PvP Arena class.
 * <p>
 * This class contains methods for enabling/disabling the arena, removing and adding players, giving the kit, scores and getting the players in the arena.
 * 
 * @see {@link BoardManager}
 * @author LeonTG77
 */
public class Arena {
	private static Arena instance = new Arena();
	private boolean enabled = false;
	
	public static final String PREFIX = "§4§lArena §8» §7";
	private BukkitRunnable regen;
	
	public boolean reset = false;
	public boolean wasEnabled = false;
	
	public Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
	public Objective arenaKills = board.getObjective("arenaKills");
	
	public HashMap<Player, Integer> killstreak = new HashMap<Player, Integer>();
	private List<Player> players = new ArrayList<Player>();
	
	// make a list of found seeds that are good for the arena.
	/* CHRISMAS SEED!!!! private List<Long> seeds = ImmutableList.of(-4978851967201209985l, -4967553104279076810l, -8429542510557232451l, -3176074841184868038l, -397143620226990283l, -6555102318239067639l, 6776651824076158879l);*/

	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Arena getInstance() {
		return instance;
	}
	
	/**
	 * Setup the arena class.
	 */
	public void setup() {
		if (board.getObjective("arenaKills") == null) {
			arenaKills = board.registerNewObjective("arenaKills", "dummy");
		}
		
		arenaKills.setDisplayName("§4Arena §8- §7Use /a to join");
		
		if (Game.getInstance().arenaBoard()) {
			arenaKills.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		plugin.getLogger().info("The arena has been setup.");
	}
	
	/**
	 * Enable the arena
	 */
	public void enable() {
		Game game = Game.getInstance();
		
		Bukkit.getPluginManager().registerEvents(new ArenaListener(), plugin);
		this.enabled = true;
		
		if (game.pregameBoard()) {
			BoardManager boards = BoardManager.getInstance();
			
			boards.setScore("§a ", 11);
			boards.setScore("§8» §cArena:", 10);
			boards.setScore("§8» §7/a ", 9);
		}
		
		if (game.arenaBoard()) {
			PlayerUtils.broadcast(PREFIX + "The arena board has been enabled.");
			arenaKills.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			game.setPregameBoard(false);
			game.setArenaBoard(true);

			setScore("§8» §a§lPvE", 1);
			setScore("§8» §a§lPvE", 0);
		}
		
		regen = new BukkitRunnable() {
			int time = 30;
			
			public void run() {
				time--;
				
				if (time == 15 || time == 10 || time == 5 || time == 1) {
					PlayerUtils.broadcast(PREFIX + "The arena will reset in §a" + time + " §7minute" + (time == 1 ? "" : "s") + ".");
					return;
				}
			
				if (time == 0) {
					time = 30;
					reset();
				}
			}
		};
		
		regen.runTaskTimer(Main.plugin, 1200, 1200);
	}
	
	/**
	 * Disable the arena
	 */
	public void disable() {
		Game game = Game.getInstance();
		
		HandlerList.unregisterAll(new ArenaListener());
		this.enabled = false;
		
		for (Player player : getPlayers()) {
			User user = User.get(player);
			user.reset();
			
			if (player.isDead()) {
				player.spigot().respawn();
			}
			
			board.resetScores(player.getName());
			player.teleport(Main.getSpawn());
			
			user.setStat(Stat.ARENACKS, 0);
		}
		
		if (game.pregameBoard()) {
			BoardManager boards = BoardManager.getInstance();
			
			boards.resetScore("§a ");
			boards.resetScore("§8» §cArena:");
			boards.resetScore("§8» §7/a ");
		}
		
		if (game.arenaBoard()) {
			for (String entry : board.getEntries()) {
				resetScore(entry);
			}
			
			PlayerUtils.broadcast(PREFIX + "The arena board has been disabled.");
			BoardManager.getInstance().kills.setDisplaySlot(DisplaySlot.SIDEBAR);
			game.setArenaBoard(false);
		}
		
		killstreak.clear();
		players.clear();

		regen.cancel();
		regen = null;
	}

	public void reset() {
		reset = true;
		wasEnabled = isEnabled();
		
		if (wasEnabled) {
			disable();
		}
		
		PlayerUtils.broadcast(PREFIX + "The arena is resetting, lag incoming.");
		WorldManager manager = WorldManager.getInstance();
		
		World world = Bukkit.getServer().getWorld("arena");
		
		manager.deleteWorld(world);
		manager.createWorld("arena", 200, /*seeds.get(new Random().nextInt(seeds.size()))*/ -4296693922760164262l, Environment.NORMAL, WorldType.NORMAL);
		
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
		return players;
	}
	
	/**
	 * Check if the arena contains a player.
	 * 
	 * @param player the player.
	 * @return <code>True</code> if the player is in the arena, <code>false</code> otherwise.
	 */
	public boolean hasPlayer(Player player) {
		return players.contains(player);
	}
	
	/**
	 * Adds the given player to the arena.
	 * 
	 * @param player the player.
	 */
	public void addPlayer(Player player) {
		Location loc;
		
		try {
			loc = ScatterUtils.getScatterLocations(Bukkit.getWorld("arena"), (int) Bukkit.getWorld("arena").getWorldBorder().getSize() / 3, 1).get(0);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + "Could not teleport you to the arena.");
			return;
		}

		player.sendMessage(PREFIX + "You joined the arena.");
		
		killstreak.put(player, 0);
		players.add(player);
		giveKit(player);
		
		loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 2);
		
		player.teleport(loc);
		player.setGameMode(GameMode.SURVIVAL);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 7));
	}
	
	/**
	 * Removes the given player from the arena.
	 * 
	 * @param player The player.
	 * @param death If the removal was caused by dying
	 */
	public void removePlayer(Player player, boolean death) {
		players.remove(player);
		
		if (death) {
			return;
		}

		player.sendMessage(PREFIX + "You left the arena.");

		if (killstreak.containsKey(player) && killstreak.get(player) > 4) {
			PlayerUtils.broadcast(PREFIX + "§6" + player.getName() + "§7's killstreak of §a" + killstreak.get(player) + " §7was shut down from leaving!");
		}

		killstreak.remove(player);
		
		User user = User.get(player);
		
		user.setStat(Stat.ARENACKS, 0);
		user.reset();
		
		board.resetScores(player.getName());
		player.teleport(Main.getSpawn());
		
		if (player.isDead()) {
			player.spigot().respawn();
		}
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
		
		player.getInventory().setItem(27, new ItemStack (Material.ARROW, 64));
		player.getInventory().addItem(new ItemStack (Material.WORKBENCH, 16));
		player.getInventory().addItem(new ItemStack (Material.ENCHANTMENT_TABLE, 4));
		
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
		Score score = arenaKills.getScore(player);
		
		score.setScore(newScore);
	}

	/**
	 * Gets the given score for the given string.
	 * 
	 * @param string the wanted string.
	 * @return The score of the string.
	 */
	public int getScore(String string) {
		return arenaKills.getScore(string).getScore();
	}

	/**
	 * Reset the score of the given string.
	 * 
	 * @param string the string resetting.
	 */
	public void resetScore(String string) {
		board.resetScores(string);
	}
}