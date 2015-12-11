package com.leontg77.ultrahardcore;

import java.util.ArrayList;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main.BorderShrink;
import com.leontg77.ultrahardcore.Spectator.SpecInfo;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.commands.team.TeamCommand;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Astrophobia;
import com.leontg77.ultrahardcore.scenario.scenarios.Kings;
import com.leontg77.ultrahardcore.scenario.scenarios.SlaveMarket;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.EntityUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * The runnable class for all the runnables
 * <p>
 * This class contains methods for starting countdowns, timers, RR timer and RR countdown.
 * 
 * @author LeonTG77
 */
public class Timers {
	private static Timers instance = new Timers();
	private Game game = Game.getInstance();

	public static int taskSeconds;
	public static int taskMinutes;

	public static int time;
	public static int pvp;
	public static int meetup;
	
	public static int timeSeconds;
	public static int pvpSeconds;
	public static int meetupSeconds;
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Timers getInstance() {
		return instance;
	}
	
	/**
	 * Start the countdown for the game.
	 */
	public void start() {
		for (int i = 0; i < 150; i++) {
			for (Player online : PlayerUtils.getPlayers()) {
				online.sendMessage("§0");
			}
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "The game will start in §a30§7 seconds.");
		PlayerUtils.broadcast(Main.PREFIX + "Opening game info inventory in §a5§7 seconds.");

		new BukkitRunnable() {
			public void run() {
				InvGUI.getInstance().openGameInfo(PlayerUtils.getPlayers());
			}
		}.runTaskLater(Main.plugin, 100);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "Remember to use §a/uhc §7for all game information.");
			}
		}.runTaskLater(Main.plugin, 250);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "If you have a question and §a/uhc§7 didn't help, ask in §a/helpop");
			}
		}.runTaskLater(Main.plugin, 300);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "To find the matchpost, use §a/post");
			}
		}.runTaskLater(Main.plugin, 350);

		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast(Main.PREFIX + "This is a §a" + GameUtils.getTeamSize() + "- " + game.getScenarios());
			}
		}.runTaskLater(Main.plugin, 400);

		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§45", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §45.");
			}
		}.runTaskLater(Main.plugin, 500);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§c4", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §c4.");
			}
		}.runTaskLater(Main.plugin, 520);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§63", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §63.");
			}
		}.runTaskLater(Main.plugin, 540);

		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §e2.");
			}
		}.runTaskLater(Main.plugin, 560);

		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§a1", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "Game starting in §a1.");
			}
		}.runTaskLater(Main.plugin, 580);
		
		new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");
				PlayerUtils.broadcast(Main.PREFIX + "The game has started!");
				PlayerUtils.broadcast(Main.PREFIX + "PvP will be enabled in: §a" + game.getPvP() + " minutes.");
				PlayerUtils.broadcast(Main.PREFIX + "Meetup is in: §a" + game.getMeetup() + " minutes.");
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");
				
				ScenarioManager scen = ScenarioManager.getInstance();
				Spectator spec = Spectator.getInstance();

				TeamManager teams = TeamManager.getInstance();
				BoardManager sb = BoardManager.getInstance();

				Bukkit.getPluginManager().registerEvents(new SpecInfo(), Main.plugin);
				Bukkit.getPluginManager().callEvent(new GameStartEvent());
				
				State.setState(State.INGAME);
				game.setArenaBoard(false);
				
				sb.setScore("§8» §a§lPvE", 1);
				sb.setScore("§8» §a§lPvE", 0);
				
				pvp = game.getPvP();
				meetup = game.getMeetup();
				
				pvpSeconds = (pvp * 60);
				meetupSeconds = (meetup * 60);
				
				timer();

				Bukkit.getServer().setIdleTimeout(10);
				SpecInfo.getTotalDiamonds().clear();
				SpecInfo.getTotalGold().clear();

				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(20));
				}
				
				for (Team team : teams.getTeamsWithPlayers()) {
					Main.teamKills.put(team.getName(), 0);
					
					ArrayList<String> players = new ArrayList<String>(team.getEntries());
					TeamCommand.savedTeams.put(team.getName(), players);
				}
				
				for (String entry : sb.board.getEntries()) {
					if (entry.equals("§a§o@ArcticUHC") || sb.getScore(entry) > 0) {
						sb.setScore(entry, sb.getScore(entry) - 9);
					}
				}
				
				for (World world : GameUtils.getGameWorlds()) {
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					world.setTime(0);
					
					world.setGameRuleValue("doDaylightCycle", "true");
					world.setSpawnFlags(false, true);
					world.setThundering(false);
					world.setStorm(false);
					
					if (game.getBorderShrink() == BorderShrink.START) {
						world.getWorldBorder().setSize(300, meetupSeconds);
					}
					
					for (Entity mob : world.getEntities()) {
						if (EntityUtils.isButcherable(mob.getType())) {
							mob.remove();
						}
					}
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
					for (Achievement a : Achievement.values()) {
						if (online.hasAchievement(a)) {
							online.removeAchievement(a);
						}
					}
					
					User user = User.get(online);
					
					if (spec.isSpectating(online)) {
						PacketUtils.sendTitle(online, "§aGo!", "§7Have fun spectating!", 1, 20, 1);
					} else {
						PacketUtils.sendTitle(online, "§aGo!", "§7Good luck, have fun!", 1, 20, 1);
						user.increaseStat(Stat.GAMESPLAYED);
						
						if (online.getGameMode() != GameMode.SURVIVAL) {
							online.setGameMode(GameMode.SURVIVAL);
						}
					}

					user.resetEffects();
					user.resetHealth();
					user.resetFood();
					user.resetExp();

					Main.kills.put(online.getName(), 0);
					
					Kings kings = scen.getScenario(Kings.class);

					if (kings.isEnabled() && kings.getKings().contains(online.getName())) {
		        		online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
					} else {
						online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100));
					}
					
					if (spec.isSpectating(online)) {
						continue;
					}
					
					if (scen.getScenario(SlaveMarket.class).isEnabled()) {
						PlayerInventory inv = online.getInventory();

						for (ItemStack item : inv.getContents()) {
							if (item == null) {
								continue;
							}
							
							if (item.getType() == Material.DIAMOND) {
								continue;
							}
							
							inv.removeItem(item);	
						}
						
				        inv.setArmorContents(null);
				        online.setItemOnCursor(new ItemStack(Material.AIR));

				        InventoryView openInventory = online.getOpenInventory();
				        
				        if (openInventory.getType() == InventoryType.CRAFTING) {
				            openInventory.getTopInventory().clear();
				        }
					} else {
						user.resetInventory();
					}
				}
			}
		}.runTaskLater(Main.plugin, 600);
	}
	
	/**
	 * Start the timers.
	 */
	public void timer() {
		if (Bukkit.getScheduler().isQueued(taskMinutes) || Bukkit.getScheduler().isCurrentlyRunning(taskMinutes)) {
			Bukkit.getScheduler().cancelTask(taskMinutes);
		}
		
		if (Bukkit.getScheduler().isQueued(taskSeconds) || Bukkit.getScheduler().isCurrentlyRunning(taskSeconds)) {
			Bukkit.getScheduler().cancelTask(taskSeconds);
		}
		
		taskMinutes = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				ScenarioManager scen = ScenarioManager.getInstance();
				BoardManager sb = BoardManager.getInstance();
				
				time++;
				pvp--;
				meetup--;
				
				if (time == 1) {
					PlayerUtils.broadcast(Main.PREFIX + "The chat has been enabled.");
					game.setMuted(false);
				}
				
				if (time == 2) {
					for (World world : GameUtils.getGameWorlds()) {
						world.setSpawnFlags(true, true);
					}
					
					PlayerUtils.broadcast(Main.PREFIX + "Hostile mobs can now spawn.");
				}

				if (pvp == 0) {
					PlayerUtils.broadcast(Main.PREFIX + "PvP/iPvP has been enabled.");
					Bukkit.getPluginManager().callEvent(new PvPEnableEvent());
					
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendTitle(online, "", "§4PvP has been enabled!", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
					}
					
					for (World world : GameUtils.getGameWorlds()) {
						world.setPVP(true);
						
						if (game.getBorderShrink() == BorderShrink.PVP) {
							world.getWorldBorder().setSize(300, meetup * 60);
						}
					}
					
					game.setPregameBoard(false);
					
					for (String entry : sb.board.getEntries()) {
						if (sb.getScore(entry) != 0 && !entry.equals("§8» §a§lPvE")) {
							sb.resetScore(entry);
						}
					}
				}
				
				if (meetup == 0) {
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					PlayerUtils.broadcast(" ");
					PlayerUtils.broadcast(ChatColor.RED + " Meetup is now, head to 0,0!");
					PlayerUtils.broadcast(" ");
					PlayerUtils.broadcast(ChatColor.RED + " You may be do anything you want as long");
					PlayerUtils.broadcast(ChatColor.RED + " as your inside 300x300 on the surface!");
					PlayerUtils.broadcast(" ");
					
					if (game.getBorderShrink() == BorderShrink.MEETUP) {
						PlayerUtils.broadcast(ChatColor.RED + " Borders will shrink in 2 minutes.");
						PlayerUtils.broadcast(" ");
					}
					
					PlayerUtils.broadcast(ChatColor.DARK_GRAY + "»»»»»»»»»»»»»»»«««««««««««««««");
					
					Bukkit.getPluginManager().callEvent(new MeetupEvent());
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.WITHER_DEATH, 1, 1);
					}

					for (World world : GameUtils.getGameWorlds()) {
						world.setThundering(false);
						world.setStorm(false);

						if (!scen.getScenario(Astrophobia.class).isEnabled()) {
							world.setGameRuleValue("doDaylightCycle", "false");
							world.setTime(6000);
						}
					}
				}
				
				if (game.getBorderShrink() == BorderShrink.MEETUP) {
					if (meetup == -2) {
						PlayerUtils.broadcast(Main.PREFIX + "Border will now shrink to §6300x300 §7over §a10 §7minutes.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}

						for (World world : GameUtils.getGameWorlds()) {
							world.getWorldBorder().setSize(300, 600);
						}
					}
					
					if (meetup == -12) {
						PlayerUtils.broadcast(Main.PREFIX + "Border has stopped shrinking.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
					}
				}
				
				if (meetup > 0) {
					String meetupToString = String.valueOf(meetup);
					
					if (meetupToString.equals("1") || meetupToString.endsWith("5") || meetupToString.endsWith("0")) {
						PlayerUtils.broadcast(Main.PREFIX + "Meetup is in §a" + DateUtils.advancedTicksToString(meetup * 60) + "§7.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
						
						if (meetupToString.equals("1")) {
							PlayerUtils.broadcast(Main.PREFIX + "Start preparing to head to 0,0.");
							return;
						}
					}
				}
				
				if (pvp > 0) {
					String pvpToString = String.valueOf(pvp);
					
					if (pvpToString.equals("1") || pvpToString.endsWith("5") || pvpToString.endsWith("0")) {
						PlayerUtils.broadcast(Main.PREFIX + "PvP will be enabled in §a" + DateUtils.advancedTicksToString(pvp * 60) + "§7.");
						
						for (Player online : PlayerUtils.getPlayers()) {
							online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
						}
					}
				}
			}
		}, 1200, 1200);
		
		taskSeconds = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			int finalHeal = 20;
			int timeToBorder = 121;
			
			public void run() {
				timeSeconds++;
				pvpSeconds--;
				meetupSeconds--;
				
				if (timeSeconds == 20) {
					PlayerUtils.broadcast(Main.PREFIX + "Final heal has been given.");
					PlayerUtils.broadcast(Main.PREFIX + "Do not ask for another one.");
					
					Bukkit.getPluginManager().callEvent(new FinalHealEvent());
					
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendTitle(online, "§6Final heal!", "§7Do not ask for another one", 5, 10, 5);
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
						
						User user = User.get(online);
						user.resetHealth();
						user.resetFood();
					}
				}
				
				if (timeSeconds < 20) {
					finalHeal--;
					
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7Final heal is given in §8» §a" + DateUtils.ticksToString(finalHeal));
					}
				} else if (pvpSeconds > 0) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7PvP is enabled in §8» §a" + DateUtils.ticksToString(pvpSeconds));
					}
				} else if (meetupSeconds > 0) {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§7Meetup is in §8» §a" + DateUtils.ticksToString(meetupSeconds));
					}
				} else {
					for (Player online : PlayerUtils.getPlayers()) {
						PacketUtils.sendAction(online, "§8» §6Meetup is now! §8«");
					}
					
					timeToBorder--;
					
					if (timeToBorder == 60 || timeToBorder == 30 || timeToBorder == 10 || timeToBorder == 5 || 
						timeToBorder == 4 || timeToBorder == 3 || timeToBorder == 2 || timeToBorder == 1) {
						
						PlayerUtils.broadcast(Main.PREFIX + "The border starts shrinking in §a" + DateUtils.advancedTicksToString(timeToBorder) + "§7.");
					}
				}
			}
		}, 20, 20);
	}
	
	/**
	 * Start the countdown for the recorded round.
	 */
	public void startRR() {
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.sendTitle(online, "§c3", "", 1, 20, 1);
			online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
		}
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§e2", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}.runTaskLater(Main.plugin, 20);
		
		new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PacketUtils.sendTitle(online, "§a1", "", 1, 20, 1);
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
				}
			}
		}.runTaskLater(Main.plugin, 40);
		
		new BukkitRunnable() {
			public void run() {
				ScenarioManager scen = ScenarioManager.getInstance();
				Spectator spec = Spectator.getInstance();

				PluginManager manager = Bukkit.getPluginManager();
				manager.registerEvents(new SpecInfo(), Main.plugin);
				
				State.setState(State.INGAME);
				game.setArenaBoard(false);
				
				time = 20;
				pvp = 0;
				meetup = 1;
				
				timerRR();

				Bukkit.getServer().setIdleTimeout(10);
				SpecInfo.getTotalDiamonds().clear();
				SpecInfo.getTotalGold().clear();
				
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");
				PlayerUtils.broadcast(Main.PREFIX + "The game has started!");
				PlayerUtils.broadcast("§8» §m---------------------------------§8 «");
				
				for (World world : GameUtils.getGameWorlds()) {
					world.setDifficulty(Difficulty.HARD);
					world.setPVP(false);
					world.setTime(0);
					
					world.setGameRuleValue("doDaylightCycle", "true");
					world.setThundering(false);
					world.setStorm(false);
					
					if (game.getBorderShrink() == BorderShrink.START) {
						world.getWorldBorder().setSize(300, meetupSeconds);
					}
					
					for (Entity mob : world.getEntities()) {
						if (EntityUtils.isButcherable(mob.getType())) {
							mob.remove();
						}
					}
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
					for (Achievement a : Achievement.values()) {
						if (online.hasAchievement(a)) {
							online.removeAchievement(a);
						}
					}
					
					if (spec.isSpectating(online)) {
						PacketUtils.sendTitle(online, "§aGo!", "§7Have fun spectating!", 1, 20, 1);
					} else {
						PacketUtils.sendTitle(online, "§aGo!", "§7Good luck, have fun!", 1, 20, 1);
						
						if (online.getGameMode() != GameMode.SURVIVAL) {
							online.setGameMode(GameMode.SURVIVAL);
						}
					}
					
					User user = User.get(online);

					user.resetEffects();
					user.resetHealth();
					user.resetFood();
					user.resetExp();
					
					Kings kings = scen.getScenario(Kings.class);

					if (kings.isEnabled() && kings.getKings().contains(online.getName())) {
		        		online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 0)); 
		            	online.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
					} else {
						online.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100));
					}
					
					if (spec.isSpectating(online)) {
						continue;
					}
					
					if (scen.getScenario(SlaveMarket.class).isEnabled()) {
						PlayerInventory inv = online.getInventory();

						for (ItemStack item : inv.getContents()) {
							if (item == null) {
								continue;
							}
							
							if (item.getType() == Material.DIAMOND) {
								continue;
							}
							
							inv.removeItem(item);	
						}
						
				        inv.setArmorContents(null);
				        online.setItemOnCursor(new ItemStack(Material.AIR));

				        InventoryView openInventory = online.getOpenInventory();
				        
				        if (openInventory.getType() == InventoryType.CRAFTING) {
				            openInventory.getTopInventory().clear();
				        }
					} else {
						user.resetInventory();
					}
				}
			}
		}.runTaskLater(Main.plugin, 60);
	}
	
	/**
	 * Start the recorded round timers.
	 */
	public void timerRR() {
		if (Bukkit.getScheduler().isQueued(taskMinutes) || Bukkit.getScheduler().isCurrentlyRunning(taskMinutes)) {
			Bukkit.getScheduler().cancelTask(taskMinutes);
		}
		
		taskMinutes = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				pvp++;
				time--;
				
				if (pvp == 20) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a1 §8| §7Start of episode §a2");
					PlayerUtils.broadcast(Main.PREFIX + "PvP has been enabled!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					
					for (World world : GameUtils.getGameWorlds()) {
						world.setPVP(true);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 40) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a2 §8| §7Start of episode §a3");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 60) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a3 §8| §7Start of episode §a4");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 80) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a4 §8| §7Start of episode §a5");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 100) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a5 §8| §7Start of episode §a6");
					PlayerUtils.broadcast(Main.PREFIX + "Perma day activated!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					
					for (World world : GameUtils.getGameWorlds()) {
						world.setGameRuleValue("doDaylightCycle", "false");
						world.setTime(6000);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 120) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a6 §8| §7Start of episode §a7");
					PlayerUtils.broadcast(Main.PREFIX + "Meetup is now!");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 140) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a7 §8| §7Start of episode §a8");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 160) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a8 §8| §7Start of episode §a9");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
				
				if (pvp == 180) {
					PlayerUtils.broadcast(Main.PREFIX + "End of episode §a9 §8| §7Start of episode §a10");
					
					for (Player online : PlayerUtils.getPlayers()) {
						online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
					}
					time = 20;
					meetup++;
				}
			}
		}, 1200, 1200);
	}
}