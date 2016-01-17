package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
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
		if (!sender.hasPermission("uhc.spread")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			sender.sendMessage(Main.PREFIX + "Usage: /spread ");
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
		
		if (args[2].equalsIgnoreCase("*")) {
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
			
			if (Arena.getInstance().isEnabled()) {
				throw new CommandException("You can't scatter without disabling the arena.");
			}
			
			Parkour.getInstance().reset();
			State.setState(State.SCATTER);
			
			int teams = 0;
			int solo = 0;
			
			final BoardManager boardMan = BoardManager.getInstance();
			final TeamManager teamMan = TeamManager.getInstance();
			
			if (game.getTeamSize().toLowerCase().startsWith("cto")) {
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
				
				if (loopTeam.getSize() > 1) {
					teams++;
				} else {
					solo++;
				}
			}
			
			for (World worlds : GameUtils.getGameWorlds()) {
				worlds.setTime(0);
				worlds.setDifficulty(Difficulty.HARD);
				worlds.setPVP(false);
				
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
			
			if (teamSpread) {
				PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + teams + " §7teams and §a" + solo + " §7solos...");
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + Bukkit.getServer().getWhitelistedPlayers().size() + " §7players...");
			}
			
			for (Player online : PlayerUtils.getPlayers()) {
				online.playSound(online.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
			}
			
			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Finding scatter locations...");

					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
					}
					
					if (teams) {
						List<Location> loc = ScatterManager.findScatterLocations(Bukkit.getWorld(world), radius, te + so);
						
						int index = 0;
						
						for (Team tem : TeamManager.getInstance().getTeamsWithPlayers()) {
							for (String player : tem.getEntries()) {
								scatterLocs.put(player, loc.get(index));
								
								PlayerUtils.getOfflinePlayer(player).setWhitelisted(true);
							}
							index++;
						}
						
						for (OfflinePlayer online : Bukkit.getServer().getWhitelistedPlayers()) {
							if (BoardManager.getInstance().board.getEntryTeam(online.getName()) == null) {
								scatterLocs.put(online.getName(), loc.get(index));
								index++;
							}
						}
					} else {
						List<Location> loc = ScatterManager.findScatterLocations(Bukkit.getWorld(world), radius, Bukkit.getServer().getWhitelistedPlayers().size());
					
						int index = 0;
						
						for (OfflinePlayer wld : Bukkit.getServer().getWhitelistedPlayers()) {
							scatterLocs.put(wld.getName(), loc.get(index));
							index++;
						}
					}
				}
			}.runTaskLater(Main.plugin, 30);

			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Locations found, loading chunks...");

					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
					}
					
					final ArrayList<Location> locs = new ArrayList<Location>(scatterLocs.values());
					final ArrayList<String> names = new ArrayList<String>(scatterLocs.keySet());
					
					new BukkitRunnable() {
						int i = 0;
						
						public void run() {
							if (i < locs.size()) {
								if (sender instanceof Player) {
									Player player = (Player) sender;
									player.teleport(locs.get(i));
								} else {
									locs.get(i).getChunk().load(true);
								}
								i++;
							} else {
								cancel();
								locs.clear();
								PlayerUtils.broadcast(Main.PREFIX + "All chunks loaded, starting scatter...");

								for (Player online : PlayerUtils.getPlayers()) {
									online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
								}
								
								new BukkitRunnable() {
									int i = 0;
									
									public void run() {
										if (i < names.size()) {
											if (names.get(i) == null) {
												PlayerUtils.broadcast(Main.PREFIX + "- §4An error occured while scattering a player.");
												i++;
												return;
											}
											
											Player scatter = Bukkit.getServer().getPlayer(names.get(i));
											
											if (scatter == null) {
												PlayerUtils.broadcast(Main.PREFIX + "- §c" + names.get(i) + " §7offline, scheduled.");
												
												for (Player online : PlayerUtils.getPlayers()) {
													online.playSound(online.getLocation(), "random.pop", 1, 0);
												}
											} else {
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 128));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1726272000, 6));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 6));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1726272000, 10));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1726272000, 6));
												scatter.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1726272000, 2));
												scatter.teleport(scatterLocs.get(names.get(i)));
												PlayerUtils.broadcast(Main.PREFIX + "- §a" + names.get(i) + " §7has been scattered.");
												scatterLocs.remove(names.get(i));
												
												for (Player online : PlayerUtils.getPlayers()) {
													online.playSound(online.getLocation(), "random.pop", 1, 0);
												}
											}
											i++;
										} else {
											PlayerUtils.broadcast(Main.PREFIX + "The scatter has finished.");
											isReady = true;
											names.clear();
											cancel();
											
											for (Player online : PlayerUtils.getPlayers()) {
												online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
											}
										}
									}
								}.runTaskTimer(Main.plugin, 40, 3);
							}
						}
					}.runTaskTimer(Main.plugin, 5, 5);
				}
			}.runTaskLater(Main.plugin, 60);
		} else {
			final Player target = Bukkit.getPlayer(args[2]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "That player is not online.");
				return true;
			}

			PlayerUtils.broadcast(Main.PREFIX + "Scattering §a" + target.getName() + "§7...");

			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Finding scatter location...");
					
					if (teamSpread) {
						if (target.getScoreboard().getEntryTeam(target.getName()) == null) {
							List<Location> loc = ScatterManager.findScatterLocations(Bukkit.getWorld(world), radius, 1);
							scatterLocs.put(target.getName(), loc.get(0));
							return;
						}
						
						Team tem = target.getScoreboard().getEntryTeam(target.getName());
						
						for (String tm : tem.getEntries()) {
							Player temmate = Bukkit.getServer().getPlayer(tm);
							
							if (temmate != null) {
								scatterLocs.put(target.getName(), temmate.getLocation());
								break;
							}
						}
					} else {
						List<Location> loc = ScatterManager.findScatterLocations(Bukkit.getWorld(world), radius, 1);
						scatterLocs.put(target.getName(), loc.get(0));
					}
				}
			}.runTaskLater(Main.plugin, 30);

			new BukkitRunnable() {
				public void run() {
					PlayerUtils.broadcast(Main.PREFIX + "Location found, scattering...");
					
					if (!target.isOnline()) {
						PlayerUtils.broadcast(Main.PREFIX + "- §c" + target.getName() + " §7offline, scheduled.");
					} else {
						if (State.isState(State.SCATTER)) {
							target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1726272000, 128));
							target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1726272000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1726272000, 10));
							target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1726272000, 6));
							target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1726272000, 2));
						}
						
						target.setWhitelisted(true);
						target.teleport(scatterLocs.get(target.getName()));
						PlayerUtils.broadcast(Main.PREFIX + "- §a" + target.getName() + " §7has been scattered.");
						scatterLocs.remove(target.getName());
					}
				}
			}.runTaskLater(Main.plugin, 60);
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}