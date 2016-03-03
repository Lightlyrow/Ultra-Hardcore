package com.leontg77.ultrahardcore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.listeners.ParkourListener;

/**
 * The parkour class.
 * 
 * @author LeonTG77
 */
public class Parkour {
	public static final String PREFIX = "§9Parkour §8» §7";

	private final Settings settings;
	private final Main plugin;
	
	/**
	 * Parkour class constructor.
	 * 
	 * @param plugin The main class.
	 * @param settings The settings class.
	 */
	protected Parkour(Main plugin, Settings settings) {
		this.settings = settings;
		this.plugin = plugin;
	}

	private Location spawnpoint;
	private Location checkpoint1;
	private Location checkpoint2;
	private Location checkpoint3;
	private Location endpoint;
	
	/**
	 * Set up the checkpoints and listeners for the parkour
	 */
	public void setup() {
		Bukkit.getPluginManager().registerEvents(new ParkourListener(plugin, this), plugin);
		
		spawnpoint = loadLocation("parkour.spawnpoint");
		endpoint = loadLocation("parkour.endpoint");
		
		checkpoint1 = loadLocation("parkour.checkpoint.1");
		checkpoint2 = loadLocation("parkour.checkpoint.2");
		checkpoint3 = loadLocation("parkour.checkpoint.3");
	}
	
	/**
	 * Load up the location at the given config path.
	 * 
	 * @param path The config path.
	 * @return The location if found, null otherwise.
	 */
	private Location loadLocation(final String path) {
		final FileConfiguration data = settings.getData();
		
		if (!data.contains(path)) {
			return null;
		}
		
		World world = Bukkit.getWorld(data.getString(path + ".world"));
		
		if (world == null) {
			world = Bukkit.getWorlds().get(0);
		}
		
		double x = data.getDouble(path + ".x", 0.5);
		double y = data.getDouble(path + ".y", 33.0);
		double z = data.getDouble(path + ".z", 0.5);
		float yaw = (float) data.getDouble(path + ".yaw", 0);
		float pitch = (float) data.getDouble(path + ".pitch", 0);
		
		Location loc = new Location(world, x, y, z, yaw, pitch);
		return loc;
	}
	
	private final Map<UUID, Integer> checkpoint = new HashMap<UUID, Integer>();
	private final Map<UUID, Date> startTime = new HashMap<UUID, Date>();
	
	/**
	 * Reset who is parkouring, their checkpoint and their start time.
	 */
	public void reset() {
		checkpoint.clear();
		startTime.clear();
	}
	
	/**
	 * Add the given player to the parkour.
	 * 
	 * @param player The player to add.
	 */
	public void addPlayer(final Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		
		setCheckpoint(player, 0);
		resetTime(player);
	}
	
	/**
	 * Remove the given player from the parkour 
	 * <p>
	 * Either when they log off or when they complete it.
	 * 
	 * @param player The player to remove.
	 */
	public void removePlayer(final Player player) {
		if (checkpoint.containsKey(player.getUniqueId())) {
			checkpoint.remove(player.getUniqueId());
		}
		
		if (startTime.containsKey(player.getUniqueId())) {
			startTime.remove(player.getUniqueId());
		}
	}
	
	/**
	 * Reset the start time for the given player.
	 * 
	 * @param player The player to reset for.
	 */
	public void resetTime(final Player player) {
		startTime.put(player.getUniqueId(), new Date());
	}
	
	/**
	 * Check if the given player is currently parkouring.
	 * 
	 * @param player The player checking.
	 * @return True if the player is, false otherwise.
	 */
	public boolean isParkouring(final Player player) {
		if (!player.getWorld().getName().equals("lobby")) {
			return false;
		}
		
		return checkpoint.containsKey(player.getUniqueId());
	}

	/**
	 * Set the checkpoint for the given player.
	 * 
	 * @param player The player to set for.
	 * @param checkpoint The new checkpoint.
	 */
	public void setCheckpoint(Player player, int checkpoint) {
		this.checkpoint.put(player.getUniqueId(), checkpoint);
	}
	
	/**
	 * Get the current checkpoint of the given player.
	 * 
	 * @param player The player getting for.
	 * @return The checkpoint if any, -1 if not parkouring.
	 */
	public int getCheckpoint(final Player player) {
		if (!isParkouring(player)) {
			return -1;
		}
		
		return checkpoint.get(player.getUniqueId()).intValue();
	}
	
	/**
	 * Get the start time when the given player started the parkour.
	 * 
	 * @param player The player.
	 * @return The date if the player is parkouring, null otherwise.
	 */
	public Date getStartTime(final Player player) {
		return startTime.get(player.getUniqueId());
	}
	
	/**
	 * Get the location of the given checkpoint number.
	 * 
	 * @param checkpoint The checkpoint number.
	 * @return The checkpoint if any, the spawn if invaild number.
	 */
	public Location getLocation(final int checkpoint) {
		switch (checkpoint) {
		case 0:
			return spawnpoint;
		case 1:
			return checkpoint1;
		case 2:
			return checkpoint2;
		case 3:
			return checkpoint3;
		case 4:
			return endpoint;
		default:
			return plugin.getSpawn();
		}
	}
}