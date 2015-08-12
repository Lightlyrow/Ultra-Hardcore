package com.leontg77.uhc.listeners;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Data;
import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Main.State;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.cmds.HOFCommand;
import com.leontg77.uhc.cmds.SpreadCommand;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.BlockUtils;
import com.leontg77.uhc.util.PlayerUtils;
import com.leontg77.uhc.util.PortalUtils;
import com.leontg77.uhc.util.RecipeUtils;
import com.leontg77.uhc.util.ServerUtils;

public class PlayerListener implements Listener {
	private Settings settings = Settings.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);
		
		Data data = Data.getData(player);
		data.getFile().set("username", player.getName());
		data.saveFile();
		
		player.setNoDamageTicks(0);
		
		Spectator.getManager().hideAll(player);
		PlayerUtils.handlePermissions(player);
		PlayerUtils.setTabList(player);
		
		if (Main.relog.containsKey(player.getName())) {
			Main.relog.get(player.getName()).cancel();
			Main.relog.remove(player.getName());
		}
		
		if (!TeamCommand.invites.containsKey(player)) {
			TeamCommand.invites.put(player, new ArrayList<Player>());
		}
		
		if (Main.spectating.contains(player.getName())) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			
			Spectator.getManager().set(player, true, false);
		}
		
		if (State.isState(State.INGAME) && !player.isWhitelisted() && !Main.spectating.contains(player.getName())) {
			player.sendMessage(Main.prefix() + "You joined a game that you didn't play from the start (or you was idle for too long).");
			
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setExp(0);
			
			Spectator.getManager().set(player, true, false);
		}
		
		if (!Main.spectating.contains(player.getName())) {
			PlayerUtils.broadcast("�8[�a+�8] �7" + player.getName() + " has joined.");
			if (data.isNew()) {
				PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getName() + " �7just joined for the first time.");
				
				File f = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
				PlayerUtils.broadcast(Main.prefix() + "The server has now �a" + f.listFiles().length + "�7 unique joins.");
			}
		}
		
		player.sendMessage("�8---------------------------");
		player.sendMessage(" �8� �6Welcome to Ultra Hardcore");
		player.sendMessage("�8---------------------------");
		if (ServerUtils.getTeamSize().equals("No")) {
			player.sendMessage(" �8� �cNo games scheduled");
		} else if (ServerUtils.getTeamSize().equals("Open")) {
			player.sendMessage(" �8� �7Open PvP, use �a/a �7to join.");
		} else {
			player.sendMessage(" �8� �aHost: �7" + Settings.getInstance().getConfig().getString("game.host"));
			player.sendMessage(" �8� �aTeamsize: �7" + ServerUtils.getTeamSize());
			player.sendMessage(" �8� �aGamemode: �7" + Settings.getInstance().getConfig().getString("game.scenarios"));
		}
		player.sendMessage("�8---------------------------");
		
		if (SpreadCommand.scatterLocs.containsKey(player.getName())) {
			if (State.isState(State.SCATTER)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 128));
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 6));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1000000, 10));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 6));
			}
			player.teleport(SpreadCommand.scatterLocs.get(player.getName()));
			PlayerUtils.broadcast(Main.prefix() + "- �a" + player.getName() + " �7scheduled scatter.");
			SpreadCommand.scatterLocs.remove(player.getName());
		}
		
		if (State.isState(State.LOBBY)) {
			World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
			double x = settings.getData().getDouble("spawn.x");
			double y = settings.getData().getDouble("spawn.y");
			double z = settings.getData().getDouble("spawn.z");
			float yaw = (float) settings.getData().getDouble("spawn.yaw");
			float pitch = (float) settings.getData().getDouble("spawn.pitch");
			final Location loc = new Location(w, x, y, z, yaw, pitch);
			player.teleport(loc);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		event.setQuitMessage(null);
		
		if (!Main.spectating.contains(player.getName())) {
			PlayerUtils.broadcast("�8[�c-�8] �7" + player.getName() + " has left.");
			
			if (!State.isState(State.LOBBY)) {
				Main.relog.put(player.getName(), new BukkitRunnable() {
					public void run() {
						if (!player.isOnline()) {
							if (player.isWhitelisted()) {
								player.setWhitelisted(false);
								Scoreboards.getManager().resetScore(player.getName());
								Main.relog.remove(player.getName());
								
								for (ItemStack content : player.getInventory().getContents()) {
									if (content != null) {
										Item item = player.getWorld().dropItem(player.getLocation().add(0.5, 0.7, 0.5), content);
										item.setVelocity(new Vector(0, 0.2, 0));
									}
								}

								for (ItemStack armorContent : player.getInventory().getArmorContents()) {
									if (armorContent != null && armorContent.getType() != Material.AIR) {
										Item item = player.getWorld().dropItem(player.getLocation().add(0.5, 0.7, 0.5), armorContent);
										item.setVelocity(new Vector(0, 0.2, 0));
									}
								}
								
								ExperienceOrb exp = player.getWorld().spawn(player.getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
								exp.setExperience((int) player.getExp());
								exp.setVelocity(new Vector(0, 0.2, 0));
								
								player.getInventory().clear();
								player.getInventory().setArmorContents(null);
								player.setExp(0);
								
								PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getName() + " �7took too long to come back.");
								PlayerDeathEvent event = new PlayerDeathEvent(player, new ArrayList<ItemStack>(), 0, null);
								Bukkit.getServer().getPluginManager().callEvent(event);
							}
						}
					}
				});
				
				Main.relog.get(player.getName()).runTaskLater(Main.plugin, 12000);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity().getPlayer();
		
		if (!Arena.getManager().isEnabled()) {
			player.setWhitelisted(false);
			
			if (State.isState(State.INGAME)) {
				Data data = Data.getData(player);
				data.increaseStat("deaths");
			}
			
			if (Main.deathlightning) {
			    player.getWorld().strikeLightningEffect(player.getLocation());
			}

		    if (Main.goldenheads) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						player.getLocation().getBlock().setType(Material.NETHER_FENCE);
						player.getLocation().add(0, 1, 0).getBlock().setType(Material.SKULL);
					        
						try {
					        Skull skull = (Skull) player.getLocation().add(0, 1, 0).getBlock().getState();
						    skull.setSkullType(SkullType.PLAYER);
						    skull.setOwner(player.getName());
						    skull.setRotation(BlockUtils.getBlockFaceDirection(player.getLocation()));
						    skull.update();
						    
						    Block b = player.getLocation().add(0, 1, 0).getBlock();
						    b.setData((byte) 0x1, true);
						} catch (Exception e) {
							Bukkit.getLogger().warning(ChatColor.RED + "Could not place player skull.");
						}
					}
				}, 1L);
		    }

			if (player.getKiller() == null) {
		        Scoreboards.getManager().setScore("�a�lPvE", Scoreboards.getManager().getScore("�a�lPvE", true) + 1, true);
				Scoreboards.getManager().resetScore(player.getName());
				return;
			}
			
			Player killer = player.getKiller();

			if (State.isState(State.INGAME)) {
				Data killerData = Data.getData(killer);
				killerData.increaseStat("kills");
			}

	        Scoreboards.getManager().setScore(killer.getName(), Scoreboards.getManager().getScore(killer.getName(), true) + 1, true);
			Scoreboards.getManager().resetScore(player.getName());
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		
		World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
		double x = settings.getData().getDouble("spawn.x");
		double y = settings.getData().getDouble("spawn.y");
		double z = settings.getData().getDouble("spawn.z");
		float yaw = (float) settings.getData().getDouble("spawn.yaw");
		float pitch = (float) settings.getData().getDouble("spawn.pitch");
		
		Location loc = new Location(w, x, y, z, yaw, pitch);
		event.setRespawnLocation(loc);
		
		if (!Arena.getManager().isEnabled() && !State.isState(State.LOBBY)) {
			player.sendMessage(Main.prefix() + "�7Thanks for playing our game, it really means a lot :)");
			player.sendMessage(Main.prefix() + "�7Follow us on twtter to know when our next games are: @ArcticUHC");
			if (player.hasPermission("uhc.prelist")) {
				player.sendMessage("�8�l� �7You will be put into spectator mode in 15 seconds. (No spoiling please)");
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						if (!State.isState(State.LOBBY) && player.isOnline() && !Main.spectating.contains(player.getName())) {
							Spectator.getManager().set(player, true, false);
						}
					}
				}, 300);
			} else {
				player.sendMessage("�8�l� �7You have 45 seconds to say your goodbyes. (No spoiling please)");
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						if (!State.isState(State.LOBBY) && player.isOnline() && !Main.spectating.contains(player.getName())) {
							player.kickPlayer("�8� �7Thanks for playing! �8�");
						}
					}
				}, 900);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Data data = Data.getData(player);
		
		if (VoteCommand.vote && (event.getMessage().equalsIgnoreCase("y") || event.getMessage().equalsIgnoreCase("n"))) {
			if (!State.isState(State.LOBBY) && player.getWorld().getName().equals("lobby")) {
				player.sendMessage(ChatColor.RED + "You cannot vote when you are dead.");
				event.setCancelled(true);
				return;
			}
			
			if (Main.spectating.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "You cannot vote as a spectator.");
				event.setCancelled(true);
				return;
			}
			
			if (Main.voted.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "You have already voted.");
				event.setCancelled(true);
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("y")) {
				player.sendMessage(Main.prefix() + "You voted yes.");
				VoteCommand.yes++;
				event.setCancelled(true);
				Main.voted.add(player.getName());
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("n")) {
				player.sendMessage(Main.prefix() + "You voted no.");
				VoteCommand.no++;
				event.setCancelled(true);
				Main.voted.add(player.getName());
			}
			return;
		}
    	
		if (PermissionsEx.getUser(player).inGroup("Host")) {
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			
			if (settings.getConfig().getString("game.host").equals(player.getName())) {
				PlayerUtils.broadcast("�4�lHost �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
			} else {
				PlayerUtils.broadcast("�4�lCo Host �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
			}	
		}
		else if (PermissionsEx.getUser(player).inGroup("Trial")) {
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("�4�lTrial Host �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
		}
		else if (PermissionsEx.getUser(player).inGroup("Staff")) {
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("�c�lStaff �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
		}
		else if (PermissionsEx.getUser(player).inGroup("VIP")) {
			if (Main.muted) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				event.setCancelled(true);
				return;
			}
			
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("�5�lVIP �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
		} 
		else {
			if (Main.muted) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				event.setCancelled(true);
				return;
			}
			if (data.isMuted()) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}
			
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast((team != null ? team.getPrefix() + player.getName() : player.getName()) + "�8 � �f" + event.getMessage());
		} 
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.hasPermission("uhc.commandspy") && (online.getGameMode() == GameMode.CREATIVE || Main.spectating.contains(online.getName())) && online != player) {
				online.sendMessage(ChatColor.YELLOW + player.getName() + ": �7" + event.getMessage());
			}
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/me")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/bukkit:") && !player.hasPermission("uhc.admin")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/minecraft:") && !player.hasPermission("uhc.admin")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/kill")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/rl")) {
			if (!State.isState(State.LOBBY)) {
				player.sendMessage(ChatColor.RED + "You might not want to reload when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to reload, do it in the console.");
				event.setCancelled(true);
			}
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/reload")) {
			if (!State.isState(State.LOBBY)) {
				player.sendMessage(ChatColor.RED + "You might not want to reload when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to reload, do it in the console.");
				event.setCancelled(true);
			}
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/border3000")) {
			if (player.hasPermission("uhc.border")) {
				player.getWorld().getWorldBorder().setSize(2999);
				if (player.getWorld().getEnvironment() == Environment.NETHER) {
					player.getWorld().getWorldBorder().setCenter(0, 0);
				} else {
					player.getWorld().getWorldBorder().setCenter(0.5, 0.5);
				}
				player.getWorld().getWorldBorder().setWarningDistance(0);
				player.getWorld().getWorldBorder().setWarningTime(60);
				player.getWorld().getWorldBorder().setDamageAmount(0.1);
				player.getWorld().getWorldBorder().setDamageBuffer(50);
				PlayerUtils.broadcast(Main.prefix() + "Border setup with radius of 3000x3000.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/border2000")) {
			if (player.hasPermission("uhc.border")) {
				player.getWorld().getWorldBorder().setSize(1999);
				if (player.getWorld().getEnvironment() == Environment.NETHER) {
					player.getWorld().getWorldBorder().setCenter(0, 0);
				} else {
					player.getWorld().getWorldBorder().setCenter(0.5, 0.5);
				}
				player.getWorld().getWorldBorder().setWarningDistance(0);
				player.getWorld().getWorldBorder().setWarningTime(60);
				player.getWorld().getWorldBorder().setDamageAmount(0.1);
				player.getWorld().getWorldBorder().setDamageBuffer(50);
				PlayerUtils.broadcast(Main.prefix() + "Border setup with radius of 2000x2000.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/perma")) {
			if (player.hasPermission("uhc.perma")) {
				player.getWorld().setGameRuleValue("doDaylightCycle", "false");
				player.getWorld().setTime(6000);
				PlayerUtils.broadcast(Main.prefix() + "Permaday enabled.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/matchpost")) {
			player.sendMessage(Main.prefix() + "Match post: �a" + settings.getConfig().getString("matchpost"));
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/post")) {
			player.sendMessage(Main.prefix() + "Match post: �a" + settings.getConfig().getString("matchpost"));
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/killboard")) {
			if (player.hasPermission("uhc.killboard")) {
				if (Main.killboard) {
					for (String e : Scoreboards.getManager().kills.getScoreboard().getEntries()) {
						Scoreboards.getManager().resetScore(e);
					}
					PlayerUtils.broadcast(Main.prefix() + "Pregame board disabled.");
					Main.killboard = false;
				} else {
					PlayerUtils.broadcast(Main.prefix() + "Pregame board enabled.");
					if (Main.ffa) {
						Scoreboards.getManager().setScore("�a ", 10, true);
						Scoreboards.getManager().setScore("�cArena:", 9, true);
						Scoreboards.getManager().setScore("�7/a ", 8, true);
						Scoreboards.getManager().setScore("�b ", 7, true);
						Scoreboards.getManager().setScore("�cTeamsize:", 6, true);
						Scoreboards.getManager().setScore("�7" + ServerUtils.getTeamSize(), 5, true);
						Scoreboards.getManager().setScore("�c ", 4, true);
						Scoreboards.getManager().setScore("�cScenarios:", 3, true);
						Scoreboards.getManager().setScore("�7" + settings.getConfig().getString("game.scenarios"), 2, true);
						Scoreboards.getManager().setScore("�d ", 1, true);
					} else {
						Scoreboards.getManager().setScore("�e ", 13, true);
						Scoreboards.getManager().setScore("�cTeam:", 12, true);
						Scoreboards.getManager().setScore("�7/team", 11, true);
						Scoreboards.getManager().setScore("�a ", 10, true);
						Scoreboards.getManager().setScore("�cArena:", 9, true);
						Scoreboards.getManager().setScore("�7/a ", 8, true);
						Scoreboards.getManager().setScore("�b ", 7, true);
						Scoreboards.getManager().setScore("�cTeamsize:", 6, true);
						Scoreboards.getManager().setScore("�7" + ServerUtils.getTeamSize(), 5, true);
						Scoreboards.getManager().setScore("�c ", 4, true);
						Scoreboards.getManager().setScore("�cScenarios:", 3, true);
						Scoreboards.getManager().setScore("�7" + settings.getConfig().getString("game.scenarios"), 2, true);
						Scoreboards.getManager().setScore("�d ", 1, true);
					}
					Main.killboard = true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/arenaboard")) {
			if (player.hasPermission("uhc.arenaboard")) {
				if (Main.arenaboard) {
					for (String e : Scoreboards.getManager().ab.getScoreboard().getEntries()) {
						Scoreboards.getManager().resetScore(e);
					}
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been disabled.");
					Scoreboards.getManager().kills.setDisplaySlot(DisplaySlot.SIDEBAR);
					Main.arenaboard = false;
				} else {
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been enabled.");
					Scoreboards.getManager().ab.setDisplaySlot(DisplaySlot.SIDEBAR);
					Main.arenaboard = true;

					Scoreboards.getManager().setScore("�a�lPvE", 1, false);
					Scoreboards.getManager().setScore("�a�lPvE", 0, false);
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/text")) {
			event.setCancelled(true);
			
			ArrayList<String> ar = new ArrayList<String>();
			for (String arg : event.getMessage().split(" ")) {
				ar.add(arg);
			}
			ar.remove(0);
			String[] args = ar.toArray(new String[ar.size()]);
			
			if (player.hasPermission("uhc.text")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Usage: /text <message>");
					return;
				}
				
				StringBuilder bu = new StringBuilder();
				
				for (int i = 0; i < args.length; i++) {
					bu.append(args[i]).append(" ");
				}
				
				ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
				stand.setCustomName(ChatColor.translateAlternateColorCodes('&', bu.toString().trim()));
				stand.setCustomNameVisible(true);
				stand.setGravity(false);
				stand.setVisible(false);
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
	}
	
	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		CommandSender player = event.getSender();
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/border3000")) {
			if (player.hasPermission("uhc.border")) {
				player.sendMessage(ChatColor.RED + "Only players can setup brorders.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/border2000")) {
			if (player.hasPermission("uhc.border")) {
				player.sendMessage(ChatColor.RED + "Only players can setup brorders.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/perma")) {
			if (player.hasPermission("uhc.perma")) {
				for (World world : Bukkit.getServer().getWorlds()) {
					world.setGameRuleValue("doDaylightCycle", "false");
					world.setTime(6000);
				}
				PlayerUtils.broadcast(Main.prefix() + "Permaday enabled.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/matchpost")) {
			player.sendMessage(Main.prefix() + "Match post: �a" + settings.getConfig().getString("matchpost"));
			event.setCancelled(true);
		}
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/post")) {
			player.sendMessage(Main.prefix() + "Match post: �a" + settings.getConfig().getString("matchpost"));
			event.setCancelled(true);
		}
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/killboard")) {
			if (player.hasPermission("uhc.killboard")) {
				if (Main.killboard) {
					for (String e : Scoreboards.getManager().kills.getScoreboard().getEntries()) {
						Scoreboards.getManager().resetScore(e);
					}
					PlayerUtils.broadcast(Main.prefix() + "Pregame board disabled.");
					Main.killboard = false;
				} else {
					PlayerUtils.broadcast(Main.prefix() + "Pregame board enabled.");
					if (Main.ffa) {
						Scoreboards.getManager().setScore("�a ", 10, true);
						Scoreboards.getManager().setScore("�cArena:", 9, true);
						Scoreboards.getManager().setScore("�7/a ", 8, true);
						Scoreboards.getManager().setScore("�b ", 7, true);
						Scoreboards.getManager().setScore("�cTeamsize:", 6, true);
						Scoreboards.getManager().setScore("�7" + ServerUtils.getTeamSize(), 5, true);
						Scoreboards.getManager().setScore("�c ", 4, true);
						Scoreboards.getManager().setScore("�cScenarios:", 3, true);
						Scoreboards.getManager().setScore("�7" + settings.getConfig().getString("game.scenarios"), 2, true);
						Scoreboards.getManager().setScore("�d ", 1, true);
					} else {
						Scoreboards.getManager().setScore("�e ", 13, true);
						Scoreboards.getManager().setScore("�cTeam:", 12, true);
						Scoreboards.getManager().setScore("�7/team", 11, true);
						Scoreboards.getManager().setScore("�a ", 10, true);
						Scoreboards.getManager().setScore("�cArena:", 9, true);
						Scoreboards.getManager().setScore("�7/a ", 8, true);
						Scoreboards.getManager().setScore("�b ", 7, true);
						Scoreboards.getManager().setScore("�cTeamsize:", 6, true);
						Scoreboards.getManager().setScore("�7" + ServerUtils.getTeamSize(), 5, true);
						Scoreboards.getManager().setScore("�c ", 4, true);
						Scoreboards.getManager().setScore("�cScenarios:", 3, true);
						Scoreboards.getManager().setScore("�7" + settings.getConfig().getString("game.scenarios"), 2, true);
						Scoreboards.getManager().setScore("�d ", 1, true);
					}
					Main.killboard = true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/arenaboard")) {
			if (player.hasPermission("uhc.arenaboard")) {
				if (Main.arenaboard) {
					for (String e : Scoreboards.getManager().ab.getScoreboard().getEntries()) {
						Scoreboards.getManager().resetScore(e);
					}
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been disabled.");
					Scoreboards.getManager().kills.setDisplaySlot(DisplaySlot.SIDEBAR);
					Main.arenaboard = false;
				} else {
					PlayerUtils.broadcast(Main.prefix() + "Arena board has been enabled.");
					Scoreboards.getManager().ab.setDisplaySlot(DisplaySlot.SIDEBAR);
					Main.arenaboard = true;

					Scoreboards.getManager().setScore("�a�lPvE", 1, false);
					Scoreboards.getManager().setScore("�a�lPvE", 0, false);
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("/text")) {
			event.setCancelled(true);
			
			if (player.hasPermission("uhc.text")) {
				player.sendMessage(ChatColor.RED + "Only players can create floating text.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		if (event.getResult() == Result.KICK_BANNED) {
			if (player.hasPermission("uhc.staff")) {
				event.allow();
				return;
			}

			String reason = Bukkit.getBanList(Type.NAME).getBanEntry(player.getName()).getReason();
			
			event.setKickMessage("�8� �cBanned: �7" + reason + " �8�");
			PlayerUtils.broadcast("�c" + player.getName() + "tried to join while being banned for: " + reason, "uhc.staff");
			return;
		}
		
		if (event.getResult() == Result.KICK_WHITELIST) {
			if (player.hasPermission("uhc.prelist")) {
				event.allow();
				return;
			}
			
			event.setKickMessage("�8� �7You are not whitelisted �8�");
			return;
		}
		
		if (PlayerUtils.getPlayers().size() >= settings.getConfig().getInt("maxplayers")) {
			if (player.hasPermission("uhc.staff")) {
				event.allow();
				return;
			} 
			event.disallow(Result.KICK_FULL, "�8� �7The server is full �8�");
		} else {
			event.allow();
		}
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		if (settings.getConfig().getString("game.scenarios") == null) {
			return;
		}
		
		String scenarios = ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("game.scenarios"));
		
		StringBuilder builder= new StringBuilder();
		
		for (String st : scenarios.split(" ")) {
			builder.append("�6" + st + " ");
		}
		
		event.setMotd("�4�lUltra Hardcore �8- �71.8 �8- �a" + ServerUtils.getState() + "�r \n" + 
		ChatColor.GOLD + ServerUtils.getTeamSize() + " " + builder.toString().trim() + "�8 - �4Host: �7" + Settings.getInstance().getConfig().getString("game.host"));

		int max = settings.getConfig().getInt("maxplayers");
		event.setMaxPlayers(max);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getReason().equals("disconnect.spam")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FIRE && player.getWorld().getName().equals("lobby")) {
        	event.setCancelled(true);
        	return;
        }
        
		if (!Main.spectating.contains(player.getName())) {
			return;
		}
        
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			InvGUI.getManager().openSelector(player);
		} 
		else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			ArrayList<Player> players = new ArrayList<Player>();
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (!Main.spectating.contains(online.getName())) {
					players.add(online);
				}
			}
			
			if (players.size() > 0) {
				Player target = players.get(new Random().nextInt(players.size()));
				player.teleport(target.getLocation());
				player.sendMessage(Main.prefix() + "You teleported to �a" + target.getName() + "�7.");
			} else {
				player.sendMessage(Main.prefix() + "No players to teleport to.");
			}
		}
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		if (event.getInventory().getTitle().equals("Player Inventory")) {
			event.setCancelled(true);
		}
		
		if (event.getInventory().getTitle().endsWith("Fame")) {
			event.setCancelled(true);
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("�aNext page")) {
				HOFCommand.page.put(player, HOFCommand.page.get(player) + 1);
				player.openInventory(HOFCommand.inv2);
			}
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("�aPrevious page")) {
				HOFCommand.page.put(player, HOFCommand.page.get(player) - 1);
				player.openInventory(HOFCommand.inv);
			}
		}
		
		if (event.getInventory().getTitle().equals("Ultra Hardcore Rules")) {
			event.setCancelled(true);
		}
		
		if (!Main.spectating.contains(player.getName())) {
			return;
		}
        
		if (event.getInventory().getTitle().equals("Player Selector")) {
			Player target = Bukkit.getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().substring(2, event.getCurrentItem().getItemMeta().getDisplayName().length()));
			
			if (target != null) {
				player.teleport(target);
			}
			
			event.setCancelled(true);
		}
		
		if (item.getType() == Material.COMPASS) {
			if (event.isLeftClick()) {
				ArrayList<Player> players = new ArrayList<Player>();
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					if (!Main.spectating.contains(online.getName())) {
						players.add(online);
					}
				}
				
				if (players.size() > 0) {
					Player target = players.get(new Random().nextInt(players.size()));
					player.teleport(target.getLocation());
					player.sendMessage(Main.prefix() + "You teleported to �a" + target.getName() + "�7.");
				} else {
					player.sendMessage(Main.prefix() + "No players to teleport to.");
				}
				event.setCancelled(true);
				return;
			}
			InvGUI.getManager().openSelector(player);
			event.setCancelled(true);
		}
		
		if (item.getType() == Material.INK_SACK) {
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 0));
			}
			event.setCancelled(true);
		}
		
		if (item.getType() == Material.FEATHER) {
			player.teleport(new Location(player.getWorld(), 0, 100, 0));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (Main.invsee.containsKey(event.getInventory())) {
			Main.invsee.get(event.getInventory()).cancel();
			Main.invsee.remove(event.getInventory());
		}
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ArmorStand) {
			event.setCancelled(true);
			return;
		}
		
		if (!(event.getRightClicked() instanceof Player)) {
			return;
		}
		
    	Player player = (Player) event.getPlayer();
    	Player clicked = (Player) event.getRightClicked();
				
		if (Main.spectating.contains(player.getName())) {
			if (Main.spectating.contains(clicked.getName())) {
				return;
			}
			
			InvGUI.getManager().openInv(player, clicked);
		}
    }

	@EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
		TravelAgent travel = event.getPortalTravelAgent();
		Player player = event.getPlayer();
		Location from = event.getFrom();
		
		if (Main.nether) {
			String fromName = event.getFrom().getWorld().getName();

	        String targetName;
	        if (from.getWorld().getEnvironment() == Environment.NETHER) {
	            if (!fromName.endsWith("_nether")) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the overworld, contact the staff now.");
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 7);
	        } else if (from.getWorld().getEnvironment() == Environment.NORMAL) {
	            if (!PortalUtils.isPortal(Material.PORTAL, from)) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the nether, contact the staff now.");
	                return;
	            }

	            targetName = fromName + "_nether";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.prefix() + "The nether has not been created.");
	            return;
	        }

	        Location to = new Location(world, from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch());
	        to = travel.findOrCreate(to);

	        if (to != null) {
	            event.setTo(to);
	        }
		}
		
		if (Main.theend) {
			String fromName = event.getFrom().getWorld().getName();

	        String targetName;
	        if (event.getFrom().getWorld().getEnvironment() == Environment.THE_END) {
	            if (!fromName.endsWith("_end")) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the overworld, contact the staff now.");
	                return;
	            }

	            targetName = fromName.substring(0, fromName.length() - 4);
	        } else if (event.getFrom().getWorld().getEnvironment() == Environment.NORMAL) {
	            if (!PortalUtils.isPortal(Material.ENDER_PORTAL, event.getFrom())) {
	            	player.sendMessage(Main.prefix() + "Could not teleport you to the end, contact the staff now.");
	                return;
	            }
	            
	            targetName = fromName + "_end";
	        } else {
	            return;
	        }

	        World world = Bukkit.getServer().getWorld(targetName);
	        
	        if (world == null) {
            	player.sendMessage(Main.prefix() + "The end has not been created.");
	            return;
	        }

	        Location to = new Location(world, 100.0, 49, 0, 90f, 0f);

			for (int y = to.getBlockY() - 1; y <= to.getBlockY() + 2; y++) {
				for (int x = to.getBlockX() - 2; x <= to.getBlockX() + 2; x++) {
					for (int z = to.getBlockZ() - 2; z <= to.getBlockZ() + 2; z++) {
						if (y == 48) {
							to.getWorld().getBlockAt(x, y, z).setType(Material.OBSIDIAN);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						} else {
							to.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
							to.getWorld().getBlockAt(x, y, z).getState().update();
						}
					}
				}
			}
			
			event.setTo(to);
		}
    }
	
	@EventHandler
	public void onPreCraftEvent(PrepareItemCraftEvent event) {
		ItemStack item = event.getRecipe().getResult();
		
		/**
		 * @author Ghowden
		 */
        if (RecipeUtils.areSimilar(event.getRecipe(), Main.headRecipe)) {
            ItemMeta meta = item.getItemMeta();
            String name = "N/A";
          
            for (ItemStack items : event.getInventory().getContents()) {
                if (items.getType() == Material.SKULL_ITEM) {
                    SkullMeta skullMeta = (SkullMeta) items.getItemMeta();
                    name = skullMeta.getOwner();
                }
            }

            List<String> list = meta.getLore();
            list.add(ChatColor.AQUA + "Made from the head of: " + name);
            meta.setLore(list);
            item.setItemMeta(meta);
            event.getInventory().setResult(item);
        }
		
		if (item != null && item.getType() == Material.GOLDEN_APPLE) {
			if (item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals("�6Golden Head")) {
				if (ScenarioManager.getInstance().getScenario("VengefulSpirits").isEnabled()) {
					return;
				}
				
				if (!Main.goldenheads) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
			
			if (item.getDurability() == 1) {
				if (!Main.notchapples) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
		}
		
		if (item != null && item.getType() == Material.SPECKLED_MELON) {
			if (Main.harderCrafting) {
				if (event.getRecipe() instanceof ShapedRecipe) {
					if (((ShapedRecipe) event.getRecipe()).getIngredientMap().values().contains(new ItemStack (Material.GOLD_NUGGET))) {
						event.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}
			} else {
				if (event.getRecipe() instanceof ShapedRecipe) {
					if (((ShapedRecipe) event.getRecipe()).getIngredientMap().values().contains(new ItemStack (Material.GOLD_INGOT))) {
						event.getInventory().setResult(new ItemStack(Material.AIR));
					}
				}
			}
		}
    }
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final float before = player.getSaturation();

		Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
			public void run() {
				float change = player.getSaturation() - before;
				player.setSaturation((float) (before + change * 2.5D));
			}
	    }, 1L);
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE && !Main.absorption) {
			player.removePotionEffect(PotionEffectType.ABSORPTION);
			
			Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
				public void run() {
					player.removePotionEffect(PotionEffectType.ABSORPTION);
				}
	        }, 1L);
		}
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equals("�6Golden Head")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25 * (Settings.getInstance().getConfig().getInt("feature.goldenheads.heal") * 2), 1));
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		
		if (player.getWorld().getName().equals("lobby")) {
			event.setCancelled(true);
			event.setFoodLevel(20);
			return;
		}
		
		if (event.getFoodLevel() < player.getFoodLevel()) {
			event.setCancelled(new Random().nextInt(100) < 66);
	    }
	}
	
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		Player player = event.getPlayer();
		
		if (Main.spectating.contains(player.getName())) {
			event.setCancelled(true);
			return;
		}
		
		if (!State.isState(State.INGAME)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (State.isState(State.SCATTER)) {
			event.setCancelled(true);
		}
	}
}