package com.leontg77.ultrahardcore.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.gui.guis.ConfigGUI;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * The inventory managing class.
 * <p>
 * This class contains methods for opening the selector inventory, game info inventory, hall of fame inventory and player inventories.
 * 
 * @author LeonTG77
 */
public class InvGUI {
	private final Main plugin;
	
	private final TopStats topStats;
	private final GameInfoGUI gameInfo;
	private final HallOfFame hof;
	private final ConfigGUI config;
	private final Stats stats;
	
	public InvGUI(Main plugin) {
		this.plugin = plugin;
		
		topStats = new TopStats(plugin);
		gameInfo = new GameInfoGUI(plugin);
		hof = new HallOfFame(plugin);
		config = new ConfigGUI(plugin);
		stats = new Stats(plugin);
	}
	
	public HashMap<Player, HashMap<Integer, Inventory>> pagesForPlayer = new HashMap<Player, HashMap<Integer, Inventory>>();
	public HashMap<Player, Integer> currentPage = new HashMap<Player, Integer>();
	
	public static HashMap<Inventory, BukkitRunnable> invsee = new HashMap<Inventory, BukkitRunnable>();

	
	public GameInfoGUI getGameInfo() {
		return gameInfo;
	}
	
	protected TopStats getTopStats() {
		return topStats;
	}
	
	protected  Stats getStats() {
		return stats;
	}
	
	protected  HallOfFame getHOF() {
		return hof;
	}
	
	public void setup(Settings settings) {
		PluginManager manager = Bukkit.getPluginManager();
		
		manager.registerEvents(gameInfo, plugin);
		manager.registerEvents(topStats, plugin);
		manager.registerEvents(stats, plugin);
		manager.registerEvents(hof, plugin);
		manager.registerEvents(config, plugin);
		
		gameInfo.update();
		gameInfo.updateStaff();
		
		topStats.update();
		
		config.update();
		
		for (String host : settings.getHOF().getKeys(false)) {
			getHOF().update(host);
		}
		
		new BukkitRunnable() {
			public void run() {
				gameInfo.updateTimer();
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	
	/**
	 * Opens an inventory of all the online players that is playing.
	 * 
	 * @param player the player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openSelector(Game game, Player player) {
		List<Player> list = game.getPlayers();
		Inventory inv = null;
		
		int pages = ((list.size() / 28) + 1);
		
		pagesForPlayer.put(player, new HashMap<Integer, Inventory>());
		
		for (int current = 1; current <= pages; current++) {
			inv = Bukkit.createInventory(null, 54, "» §7Player Selector");
			
			for (int i = 0; i < 35; i++) {
				if (list.size() < 1) {
					continue;
				}
				
				if (isSide(i)) {
					continue;
				}
				
				Player target = list.remove(0);
				
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName("§a" + target.getName());
				meta.setLore(Arrays.asList("§7Click to teleport."));
				meta.setOwner(target.getName());
				item.setItemMeta(meta);
				inv.setItem(i, item);
			}
			
			ItemStack nextpage = new ItemStack (Material.ARROW);
			ItemMeta pagemeta = nextpage.getItemMeta();
			pagemeta.setDisplayName(ChatColor.GREEN + "Next page");
			pagemeta.setLore(Arrays.asList("§7Switch to the next page."));
			nextpage.setItemMeta(pagemeta);
			
			ItemStack prevpage = new ItemStack (Material.ARROW);
			ItemMeta prevmeta = prevpage.getItemMeta();
			prevmeta.setDisplayName(ChatColor.GREEN + "Previous page");
			prevmeta.setLore(Arrays.asList("§7Switch to the previous page."));
			prevpage.setItemMeta(prevmeta);
			
			if (current != 1) {
				inv.setItem(47, prevpage);
			}
			
			if (current != pages) {
				inv.setItem(51, nextpage);
			}
			
			pagesForPlayer.get(player).put(current, inv);
		}
		
		inv = pagesForPlayer.get(player).get(1);
		currentPage.put(player, 1);
		
		player.openInventory(inv);
		return inv;
	}

	/**
	 * Opens the inventory of the given target for the given player.
	 * 
	 * @param player player to open for.
	 * @param target the players inv to use.
	 * 
	 * @return The opened inventory.
	 */
	public Inventory openPlayerInventory(final Player player, final Player target) {
		final Inventory inv = Bukkit.getServer().createInventory(target, 54, "» §7" + target.getName() + "'s Inventory");
	
		invsee.put(inv, new BukkitRunnable() {
			public void run() {
				inv.setItem(0, target.getInventory().getHelmet());
				inv.setItem(1, target.getInventory().getChestplate());
				inv.setItem(2, target.getInventory().getLeggings());
				inv.setItem(3, target.getInventory().getBoots());
				inv.setItem(5, target.getItemInHand());
				inv.setItem(6, target.getItemOnCursor());
				
				ItemStack info = new ItemStack (Material.BOOK);
				ItemMeta infoMeta = info.getItemMeta();
				infoMeta.setDisplayName("§8» §6Player Info §8«");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§8» §7Name: §a" + target.getName());
				lore.add(" ");
				int health = (int) target.getHealth();
				lore.add("§8» §7Hearts: §6" + (((double) health) / 2) + "§4♥");
				lore.add("§8» §7Percent: §6" + NumberUtils.makePercent(target.getHealth()) + "%");
				lore.add("§8» §7Hunger: §6" + (((double) target.getFoodLevel()) / 2));
				lore.add("§8» §7XP level: §6" + target.getLevel());
				lore.add("§8» §7Location: §6x:" + target.getLocation().getBlockX() + ", y:" + target.getLocation().getBlockY() + ", z:" + target.getLocation().getBlockZ() + " (" + target.getWorld().getEnvironment().name().replaceAll("_", "").toLowerCase().replaceAll("normal", "overworld") + ")");
				lore.add(" ");
				lore.add("§8» §cPotion effects:");
				
				if (target.getActivePotionEffects().size() == 0) {
					lore.add("§8» §7None");
				}
				
				for (PotionEffect effects : target.getActivePotionEffects()) {
					lore.add("§8» §7P:§6" + NameUtils.getPotionName(effects.getType()) + " §7T:§6" + (effects.getAmplifier() + 1) + " §7D:§6" + DateUtils.ticksToString(effects.getDuration() / 20));
				}
				
				infoMeta.setLore(lore);
				info.setItemMeta(infoMeta);
				inv.setItem(8, info);
				lore.clear();
				
				for (int i = 9; i < 18; i++) {
					ItemStack glass = new ItemStack (Material.STAINED_GLASS_PANE, 1, (short) 15);
					ItemMeta glassMeta = glass.getItemMeta();
					glassMeta.setDisplayName("§0:>");
					glass.setItemMeta(glassMeta);
					inv.setItem(8, info);
					inv.setItem(i, glass);
				}
				
				int i = 18;
				
				for (ItemStack item : target.getInventory().getContents()) {
					inv.setItem(i, item);
					i++;
				}
				
				player.updateInventory();
			}
		});
		
		invsee.get(inv).runTaskTimer(plugin, 1, 1);
		player.openInventory(inv);
		
		return inv;
	}
	
	/**
	 * Opens an inventory of the given hosts hall of fame.
	 * 
	 * @param player the player opening for.
	 * @param host The owner of the hall of fame.
	 * @return The opened inventory.
	 */
	public Inventory openHOF(Player player, String host) {
		HallOfFame hof = getHOF();
		Inventory inv = hof.get(host);
		
		hof.currentHost.put(player.getName(), host);
		hof.currentPage.put(player.getName(), 1);
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Opens stats inventory of the given user.
	 * 
	 * @param target The owner of the stats.
	 * @return The opened inventory.
	 */
	public Inventory openStats(Player player, User target) {
		Inventory inv = stats.get(target);
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Opens top tats inventory.
	 * 
	 * @return The opened inventory.
	 */
	public Inventory openTopStats(Player player) {
		Inventory inv = topStats.get();
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Open the config option inventory for the given player.
	 * 
	 * @param player The player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openConfig(Player player) {
		Inventory inv = config.get();
		
		player.openInventory(inv);
		
		return inv;
	}
	
	public void openGameInfo(Player player) {
		openGameInfo(ImmutableList.of(player));
	}
	
	/**
	 * Opens the game information inventory.
	 * 
	 * @param players player to open for.
	 */
	public void openGameInfo(List<Player> players) {
		Inventory inv = gameInfo.get();
		
		for (Player player : players) {
			player.openInventory(inv);
		}
	}
}