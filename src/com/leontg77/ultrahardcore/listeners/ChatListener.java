package com.leontg77.ultrahardcore.listeners;

import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.game.VoteCommand;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class ChatListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		User user = User.get(player);

		TeamManager teams = TeamManager.getInstance();
		Team team = teams.getTeam(player);
		
		String message = event.getMessage();
		
		String name = (team == null || team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName());
		String color = name.startsWith("§") ? "§f" : "§7";
		
		event.setCancelled(true);
    	
    	if (game.isRecordedRound()) {
    		PlayerUtils.broadcast("§7" + name + "§8 » §f" + message);
    		return;
    	}
		
		if (VoteCommand.running && (message.equalsIgnoreCase("y") || message.equalsIgnoreCase("n"))) {
			World world = player.getWorld();
			
			if (State.isState(State.INGAME) && world.getName().equals("lobby")) {
				player.sendMessage(ChatColor.RED + "You cannot vote when you are dead.");
				return;
			}
			
			Spectator spec = Spectator.getInstance();
			
			if (spec.isSpectating(player)) {
				player.sendMessage(ChatColor.RED + "You cannot vote as a spectator.");
				return;
			}
			
			if (VoteCommand.voted.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "You have already voted.");
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("y")) {
				player.sendMessage(Main.PREFIX + "You voted yes.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.yes++;
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("n")) {
				player.sendMessage(Main.PREFIX + "You voted no.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.no++;
			}
			return;
		}
    	
		if (user.isMuted()) {
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date date = new Date();
			
			if (user.getMuteExpiration() == null || user.getMuteExpiration().getTime() > date.getTime()) {
				player.sendMessage(Main.PREFIX + "You have been muted for: §a" + user.getMutedReason());
				
				if (user.getMuteExpiration() == null) {
					player.sendMessage(Main.PREFIX + "Your mute is permanent.");
				} else {
					player.sendMessage(Main.PREFIX + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getMuteExpiration().getTime()));
				}
				return;
			} else {
				user.unmute();
			}
		}

		Spectator spec = Spectator.getInstance();

		if (user.getRank() == Rank.OWNER) {
			String uuid = player.getUniqueId().toString();
			String prefix;
			
			if (uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				prefix = "§3§oOwner";
			} else {
				prefix = "§4§oOwner";
			}
			
			PlayerUtils.broadcast("§8[" + prefix + "§8] §f" + name + "§8 » " + color + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.HOST) {
			PlayerUtils.broadcast("§8[§4Host§8] §f" + name + "§8 » " + color + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.TRIAL) {
			PlayerUtils.broadcast("§8[§4Trial§8] §f" + name + "§8 » " + color + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.STAFF) {
			PlayerUtils.broadcast("§8[§cStaff§8] §f" + name + "§8 » " + color + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.DONATOR) {
			if (game.isMuted()) {
				player.sendMessage(Main.PREFIX + "The chat is currently muted.");
				return;
			}

			PlayerUtils.broadcast("§8[§aDonator§8] §f" + name + "§8 » " + color + ChatColor.translateAlternateColorCodes('&', message));
			return;
		} 
		
		if (spec.isSpectating(player)) {
			if (game.isMuted()) {
				player.sendMessage(Main.PREFIX + "The chat is currently muted.");
				return;
			}

			PlayerUtils.broadcast("§8[§9Spec§8] §f" + name + "§8 » " + color + message);
			return;
		} 
			
		if (game.isMuted()) {
			player.sendMessage(Main.PREFIX + "The chat is currently muted.");
			return;
		}

		PlayerUtils.broadcast("§f" + name + "§8 » §7" + message);
	}
	
  	@EventHandler
  	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
  		String message = event.getMessage();
  		Player player = event.getPlayer();
  		
  		Spectator spec = Spectator.getInstance();
  		
  		for (Player online : PlayerUtils.getPlayers()) {
  			if (online == player) {
  				continue;
  			}
  			
  			if (!online.hasPermission("uhc.cmdspy")) {
  				continue;
  			}
  			
  			if (!spec.hasCommandSpy(online) && State.isState(State.INGAME)) {
  				continue;
  			}
  			
  			if (online.getGameMode() != GameMode.CREATIVE && !spec.isSpectating(online)) {
  				continue;
  			}
  			
  			online.sendMessage("§e" + player.getName() + ": §7" + message);
  		}
  		
  		String command = message.split(" ")[0].substring(1);
  		
  		if (command.equalsIgnoreCase("me") || command.equalsIgnoreCase("kill")) {
  			player.sendMessage(Main.NO_PERM_MSG);
  			event.setCancelled(true);
  			return;
  		}
  		
  		if (command.startsWith("bukkit:") || command.startsWith("minecraft:")) {
  			if (player.hasPermission("uhc.admin")) {
  				return;
  			}
  			
  			player.sendMessage(Main.NO_PERM_MSG);
  			event.setCancelled(true);
  			return;
  		}
  		
  		if (command.equalsIgnoreCase("rl") || command.equalsIgnoreCase("reload") || command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("restart")) {
  			if (!State.isState(State.INGAME)) {
  				if (!command.equalsIgnoreCase("stop") && !command.equalsIgnoreCase("restart")) {
  					return;
  				}
  				
  				if (!player.hasPermission("uhc.restart")) {
  					return;
  				}
  				
  				for (Player online : PlayerUtils.getPlayers()) {
  					online.kickPlayer("§8» §7The server is restarting §8«");
  				}
  				
  				Bukkit.shutdown();
  				event.setCancelled(true);
  				return;
  			}
  			
  			String finalCommand = command.replace("rl", "reload");
  			
  			player.sendMessage(ChatColor.RED + "You may not want to " + finalCommand + " when a game is running.");
  			player.sendMessage(ChatColor.RED + "If you still want to " + finalCommand + ", do it in the console.");
  			event.setCancelled(true);
  			return;
  		}
  	}
}