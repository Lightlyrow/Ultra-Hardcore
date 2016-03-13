package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * MysteryTeams scenario class
 * 
 * @author EXSolo, modified by LeonTG77
 */
public class MysteryTeams extends Scenario implements Listener, CommandExecutor {
	private static final String PREFIX = "§6[§cMysteryTeams§6] §f";
	
	private final Game game;

	private static Map<MysteryTeam, List<UUID>> orgTeams;
	private static List<MysteryTeam> teams;

	public MysteryTeams(Main plugin, Game game) {
		super("MysteryTeams", "Teams are unknown until meeting and comparing banners.");
		
		plugin.getCommand("mt").setExecutor(this);
		
		this.game = game;
		
		orgTeams = new HashMap<MysteryTeam, List<UUID>>();
		teams = Arrays.asList(MysteryTeam.values());
	}

	@Override
	public void onDisable() {
		orgTeams.clear();
	}

	@Override
	public void onEnable() {
		// this does not disable the scenario.
		onDisable();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "MysteryTeams is not enabled.");
			return true;
		}
		
		if (args.length == 0) {
			return helpMenu(sender);
		}
		
		if (args[0].equalsIgnoreCase("teamsize")) {
			sender.sendMessage(PREFIX + "Existing teamsizes:");
			
			for (MysteryTeam team : teams) {
				sender.sendMessage(PREFIX + team.getChatColor() + team.getName() + "'s teamsize: §f" + team.getSize());
			}
		} 
		
		if (!sender.hasPermission("uhc.mysteryteams")) {
			return helpMenu(sender);
		}
		
		if (args[0].equalsIgnoreCase("randomize")) {
			if (args.length == 1) {
				sender.sendMessage(PREFIX + "Usage: /mt randomize <teamsize> <amount of teams>");
				return true;
			}
			
			int teamSize;
			
			try {
				teamSize = parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			
			int amount;
			
			try {
				amount = parseInt(args[2]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			
			for (int i = 0; i < amount; i++) {
				List<Player> players = new ArrayList<Player>();
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					if (getTeam(online) == null && game.getPlayers().contains(online)) {
						players.add(online);
					}
				}

				Collections.shuffle(players);
	    		MysteryTeam team = findAvailableTeam();
				
	    		if (team == null) {
					sender.sendMessage(ChatColor.RED + "There are no more available teams.");
					break;
	    		}
	    		
				for (int j = 0; j < teamSize; j++) {
					if (players.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "No more players to add to the team.");
						break;
					}
					
					Player player = players.remove(0);
					
		    		orgTeams.get(team).add(player.getUniqueId());
		    		team.addPlayer(player);
				}
			}
			
			sender.sendMessage(PREFIX + "Randomized " + amount + " teams of " + teamSize + ".");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			if (orgTeams.isEmpty()) {
				sender.sendMessage(PREFIX + "There are no teams set.");
				return true;
			}

			sender.sendMessage(PREFIX + "All teams: §o(Names in red means they are dead)");
			
			for (Entry<MysteryTeam, List<UUID>> team : orgTeams.entrySet()) { 
				StringBuilder members = new StringBuilder();
				int i = 1;
				
				for (UUID member : team.getValue()) {
					if (members.length() > 0) {
						if (i == team.getValue().size()) {
							members.append(" §8and §f");
						} else {
							members.append("§8, §f");
						}
					}
					
					OfflinePlayer offline = Bukkit.getOfflinePlayer(member);
					
					if (teams.contains(team.getKey())) {
						members.append(team.getKey().hasPlayer(offline) ? ChatColor.GREEN + offline.getName() : ChatColor.RED + offline.getName());
					} else {
						members.append(ChatColor.RED + offline.getName());
					}
					i++;
				}
				
				sender.sendMessage(team.getKey().getChatColor() + team.getKey().getName() + ": §f" + members.toString().trim());
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("give")) {
			if (args.length == 0) {
				sender.sendMessage(PREFIX + "Gave item to everyone.");
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					MysteryTeam team = getTeam(online);
					
					if (team == null) {
						continue;
					}
					
					ItemStack item = new ItemStack(Material.BANNER);
		    		BannerMeta meta = (BannerMeta) item.getItemMeta();
		    		meta.setBaseColor(team.getDyeColor());
		    		item.setItemMeta(meta);
		    		PlayerUtils.giveItem(online, item);
				}
			}

			Player target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not online.");
				return true;
			}
			
			MysteryTeam team = getTeam(target);
			
			if (team == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not on a team.");
				return true;
			}
			
			sender.sendMessage(PREFIX + "Gave item to player.");
			
			ItemStack item = new ItemStack(Material.BANNER);
    		BannerMeta meta = (BannerMeta) item.getItemMeta();
    		meta.setBaseColor(team.getDyeColor());
    		item.setItemMeta(meta);
    		PlayerUtils.giveItem(target, item);
    		return true;
		}
		
		return helpMenu(sender);
	}
	
	/**
	 * Register a new fake team.
	 * 
	 * @return The fake team created.
	 */
	public MysteryTeam findAvailableTeam() {
		for (MysteryTeam team : teams) {
			if (team.getSize() == 0) {
				return team;
			}
		}
		
		return null;
	}
	
	private MysteryTeam getTeam(Player player) {
		for (MysteryTeam team : teams) {
			if (team.hasPlayer(player)) {
				return team;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a Set of all the fake teams.
	 * 
	 * @return A set of fake teams.
	 */
	public Set<MysteryTeam> getTeams() {
		return ImmutableSet.copyOf(teams);
	}
	
	/**
	 * Get a Set of all the orgiginal fake teams.
	 * 
	 * @return A set of orgiginal fake teams.
	 */
	public Map<MysteryTeam, List<UUID>> getOrgTeams() {
		return orgTeams;
	}

	/**
	 * Print the help menu to the given sender.
	 * 
	 * @param sender The sender printing too
	 * @return 
	 * @return Always true;
	 */
	public boolean helpMenu(CommandSender sender) {
		sender.sendMessage(PREFIX + "MysteryTeams Command");
	    sender.sendMessage(" - " + ChatColor.BLUE + "/mt teamsize");
	    
	    if (!sender.hasPermission("mysteryteams.admin")) {
	    	return true;
	    }
	    
		sender.sendMessage(" - " + ChatColor.BLUE + "/mt randomize <teamsize>");
	    sender.sendMessage(" - " + ChatColor.BLUE + "/mt give [player]");
	    sender.sendMessage(" - " + ChatColor.BLUE + "/mt clear");
	    sender.sendMessage(" - " + ChatColor.BLUE + "/mt list");
    	return true;
	}
	
	@EventHandler
	public void on(PvPEnableEvent event) {
		PlayerUtils.broadcast(PREFIX + "Banners have been given.");
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			MysteryTeam team = getTeam(online);
			
			if (team == null) {
				continue;
			}
			
			ItemStack item = new ItemStack(Material.BANNER);
    		BannerMeta meta = (BannerMeta) item.getItemMeta();
    		meta.setBaseColor(team.getDyeColor());
    		item.setItemMeta(meta);
    		PlayerUtils.giveItem(online, item);
		}
	}
	
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		CraftingInventory inv = event.getInventory();
		ItemStack result = inv.getResult();
		
		if (result == null) {
			return;
		}
		
		if (result.getType() == Material.BANNER) {
			inv.setResult(new ItemStack(Material.AIR));
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		MysteryTeam team = getTeam(player);

		if (team == null) {
			return;
		}
		
		team.removePlayer(player);

		PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Player from team " + team.getName() + " died.");
		
		if (team.getSize() > 0) {
			PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Team " + team.getName() + " has §f" + team.getSize() + team.getChatColor() + (team.getSize() > 1 ? " players" : " player") + " left");
			return;
		}
		
		teams.remove(team);
		
		if (teams.size() > 1) {
			PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Team " + team.getName() + " eliminated. There are §f" + teams.size() + team.getChatColor() + " teams left.");
			return;
		}
		
		MysteryTeam killerTeam = null;
		
		for (MysteryTeam mTeam : teams) {
			killerTeam = mTeam;
			break;
		}
		
		PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Team " + team.getName() + " eliminated. " + killerTeam.getChatColor() + "The " + killerTeam.getName() + " team won!");
	}

	public enum MysteryTeam {
		GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, "Green"), 
		ORANGE(ChatColor.GOLD, DyeColor.ORANGE, "Orange"), 
		RED(ChatColor.RED, DyeColor.RED, "Red"), 
		LIGHT_BLUE(ChatColor.AQUA, DyeColor.LIGHT_BLUE, "Light blue"), 
		YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, "Yellow"), 
		LIME(ChatColor.GREEN, DyeColor.LIME, "Light Green"), 
		PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, "Pink"), 
		GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, "Gray"), 
		SILVER(ChatColor.GRAY, DyeColor.SILVER, "Light Gray"), 
		PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, "Purple"), 
		BLUE(ChatColor.BLUE, DyeColor.BLUE, "Blue"), 
		CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN, "Cyan"), 
		WHITE(ChatColor.WHITE, DyeColor.WHITE, "White"), 
		BLACK(ChatColor.BLACK, DyeColor.BLACK, "Black");

		private ChatColor cColor;
		private DyeColor dColor;
		private String name;
		
		private Set<UUID> players = new HashSet<UUID>();

		/**
		 * Constructor for mystery teams.
		 * 
		 * @param cColor The chat color.
		 * @param dColor The dye color.
		 * @param name The name.
		 */
		private MysteryTeam(ChatColor cColor, DyeColor dColor, String name) {
			this.cColor = cColor;
			this.dColor = dColor;
			this.name = name;
		}

		/**
		 * Get the name of the team.
		 * 
		 * @return The name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the chat color of the team.
		 * 
		 * @return The chat color.
		 */
		public ChatColor getChatColor() {
			return cColor;
		}

		/**
		 * Get the dye color of the team.
		 * 
		 * @return The dye color.
		 */
		public DyeColor getDyeColor() {
			return dColor;
		}

		/**
		 * Get a set of all UUID's on this team.
		 * 
		 * @return A set of UUID's.
		 */
		public Set<OfflinePlayer> getPlayers() {
			Set<OfflinePlayer> list = new HashSet<OfflinePlayer>();
			
			for (UUID uuid : players) {
				list.add(Bukkit.getOfflinePlayer(uuid));
			}
			
			return ImmutableSet.copyOf(list);
		}

		/**
		 * Get the size of the team.
		 * 
		 * @return The size.
		 */
		public int getSize() {
			return players.size();
		}

		/**
		 * Add the given player to the team.
		 * 
		 * @param player The player adding.
		 */
		public void addPlayer(OfflinePlayer player) {
			for (MysteryTeam team : teams) {
				if (team.hasPlayer(player)) {
					team.removePlayer(player);
				}
			}
			
			players.add(player.getUniqueId());
		}
		
		/**
		 * Remove the given player to the team.
		 * 
		 * @param player The player removing.
		 * @return True if successful, false otherwise.
		 */
		public boolean removePlayer(OfflinePlayer player) {
			return players.remove(player.getUniqueId());
		}

		/**
		 * Check if the given player is on this team.
		 * 
		 * @param player The player checking.
		 * @return True if he is, false otherwise.
		 */
		public boolean hasPlayer(OfflinePlayer player) {
			return players.contains(player.getUniqueId());
		}
	}
}