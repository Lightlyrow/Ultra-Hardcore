package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Voidscape scenario class.
 * 
 * @author LeonTG77
 */
public class Voidscape extends Scenario implements CommandExecutor, Listener {
	public static final String PREFIX = "�9Voidscape �8� �7";
	
	private final Main plugin;
	
	/**
	 * Voidscape scenario class constructor.
	 * 
	 * @param plugin The main class.
	 */
	public Voidscape(Main plugin) {
		super("Voidscape", "All stone and bedrock is replaced with air, to get cobble you need to make water flow to lava and make a cobblestone generator.");
		
		this.plugin = plugin;
		
		plugin.getCommand("voidscape").setExecutor(this);
	}
	
	private final List<Location> locs = new ArrayList<Location>();
	
	private BukkitRunnable task = null;
	private double totalChunks = 0;
	
	@Override
	public void onDisable() {
		if (task != null) {
			PlayerUtils.broadcast(PREFIX + "Cancelling voidscape generation.");
			task.cancel();
		}
		
		totalChunks = 0;
		task = null;
	}

	@EventHandler
    public void on(BlockFromToEvent event) {
		if (task == null) {
			return;
		}
		
        event.setCancelled(true);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "Voidscape is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(PREFIX + "Usage: /voidscape <world> <radius>");
			return true;
		}
		
		final World world = Bukkit.getWorld(args[0]);
		
		if (world == null) {
			sender.sendMessage(ChatColor.RED + "The world '" + args[0] + "' does not exist.");
			return true;
		}
		
		int radius;
		
		try {
			radius = parseInt(args[1]);
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
			return true;
		}
		
		locs.clear();
		
		for (int x = -1 * radius; x < radius; x += 16) {
			for (int z = -1 * radius; z < radius; z += 16) {
				locs.add(new Location(world, x, 1, z));
			}
		}
		
		totalChunks = locs.size();

		PlayerUtils.broadcast(PREFIX + "Starting voidscape generation in world '�a" + world.getName() + "�7'.");
		
		task = new BukkitRunnable() {
			public void run() {
				if (locs.size() == 0) {
					PlayerUtils.broadcast(PREFIX + "The voidscape generation has finished.");
					
					cancel();
					task = null;
					return;
				}

				Location loc = locs.remove(locs.size() - 1);
				Chunk chunk = world.getChunkAt(loc);
				
				for (int y = 0; y < 128; y++) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 17; z++) {
							Block block = chunk.getBlock(x, y, z);
							
							if (block.getType() == Material.STONE || block.getType() == Material.BEDROCK || block.getType() == Material.MONSTER_EGGS) {
								block.setType(Material.AIR);
							}
						}
					}
				}

				double completed = ((totalChunks - ((double) locs.size())) * 100 / totalChunks);
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					PacketUtils.sendAction(online, PREFIX + "Generating voidscape �8(�a" + NumberUtils.formatPercentDouble(completed) + "%�8)");
				}
			}
		};
		
		task.runTaskTimer(plugin, 1, 1);
		return true;
	}
}