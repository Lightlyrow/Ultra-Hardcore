package com.leontg77.ultrahardcore.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.utils.LocationUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Scatter manager class.
 * 
 * @author LeonTG77
 */
public class ScatterManager {
	private static final ScatterManager INSTANCE = new ScatterManager();
	private static final int EFFECT_TICKS = NumberUtils.TICKS_IN_999_DAYS;
	
	/**
	 * List of all freeze effects.
	 */
	public static final Set<PotionEffect> FREEZE_EFFECTS = ImmutableSet.of(
			new PotionEffect(PotionEffectType.JUMP, EFFECT_TICKS, 128), 
			new PotionEffect(PotionEffectType.BLINDNESS, EFFECT_TICKS, 6),
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, EFFECT_TICKS, 6),
			new PotionEffect(PotionEffectType.SLOW_DIGGING, EFFECT_TICKS, 10),
			new PotionEffect(PotionEffectType.SLOW, EFFECT_TICKS, 6),
			new PotionEffect(PotionEffectType.INVISIBILITY, EFFECT_TICKS, 2)
	);
	
	/**
	 * List of blocks not to spawn on.
	 */
	private static final Set<Material> INVAILD_SPAWN_BLOCKS = ImmutableSet.of(
			Material.STATIONARY_WATER, 
			Material.WATER, 
			Material.STATIONARY_LAVA,
			Material.LAVA, 
			Material.CACTUS
	);

	private final Map<String, Location> lateScatters = new HashMap<String, Location>();
	
	private boolean scattering = true;
	private boolean scatterTeams;
	
	private World world;
	private int radius;
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static ScatterManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Check if the scatter is currently running.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isScattering() {
		return scattering;
	}
	
	/**
	 * Enable or disable team spead.
	 * <p>
	 * Team spead means wether to spread teams together or have everyone at seperate places.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setTeamScatter(final boolean enable) {
		this.scatterTeams = enable;
	}
	
	/**
	 * Set the world to use for the scatter.
	 * <p>
	 * Once the world is set it will calculate the radius.
	 * 
	 * @param world The world to use.
	 */
	public void setWorld(final World world) {
		this.world = world;
		
		this.radius = ((((int) world.getWorldBorder().getSize()) / 2) - 1);
	}

	/**
	 * Check if the given player needs a late scatter.
	 * 
	 * @param player The player to check.
	 * @return True if he needs it, false otherwise.
	 */
	public boolean needsLateScatter(Player player) {
		return lateScatters.containsKey(player.getName());
	}

	/**
	 * Handle a late scatter.
	 * 
	 * @param toScatter The player to handle.
	 */
	public void handleLateScatter(Player toScatter) {
		if (State.isState(State.SCATTER)) {
			for (PotionEffect effect : FREEZE_EFFECTS) {
				if (toScatter.hasPotionEffect(effect.getType())) {
					toScatter.removePotionEffect(effect.getType());
				}
				
				toScatter.addPotionEffect(effect);
			}
		}
			
		toScatter.teleport(lateScatters.get(toScatter.getName()));
		lateScatters.remove(toScatter.getName());
	}
	
	/**
	 * Scatter the given list of strings.
	 * <p>
	 * If team spread is on the strings will be team names, otherwise it will be player names.
	 * 
	 * @param toScatter The players to scatter.
	 * @throws CommandException If theres no players to scatter.
	 */
	public void scatter(final List<String> toScatter) throws CommandException {
		if (toScatter.isEmpty()) {
			throw new CommandException("There are no players to scatter.");
		}
		
		final Map<String, Location> scatterLocs = new HashMap<String, Location>();
		final TeamManager manager = TeamManager.getInstance();
		
		scattering = true;
		
		new BukkitRunnable() {
			public void run() {
				if (toScatter.size() > 1) {
					PlayerUtils.broadcast(Main.PREFIX + "Finding scatter locations...");
					
					for (Player online : Bukkit.getOnlinePlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
					}
				}
				
				List<Location> loc = findScatterLocations(world, radius, toScatter.size());
				int index = 0;

				for (String teamOrPlayer : toScatter) {
					scatterLocs.put(teamOrPlayer, loc.get(index));
					index++;
				}
			}
		}.runTaskLater(Main.plugin, 30);

		new BukkitRunnable() {
			public void run() {
				if (toScatter.size() > 1) {
					PlayerUtils.broadcast(Main.PREFIX + "Locations found, loading chunks...");

					for (Player online : Bukkit.getOnlinePlayers()) {
						online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
					}
				}
				
				final List<Location> locs = new ArrayList<Location>(scatterLocs.values());
				final List<String> names = new ArrayList<String>(scatterLocs.keySet());
				
				new BukkitRunnable() {
					int i = 0;
					
					public void run() {
						if (i < locs.size()) {
							Player host = Bukkit.getPlayer(Game.getInstance().getHost());
							
							if (State.isState(State.INGAME) || host == null) {
								locs.get(i).getChunk().load(true);
							} else {
								host.teleport(locs.get(i));
							}
							
							i++;

							if (toScatter.size() > 1) {
								for (Player online : Bukkit.getOnlinePlayers()) {
									PacketUtils.sendAction(online, "§7Loading scatter locations... §8[§a" + i + "§7/§a" + locs.size() + "§8]");
								}
							}
						} else {
							if (toScatter.size() > 1) {
								PlayerUtils.broadcast(Main.PREFIX + "All chunks loaded, starting scatter...");

								for (Player online : Bukkit.getOnlinePlayers()) {
									online.playSound(online.getLocation(), Sound.NOTE_BASS, 1, 1);
								}
							}
							
							locs.clear();
							cancel();
							
							new BukkitRunnable() {
								int i = 0;
								
								public void run() {
									if (i < names.size()) {
										String scatter = names.get(i);
										
										if (scatter == null) {
											PlayerUtils.broadcast("§cAn error occured while scattering a player.", "uhc.staff");
											i++;
											return;
										}
										
										if (scatterTeams) {
											Team team = manager.getTeam(scatter);
											
											for (OfflinePlayer teammate : manager.getPlayers(team)) {
												Player toScatter = teammate.getPlayer();
												
												if (toScatter == null) {
													lateScatters.put(teammate.getName(), scatterLocs.get(scatter));
												} else {
													if (State.isState(State.SCATTER)) {
														for (PotionEffect effect : FREEZE_EFFECTS) {
															if (toScatter.hasPotionEffect(effect.getType())) {
																toScatter.removePotionEffect(effect.getType());
															}
															
															toScatter.addPotionEffect(effect);
														}
													}
													
													toScatter.teleport(scatterLocs.get(scatter));
												}
											}
											
											scatterLocs.remove(scatter);
										} else {
											Player toScatter = Bukkit.getPlayer(scatter);
											
											if (toScatter == null) {
												lateScatters.put(scatter, scatterLocs.get(scatter));
												scatterLocs.remove(scatter);
											} else {
												if (State.isState(State.SCATTER)) {
													for (PotionEffect effect : FREEZE_EFFECTS) {
														if (toScatter.hasPotionEffect(effect.getType())) {
															toScatter.removePotionEffect(effect.getType());
														}
														
														toScatter.addPotionEffect(effect);
													}
												}
												
												toScatter.teleport(scatterLocs.get(scatter));
												scatterLocs.remove(scatter);
											}
										}
										
										i++;

										if (toScatter.size() > 1) {
											for (Player online : Bukkit.getOnlinePlayers()) {
												PacketUtils.sendAction(online, "§7Scattered " + scatter + " §8[§a" + i + "§7/§a" + names.size() + "§8]");
											}
										}
									} else {
										if (toScatter.size() > 1) {
											PlayerUtils.broadcast(Main.PREFIX + "The scatter has finished.");
											
											for (Player online : Bukkit.getOnlinePlayers()) {
												online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
											}
										}
										
										scattering = false;
										
										names.clear();
										cancel();
									}
								}
							}.runTaskTimer(Main.plugin, 40, 3);
						}
					}
				}.runTaskTimer(Main.plugin, 5, 5);
			}
		}.runTaskLater(Main.plugin, 60);
	}
	
	/**
	 * Get a list of available scatter locations.
	 * 
	 * @param world the world to scatter in.
	 * @param radius the maximum radius to scatter.
	 * @param count the amount of scatter locations needed.
	 * 
	 * @return A list of vaild scatter locations.
	 */
	public List<Location> findScatterLocations(World world, int radius, int count) {
		final List<Location> locs = new ArrayList<Location>();
		
		for (int i = 0; i < count; i++) {
			double min = 150;
			
			for (int j = 0; j < 4004; j++) {
				if (j == 4003) {
					PlayerUtils.broadcast(ChatColor.RED + "Could not scatter a player", "uhc.admin");
					break;
				}
				
				final Random rand = new Random();
				
				int x = rand.nextInt(radius * 2) - radius;
				int z = rand.nextInt(radius * 2) - radius;

				Location loc = new Location(world, x + 0.5, 0, z + 0.5);

				boolean close = false;
				for (Location l : locs) {
					if (l.distanceSquared(loc) < min) {
						close = true;
					}
				}
				
				if (!close && isVaild(loc.clone())) {
					double y = LocationUtils.highestTeleportableYAtLocation(loc);
					loc.setY(y + 2);
					locs.add(loc);
					break;
				} else {
					min -= 1;
				}
			}
		}
		
		return locs;
	}

	/**
	 * Check if the given location is a vaild scatter location.
	 * 
	 * @param loc the location.
	 * @return True if its vaild, false otherwise.
	 */
	private boolean isVaild(Location loc) {
		loc.setY(loc.getWorld().getHighestBlockYAt(loc));
		
		Material type = loc.add(0, -1, 0).getBlock().getType();
		boolean vaild = true;
		
		if (loc.getBlockY() < 60) {
			vaild = false;
		}	
		
		for (Material no : INVAILD_SPAWN_BLOCKS) {
			if (type == no) {
				vaild = false;
			}
		}
		
		return vaild;
	}
}