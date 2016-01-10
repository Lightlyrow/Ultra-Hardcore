package com.leontg77.ultrahardcore.listeners;

import static com.leontg77.ultrahardcore.Main.plugin;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.game.SpreadCommand;
import com.leontg77.ultrahardcore.commands.game.WhitelistCommand;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
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
	private Game game = Game.getInstance();
	
	@EventHandler
	public void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		Settings settings = Settings.getInstance();
		Spectator spec = Spectator.getInstance();
		
		// update names (name changes)
		for (String path : settings.getHOF().getKeys(false)) {
			String confUUID = settings.getHOF().getString(path + ".uuid", "none");
			String confName = settings.getHOF().getString(path + ".name", "none");
			
			if (confUUID.equals(player.getUniqueId().toString()) && !confName.equals(player.getName())) {
				settings.getHOF().set(path + ".name", player.getName());
			}
		}
		
		settings.saveHOF();
		
		User user = User.get(player);
		user.getFile().set("username", player.getName());
		user.getFile().set("uuid", player.getUniqueId().toString());
		user.getFile().set("ip", player.getAddress().getAddress().getHostAddress());
		
		if (!player.hasPermission("uhc.border")) {
			PermissionsManager.addPermissions(player);
        }
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		
		user.getFile().set("lastlogin", date.getTime());
		user.saveFile();
		
		PacketUtils.setTabList(player);
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
			if ((State.isState(State.INGAME) || State.isState(State.CLOSED) || State.isState(State.SCATTER)) && !player.isWhitelisted() && !spec.isSpectating(player)) {
				player.sendMessage(Main.PREFIX + "You joined a game without being whitelisted.");

				user.resetInventory();
				user.resetExp();
				
				spec.enableSpecmode(player);
			} else {
				PlayerUtils.broadcast("§8[§a+§8] " + user.getRankColor() + player.getName() + " §7joined.");
				
				if (user.isNew()) {
					File f = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
					
					PlayerUtils.broadcast(Main.PREFIX + "Welcome §6" + player.getName() + " §7to the server! §8[§a#" + NumberUtils.formatInt(f.listFiles().length) + "§8]");
				}
			}
		}
		
		HashMap<String, Location> scatter = SpreadCommand.scatterLocs;
		
		if (scatter.containsKey(player.getName()) && SpreadCommand.isReady) {
			if (State.isState(State.SCATTER)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 128));
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1726272000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1726272000, 10));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1726272000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1726272000, 2));
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "- §a" + player.getName() + " §7scheduled scatter.");
			player.teleport(scatter.get(player.getName()));
			scatter.remove(player.getName());
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
		
		if (!State.isState(State.INGAME) && !State.isState(State.SCATTER)) {
			player.teleport(Main.getSpawn());
		}
		
		if (!game.isRecordedRound()) {
			player.sendMessage("§8» §m----------§8[ §4§lArctic UHC §8]§m----------§8 «");
			
			if (GameUtils.getTeamSize(false, false).startsWith("No")) {
				player.sendMessage("§8» §c There are no games running currently.");
			} 
			else if (GameUtils.getTeamSize(false, false).startsWith("Open")) {
				player.sendMessage("§8» §7 Open PvP, use §a/a §7to join.");
			} 
			else {
				player.sendMessage("§8» §7 Host: §a" + game.getHost());
				player.sendMessage("§8» §7 Gamemode: §a" + GameUtils.getTeamSize(false, true) + game.getScenarios());
			}
			
			player.sendMessage("§8» §m---------------------------------§8 «");
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		
		if (User.fileExist(player.getUniqueId())) {
			PermissionsManager.addPermissions(player);
        }
		
		if (player.getName().startsWith("Mr") && player.getName().endsWith("Bar")) {
			event.disallow(Result.KICK_BANNED, "§8» §7No Mr####Bar names are allowed here, sorry. §8«");
			return;
		}
		
		if (event.getResult() == Result.KICK_BANNED) {
			BanList name = Bukkit.getBanList(Type.NAME);
			BanList ip = Bukkit.getBanList(Type.IP);
			
			String adress = event.getAddress().getHostAddress();
			
			if (name.getBanEntry(player.getName()) != null) {
				if (player.hasPermission("uhc.staff")) {
					name.pardon(player.getName());
					event.allow();
					return;
				}

				BanEntry ban = name.getBanEntry(player.getName());
				PlayerUtils.broadcast(Main.PREFIX + ChatColor.RED + player.getName() + " §7tried to join while being " + (ban.getExpiration() == null ? "banned" : "temp-banned") + " for:§c " + ban.getReason(), "uhc.staff");
				
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
			else if (ip.getBanEntry(adress) != null) {
				if (player.hasPermission("uhc.staff")) {
					ip.pardon(adress);
					event.allow();
					return;
				}

				BanEntry ban = ip.getBanEntry(adress);
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
		
		if (Spectator.getInstance().isSpectating(player)) {
			event.allow();
			return;
		}
		
		if (PlayerUtils.getPlayers().size() >= game.getMaxPlayers()) {
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
			
			String teamSize = GameUtils.getTeamSize(false, false);
			
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
				if (!WhitelistCommand.prewls && !State.isState(State.INGAME)) {
					event.disallow(Result.KICK_WHITELIST, "§4Pre-whitelist has been disabled\n\n" + event.getKickMessage());
					return;
				}
				
				event.allow();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLoginLater(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		
		if (event.getResult() != Result.ALLOWED) {
			PermissionsManager.removePermissions(player);
		}
	}
}