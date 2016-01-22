package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.EntityUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Spread command class.
 * 
 * @author LeonTG77
 */
public class SpreadCommand extends UHCCommand {

	public SpreadCommand() {
		super("spread", "<teamspread> <player|*>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}

		final Settings settings = Settings.getInstance();
		final Game game = Game.getInstance();
		
		final ScatterManager manager = ScatterManager.getInstance();
		final World world = game.getWorld();
		
		if (world == null) {
			throw new CommandException("There are no worlds called '" + settings.getConfig().getString("world", "girhgqeruiogh") + "'.");
		}
		
		final boolean teamSpread = parseBoolean(args[0], "Team Spread");
		final TeamManager teamMan = TeamManager.getInstance();
		
		if (args[1].equalsIgnoreCase("*")) {
			switch (State.getState()) {
			case NOT_RUNNING:
				throw new CommandException("You can't scatter when no games are running.");
			case OPEN:
				throw new CommandException("You can't scatter when the server isn't whitelisted.");
			case SCATTER:
				throw new CommandException("You can't scatter while a scatter is currently running.");
			case INGAME:
				throw new CommandException("You can't scatter when the game has started.");
			default:
				break;
			}
			
			if (!Bukkit.hasWhitelist()) {
				throw new CommandException("You can't scatter when the server isn't whitelisted.");
			}
			
			if (game.teamManagement()) {
				throw new CommandException("You can't scatter without disabling team management.");
			}
			
			if (!game.isMuted()) {
				throw new CommandException("You can't scatter without muting the chat.");
			}
			
			if (Arena.getInstance().isEnabled()) {
				throw new CommandException("You can't scatter without disabling the arena.");
			}
			
			Parkour.getInstance().reset();
			State.setState(State.SCATTER);
			
			int teams = 0;
			int solo = 0;
			
			List<String> toScatter = new ArrayList<String>();
			game.setPreWhitelists(true);
			
			if (game.getTeamSize().toLowerCase().startsWith("cto") && teamSpread) {
				for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
					if (teamMan.getTeam(whitelisted) != null) {
						continue;
					}
					
					Team team = teamMan.findAvailableTeam();
					
					if (team != null) {
						teamMan.joinTeam(team, whitelisted);
					}
				}
			}
			
			for (Team loopTeam : teamMan.getTeams()) {
				if (loopTeam.getSize() == 0) {
					continue;
				}

				toScatter.add(loopTeam.getName());
				
				if (loopTeam.getSize() > 1) {
					teams++;
				} else {
					solo++;
				}
			}
			
			if (!teamSpread) {
				for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
					toScatter.add(whitelisted.getName());
					solo++;
				}
			}
			
			manager.setTeamScatter(teamSpread);
			manager.setWorld(world);
			
			manager.scatter(ImmutableList.copyOf(toScatter));
			
			if (teamSpread) {
				PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + teams + " §7teams and §a" + solo + " §7solos...");
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + solo + " §7players...");
			}
			
			for (Player online : PlayerUtils.getPlayers()) {
				online.playSound(online.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
			}
			
			for (World worlds : GameUtils.getGameWorlds()) {
				worlds.setDifficulty(Difficulty.HARD);
				worlds.setPVP(false);
				worlds.setTime(0);
				
				worlds.setGameRuleValue("doDaylightCycle", "true");
				worlds.setSpawnFlags(false, true);
				worlds.setThundering(false);
				worlds.setStorm(false);
				
				for (Entity mob : worlds.getEntities()) {
					if (EntityUtils.isButcherable(mob.getType())) {
						mob.remove();
					}
				}
			}
		} else {
			final Player target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				throw new CommandException("'" + args[1] + "' is not online.");
			}

			PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + target.getName() + "§7...");

			if (teamSpread) {
				Team team = teamMan.getTeam(target);
				
				if (team != null) {
					for (OfflinePlayer teammate : teamMan.getPlayers(team)) {
						if (teammate.getPlayer() == null) {
							continue;
						}
						
						PlayerUtils.broadcast(Main.PREFIX + "Scattered §a" + target.getName() + "§7.");
						target.teleport(teammate.getPlayer());
						return true;
					}
				}
			}
			
			manager.setTeamScatter(false);
			manager.setWorld(world);
			
			manager.scatter(ImmutableList.of(target.getName()));
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			toReturn.add("true");
			toReturn.add("false");
		}
		
		if (args.length == 2) {
			toReturn.add("*");
			
			for (Player online : PlayerUtils.getPlayers()) {
				toReturn.add(online.getName());
			}
		}
		
		return toReturn;
	}
}