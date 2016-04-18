package com.leontg77.ultrahardcore.listeners;

import java.io.File;
import java.util.Date;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Login listener class.
 * <p> 
 * Contains all eventhandlers for login releated events.
 * 
 * @author LeonTG77
 */
public class LoginListener implements Listener {
	private final Main plugin;
	
	private final Settings settings;
	private final Game game;

	private final ScatterManager scatter;
	private final SpecManager spec;
	
	private final PermissionsManager perm;
	
	/**
	 * Login listener class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 * @param settings The settings class.
	 * @param spec The spectator manager class.
	 * @param scatter The scatter manager class.
	 * @param perm The permission manager class.
	 */
	public LoginListener(Main plugin, Game game, Settings settings, SpecManager spec, ScatterManager scatter, PermissionsManager perm) {
		this.plugin = plugin;
		
		this.settings = settings;
		this.game = game;
		
		this.scatter = scatter;
		this.spec = spec;
		
		this.perm = perm;
	}
	
	@EventHandler
	public void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		// update names (name changes)
		for (String path : settings.getHOF().getKeys(false)) {
			String confUUID = settings.getHOF().getString(path + ".uuid", "none");
			String confName = settings.getHOF().getString(path + ".name", "none");
			
			if (confUUID.equals(player.getUniqueId().toString()) && !confName.equals(player.getName())) {
				settings.getHOF().set(path + ".name", player.getName());
				settings.saveHOF();
			}
		}
		
		User user = plugin.getUser(player);
		Date date = new Date();
		
		user.getFile().set("ip", player.getAddress().getAddress().getHostAddress());
		user.getFile().set("uuid", player.getUniqueId().toString());
		user.getFile().set("lastlogin", date.getTime());

		String oldName = user.getFile().getString("username", "none");

		// update names (name changes) and broadcast old name
		if (!oldName.equals(player.getName())) {
			PlayerUtils.broadcast(Main.PREFIX + "§a" + player.getName() + " §7was previously known as §a" + oldName + "§7.");
			user.getFile().set("username", player.getName());
		}
		
		user.saveFile();
		
		PacketUtils.setTabList(player, game);
		player.setNoDamageTicks(0);

		spec.hideSpectators(player);
		event.setJoinMessage(null);
		
		if (player.isDead()) {
			player.spigot().respawn();
		}
		
		if (spec.isSpectating(player)) {
			user.resetInventory();
			user.resetExp();
			
			spec.enableSpecmode(player);
		} else {
			if ((State.isState(State.ENDING) || State.isState(State.INGAME) || State.isState(State.CLOSED) || State.isState(State.SCATTER)) && !player.isWhitelisted() && !spec.isSpectating(player)) {
				player.sendMessage(Main.PREFIX + "You are not whitelisted, enabling spec mode...");

				user.resetInventory();
				user.resetExp();
				
				spec.enableSpecmode(player);
			} else {
				if (!user.getAlts().isEmpty()) {
					String alts = user.getAlts().toString();
					
					PlayerUtils.broadcast(Main.PREFIX + "§c" + player.getName() + " §7might be an alt of§8: " + alts.substring(1, alts.length() - 1) + "§8.", "uhc.staff");
				}
				
				PlayerUtils.broadcast("§8[§a+§8] " + user.getRankColor() + player.getName() + " §7joined. §8(§a" + plugin.getOnlineCount() + "§8/§a" + game.getMaxPlayers() + "§8)");
				
				if (user.isNew()) {
					File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
					
					PlayerUtils.broadcast(Main.PREFIX + "Welcome §6" + player.getName() + " §7to the server! §8[§a#" + NumberUtils.formatInt(folder.listFiles().length) + "§8]");
				}
			}
		}
		
		if (scatter.needsLateScatter(player) && !scatter.isScattering()) {
			if (State.isState(State.INGAME)) {
				for (PotionEffect effect : ScatterManager.FREEZE_EFFECTS) {
					if (player.hasPotionEffect(effect.getType())) {
						player.removePotionEffect(effect.getType());
					}
					
					player.addPotionEffect(effect);
				}
			}
			
			PlayerUtils.broadcast(Main.ARROW + "§8- §a" + player.getName() + " §7scheduled scatter.");
			scatter.handleLateScatter(player);
		}
		
		// incase they join with freeze effects.
		if (!State.isState(State.SCATTER) && player.hasPotionEffect(PotionEffectType.JUMP) && 
			player.hasPotionEffect(PotionEffectType.BLINDNESS) && 
			player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && 
			player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && 
			player.hasPotionEffect(PotionEffectType.SLOW) && 
			player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			
			player.removePotionEffect(PotionEffectType.JUMP);
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			player.removePotionEffect(PotionEffectType.SLOW);	
			player.removePotionEffect(PotionEffectType.INVISIBILITY);	
		}
		
		if (!game.isRecordedRound()) {
			player.sendMessage("§8» §m-----------§8[ §4Arctic UHC §8]§m------------§8 «");
			
			if (game.getAdvancedTeamSize(false, false).startsWith("No")) {
				player.sendMessage("§c   There are no games running currently.");
			} 
			else if (game.getAdvancedTeamSize(false, false).startsWith("Open")) {
				player.sendMessage("   §7Open PvP, use §a/a §7to join.");
			} 
			else {
				player.sendMessage("§7   Host: §a" + game.getHost());
				player.sendMessage("§7   Gamemode: §a" + game.getAdvancedTeamSize(false, true) + game.getScenarios());
			}
			
			player.sendMessage("§8» §m---------------------------------§8 «");
		}
		
		if (State.isState(State.INGAME) || State.isState(State.SCATTER) || State.isState(State.ENDING)) {
			if (player.hasPlayedBefore()) {
				return;
			}
		}
		
		player.teleport(plugin.getSpawn());
	}
	
	@EventHandler
	public void on(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		perm.addPermissions(player);
		
		BanList name = Bukkit.getBanList(Type.NAME);
		BanList ip = Bukkit.getBanList(Type.IP);
		
		String IP = event.getAddress().getHostAddress();
		
		if (IP.startsWith("69.65.")) { // http://whois.domaintools.com/69.65.39.193
			name.addBan(player.getName(), "DDoS", null, "CONSOLE");
			event.setResult(Result.KICK_BANNED);
		}
		
		if (IP.startsWith("151.80.11.") || IP.startsWith("164.132.80.") || IP.startsWith("176.31.75.") || IP.startsWith("178.33.27.") || IP.startsWith("91.121.231.")) {
			name.addBan(player.getName(), "MCLeaks", null, "CONSOLE");
			event.setResult(Result.KICK_BANNED);
		}
		
		if (event.getResult() == Result.KICK_BANNED) {
			if (name.getBanEntry(player.getName()) != null) {
				if (player.isOp()) {
					name.pardon(player.getName());
					event.allow();
					return;
				}

				BanEntry ban = name.getBanEntry(player.getName());
				if (!(ban.getReason().equalsIgnoreCase("MCLeaks")) {
					PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + player.getName() + " §7tried to join while being " + (ban.getExpiration() == null ? "banned" : "temp-banned") + " for:§c " + ban.getReason(), "uhc.staff");
				}
				
				event.setKickMessage(
				"§8» §7You have been §4" + (ban.getExpiration() == null ? "banned" : "temp-banned") + " §7from §6Arctic UHC §8«" +
				"\n" + 
				"\n§cReason §8» §7" + ban.getReason() +
				"\n§cBanned by §8» §7" + ban.getSource() + (ban.getExpiration() == null ? "" : "" +
				"\n§cExpires in §8» §7" + DateUtils.formatDateDiff(ban.getExpiration().getTime())) +
				"\n" +
				"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				);
			}
			else if (ip.getBanEntry(IP) != null) {
				if (player.isOp()) {
					ip.pardon(IP);
					event.allow();
					return;
				}

				BanEntry ban = ip.getBanEntry(IP);
				PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + player.getName() + " §7tried to join while being IP-banned for:§c " + ban.getReason(), "uhc.staff");
				
				event.setKickMessage(
				"§8» §7You have been §4IP banned §7from §6Arctic UHC §8«" +
				"\n" + 
				"\n§cReason §8» §7" + ban.getReason() +
				"\n§cBanned by §8» §7" + ban.getSource() + 
				"\n" +
				"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
				);
			}
			else {
				event.allow();
			}
			return;
		}
		
		if (spec.isSpectating(player)) {
			event.allow();
			return;
		}
		
		if (Bukkit.getOnlinePlayers().size() >= game.getMaxPlayers()) {
			if (!game.isRecordedRound()) {
				if (player.isWhitelisted() || player.isOp()) {
					event.allow();
					return;
				}
				
				if (player.hasPermission("uhc.staff")) {
					if (State.isState(State.INGAME)) {
						event.allow();
						return;
					}
				} 

				event.disallow(Result.KICK_FULL, "§8» §7The server is currently full, try again later §8«");
				return;
			}
		}
		
		if (event.getResult() == Result.KICK_WHITELIST) {
			if (player.isOp()) {
				event.allow();
				return;
			}
			
			String teamSize = game.getAdvancedTeamSize(false, false);
			
			if (teamSize.startsWith("No") || game.isRecordedRound() || game.isPrivateGame()) {
				event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThere are no games running");
			} else if (teamSize.startsWith("Open")) {
				Bukkit.setWhitelist(false);
				event.allow();
				return;
			} else {
				switch (State.getState()) {
				case CLOSED:
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThe game has closed, you were too late.");
					break;
				case INGAME:
				case SCATTER:
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThe game has already started");
					break;
				case NOT_RUNNING:
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThe game has not opened yet,\n§ccheck the post for open time.\n\n§7Match post: §a" + game.getMatchPost());
					break;
				case OPEN:
					Bukkit.setWhitelist(false);
					event.allow();
					break;
				default:
					event.setKickMessage("§8» §7You are not whitelisted §8«\n\n§cThere are no games running");
					break;
				}
			}
			
			if (player.hasPermission("uhc.prelist") && !game.isRecordedRound()) {
				if (!game.preWhitelists() && !State.isState(State.INGAME)) {
					event.disallow(Result.KICK_WHITELIST, "§4Pre-whitelist has is currently disabled!\n\n" + event.getKickMessage());
					return;
				}
				
				event.allow();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLater(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		
		if (event.getResult() != Result.ALLOWED) {
			perm.removePermissions(player);
		}
	}
}
