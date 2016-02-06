package com.leontg77.ultrahardcore.commands.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Fireworks;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.world.AntiStripmineFeature;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.WorldManager;

/**
 * End command class.
 * 
 * @author LeonTG77
 */
public class EndCommand extends UHCCommand {

	public EndCommand() {
		super("end", "<kills> <winners>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		final int totalKills = parseInt(args[0], "kill amount");

		final BoardManager board = BoardManager.getInstance();
		final TeamManager teams = TeamManager.getInstance();
		
		final Spectator spec = Spectator.getInstance();
		
		final List<String> winners = new ArrayList<String>();
		final Settings settings = Settings.getInstance();
		
		PlayerUtils.broadcast(Main.PREFIX + "The game has ended!");
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "The winners are:");
		
		for (int i = 1; i < args.length; i++) {
			final OfflinePlayer winner = PlayerUtils.getOfflinePlayer(args[i]);
			final User user = User.get(winner);

			if (!game.isRecordedRound() && !game.isPrivateGame()) {
				user.getFile().set("stats.wins", user.getFile().getInt("stats.wins") + 1);
				user.saveFile();
			}
			
			final int pKills;

			final Integer savedKills = Main.kills.get(winner.getName());
			final int boardKills = board.getScore(winner.getName());
			
			if (savedKills == null || savedKills < boardKills) {
				pKills = boardKills;
			} else {
				pKills = savedKills;
			}
			
			final String color = teams.getTeam(winner) == null ? "§7" : teams.getTeam(winner).getPrefix();
			
			PlayerUtils.broadcast(Main.PREFIX + "§8- " + color + winner.getName() + " §8(§a" + pKills + " §7 " + (pKills == 1 ? "kill" : "kills") + "§8)");
			winners.add(winner.getName());
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "With a total of §a" + totalKills + "§7 kills.");
		PlayerUtils.broadcast(" ");
		PlayerUtils.broadcast(Main.PREFIX + "Thanks for playing and congrats to the winners!");
		PlayerUtils.broadcast(Main.PREFIX + "Remember to check out the hall of fame by using §6/hof§7.");
		
		final String host = GameUtils.getHostName(game.getHost());
		
		final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Date date = new Date();

		final Fireworks firework = Fireworks.getInstance();
		FileUtils.updateUserFiles();
		
		int matchcount = 1;
		
		if (settings.getHOF().contains(host) && settings.getHOF().contains(host + ".games")) {
			matchcount = settings.getHOF().getConfigurationSection(host + ".games").getKeys(false).size() + 1;
		}

		if (!game.isRecordedRound()) {
			settings.getHOF().set(host + ".games." + matchcount + ".date", dateFormat.format(date));
			settings.getHOF().set(host + ".games." + matchcount + ".winners", winners);
			settings.getHOF().set(host + ".games." + matchcount + ".kills", totalKills);
			settings.getHOF().set(host + ".games." + matchcount + ".teamsize", GameUtils.getTeamSize(true, false).trim());
			settings.getHOF().set(host + ".games." + matchcount + ".scenarios", game.getScenarios());
			settings.saveHOF();
		}
		
		InvGUI.getInstance().setup();
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			scen.setEnabled(false);
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
		}
		
		State.setState(State.NOT_RUNNING);
		firework.startFireworkShow();
		
		Main.teamKills.clear();
		Main.kills.clear();

		Bukkit.getServer().setIdleTimeout(60);
		Main.plugin.saveData();

		game.setScenarios("games running");
		game.setMatchPost("none");
		game.setMaxPlayers(120);
		game.setTeamSize("No");
		
		final FeatureManager feat = FeatureManager.getInstance();
		feat.getFeature(AntiStripmineFeature.class).enable();
		
		teams.getSavedTeams().clear();

		for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
			whitelisted.setWhitelisted(false);
		}

		new BukkitRunnable() {
			public void run() {
				for (String entry : board.getBoard().getEntries()) {
					board.resetScore(entry);
				}
				
				final TeamManager teams = TeamManager.getInstance();
				
				for (Team team : teams.getTeams()) {
					for (String member : team.getEntries()) {
						team.removeEntry(member);
					}
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Reset scoreboards and teams.");

				final WorldManager manager = WorldManager.getInstance();
				
				for (World world : GameUtils.getGameWorlds()) {
					manager.deleteWorld(world);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Deleted used worlds.");
			}
		}.runTaskLater(Main.plugin, 300);

		new BukkitRunnable() {
			public void run() {
				String kickMessage = 
				"§8» §cThanks for playing! §8«" +
			    "\n" + 
			    "\n§7If you'd like to know about updates and upcoming games," +
			    "\n§7you can follow us on twitter §a@ArcticUHC§7!";
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.kickPlayer(kickMessage);
				}
				
				Bukkit.shutdown();
			}
		}.runTaskLater(Main.plugin, 1200);
		
		timer.stopTimers();
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		for (Player online : PlayerUtils.getPlayers()) {
			toReturn.add(online.getName());
		}
		
		return toReturn;
	}
}