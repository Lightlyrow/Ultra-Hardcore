package com.leontg77.ultrahardcore.listeners;

import java.util.Date;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import com.leontg77.ultrahardcore.utils.GameUtils;
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
    public void on(final AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		final User user = User.get(player);

		final TeamManager teams = TeamManager.getInstance();
		final Spectator spec = Spectator.getInstance();

		final String message = event.getMessage();
		final Team team = teams.getTeam(player);
	
		final String name = (team == null || spec.isSpectating(player) ? "§f%s" : team.getPrefix() + "%s");
		final String messageAndColor = name.startsWith("§f") ? "§7%s" : "§f%s";
    	
    	if (game.isRecordedRound()) {
    		event.setFormat(name + " §8» " + messageAndColor);
    		return;
    	}
		
		if (VoteCommand.isRunning() && (message.equalsIgnoreCase("y") || message.equalsIgnoreCase("n"))) {
			if (!GameUtils.getGamePlayers().contains(player)) {
				player.sendMessage(ChatColor.RED + "You can only vote while playing the game.");
				event.setCancelled(true);
				return;
			}
			
			if (VoteCommand.hasVoted(player)) {
				player.sendMessage(ChatColor.RED + "You can only vote once.");
				event.setCancelled(true);
				return;
			}
			
			if (message.equalsIgnoreCase("y")) {
				player.sendMessage(Main.PREFIX + "You voted §ayes§7.");
				event.setCancelled(true);
				
				VoteCommand.addVote(player, true);
				return;
			}
			
			if (message.equalsIgnoreCase("n")) {
				player.sendMessage(Main.PREFIX + "You voted §cno§7.");
				event.setCancelled(true);
				
				VoteCommand.addVote(player, false);
			}
			return;
		}
    	
		if (user.isMuted()) {
			Date date = new Date();
			
			if (user.getMuteExpiration() == null || user.getMuteExpiration().getTime() > date.getTime()) {
				player.sendMessage(Main.PREFIX + "You have been muted for: §a" + user.getMutedReason());
				event.setCancelled(true);
				
				if (user.getMuteExpiration() == null) {
					player.sendMessage(Main.PREFIX + "Your mute is permanent.");
				} else {
					player.sendMessage(Main.PREFIX + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getMuteExpiration().getTime()));
				}
				return;
			}
			
			event.setCancelled(false);
			user.unmute();
		}
		
		Iterator<Player> it = event.getRecipients().iterator();
		
		// remove all people who ignore this player to see his message.
		while (it.hasNext()) {
			Player next = it.next();
			User nextUser = User.get(next);
			
			if (nextUser.isIgnoring(player)) {
				it.remove();
			}
		}
		
		if (user.getRank().getLevel() > 3) {
			event.setMessage(ChatColor.translateAlternateColorCodes('&', message));
		}

		if (user.getRank() == Rank.OWNER) {
			String uuid = player.getUniqueId().toString();
			String prefix;
			
			if (uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				prefix = "§3§oOwner";
			} else {
				prefix = "§4§oOwner";
			}

    		event.setFormat("§8[" + prefix + "§8] " + name + " §8» " + messageAndColor);
			return;
		}
		
		if (user.getRank() == Rank.HOST) {
    		event.setFormat("§8[§4Host§8] " + name + " §8» " + messageAndColor);
			return;
		}
		
		if (user.getRank() == Rank.TRIAL) {
    		event.setFormat("§8[§4Trial§8] " + name + " §8» " + messageAndColor);
			return;
		}
		
		if (user.getRank() == Rank.STAFF) {
    		event.setFormat("§8[§cStaff§8] " + name + " §8» " + messageAndColor);
			return;
		}
		
		if (user.getRank() == Rank.DONATOR) {
			if (game.isMuted()) {
				player.sendMessage(Main.PREFIX + "The chat is currently muted.");
				event.setCancelled(true);
				return;
			}

    		event.setFormat("§8[§a$$§8] " + name + " §8» " + messageAndColor);
			return;
		} 
		
		if (spec.isSpectating(player)) {
			if (game.isMuted()) {
				player.sendMessage(Main.PREFIX + "The chat is currently muted.");
				event.setCancelled(true);
				return;
			}

    		event.setFormat("§8[§9Spec§8] " + name + " §8» " + messageAndColor);
			return;
		} 
			
		if (game.isMuted()) {
			player.sendMessage(Main.PREFIX + "The chat is currently muted.");
			event.setCancelled(true);
			return;
		}

		event.setFormat(name + " §8» " + messageAndColor);
	}
	
  	@EventHandler
  	public void on(PlayerCommandPreprocessEvent event) {
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
  		
  		String command = message.split(" ")[0].substring(1).toLowerCase();
  		
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