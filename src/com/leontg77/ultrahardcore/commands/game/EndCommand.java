package com.leontg77.ultrahardcore.commands.game;

import static com.leontg77.ultrahardcore.Main.plugin;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Fireworks;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.Spectator.SpecInfo;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.team.TeamCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.worlds.WorldManager;

/**
 * End command class.
 * 
 * @author LeonTG77
 */
public class EndCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!sender.hasPermission("uhc.end")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Main.PREFIX + "Usage: /end <kills> <winners>");
			return true;
		}
		
		int kills;
		
		try {
			kills = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a vaild number.");
			return true;
		}

		Spectator spec = Spectator.getInstance();
		Game game = Game.getInstance();
		
		Settings settings = Settings.getInstance();
		ArrayList<String> winners = new ArrayList<String>();
		
		PlayerUtils.broadcast(Main.PREFIX + "The game is now over!");
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "The winners are:");
		
		for (int i = 1; i < args.length; i++) {
			OfflinePlayer winner = PlayerUtils.getOfflinePlayer(args[i]);
			
			User user = User.get(winner);

			if (!game.isRecordedRound() && !Bukkit.getOfflinePlayer(game.getHost()).getName().equalsIgnoreCase("LeonsPrivate")) {
				user.getFile().set("stats.wins", user.getFile().getInt("stats.wins") + 1);
				user.saveFile();
			}
			
			PlayerUtils.broadcast("�8� �7" + args[i]);
			winners.add(args[i]);
		}
		
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "With �a" + kills + "�7 kills.");
		PlayerUtils.broadcast(Main.PREFIX + "View the hall of fame with �a/hof");
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "Congrats on the win and thanks for playing!");
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String host = GameUtils.getHostConfigName(GameUtils.getHostName(game.getHost()));
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();

		Fireworks firework = Fireworks.getInstance();
		FileUtils.updateUserFiles();
		
		int matchcount = 1;
		
		if (settings.getHOF().contains(host)) {
			matchcount = settings.getHOF().getConfigurationSection(host).getKeys(false).size() + 1;
		}

		if (!game.isRecordedRound()) {
			settings.getHOF().set(host + "." + matchcount + ".date", dateFormat.format(date));
			settings.getHOF().set(host + "." + matchcount + ".winners", winners);
			settings.getHOF().set(host + "." + matchcount + ".kills", kills);
			settings.getHOF().set(host + "." + matchcount + ".teamsize", GameUtils.getTeamSize().trim());
			settings.getHOF().set(host + "." + matchcount + ".scenarios", game.getScenarios());
			settings.saveHOF();
		}
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			scen.setEnabled(false);
		}

		for (World world : Bukkit.getWorlds()) {
			world.save();
		}

		for (Player online : PlayerUtils.getPlayers()) {
			if (spec.isSpectating(online)) {
				spec.disableSpecmode(online);
			}
			
			for (Player onlineTwo : PlayerUtils.getPlayers()) {
				online.showPlayer(onlineTwo);
				onlineTwo.showPlayer(online);
			}

			online.setGameMode(GameMode.SURVIVAL);
			online.teleport(Main.getSpawn());
			online.setMaxHealth(20.0);
			online.setFireTicks(0);
			
			User user = User.get(online);
			user.reset();
			
			online.saveData();
		}
		
		HandlerList.unregisterAll(new SpecInfo());
		State.setState(State.NOT_RUNNING);

		spec.spectators.clear();
		spec.specinfo.clear();
		spec.cmdspy.clear();
		
		TeamCommand.savedTeams.clear();
		firework.startFireworkShow();
		
		SpecInfo.getTotalDiamonds().clear();
		SpecInfo.getTotalGold().clear();
		
		Main.teamKills.clear();
		Main.kills.clear();

		Bukkit.getServer().setIdleTimeout(60);
		Main.plugin.saveData();

		game.setScenarios("games running");
		game.setAntiStripmine(true);
		game.setMatchPost("none");
		game.setMaxPlayers(150);
		game.setTeamSize(0);
		game.setFFA(true);
		
		TeamManager teams = TeamManager.getInstance();
		Team team = teams.getTeam("spec");
		
		for (String member : team.getEntries()) {
			team.removeEntry(member);
		}

		for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
			whitelisted.setWhitelisted(false);
		}

		new BukkitRunnable() {
			public void run() {
				BoardManager board = BoardManager.getInstance();
				
				for (String entry : board.board.getEntries()) {
					board.resetScore(entry);
				}
				
				TeamManager teams = TeamManager.getInstance();
				
				for (Team team : teams.getTeams()) {
					for (String member : team.getEntries()) {
						team.removeEntry(member);
					}
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Reset scoreboards and teams.");

				WorldManager manager = WorldManager.getInstance();
				
				for (World world : GameUtils.getGameWorlds()) {
					manager.deleteWorld(world);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Deleted used worlds.");
			}
		}.runTaskLater(Main.plugin, 600);
		
		File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
		File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
		
		for (File dataFiles : playerData.listFiles()) {
			dataFiles.delete();
		}
		
		for (File statsFiles : stats.listFiles()) {
			statsFiles.delete();
		}
		
		try {
			Bukkit.getServer().getScheduler().cancelTask(Timers.taskMinutes);
		} catch (Exception e) {
			plugin.getLogger().warning("Could not cancel task " + Timers.taskMinutes);
		}
		
		try {
			Bukkit.getServer().getScheduler().cancelTask(Timers.taskSeconds);
		} catch (Exception e) {
			plugin.getLogger().warning("Could not cancel task " + Timers.taskSeconds);
		}
		return true;
	}
}