package com.leontg77.ultrahardcore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.EntityUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * The spectator class to manage spectating.
 * <p>
 * This class contains methods for enabling/disabling spec mode, 
 * toggling spec mode and managing vanishing, specinfo and commandspy.
 * As well it has the class for specinfo events.
 * 
 * @author LeonTG77
 */
public class Spectator {
	private static Spectator instance = new Spectator();
	
	private Set<String> spectators = new HashSet<String>();
	private Set<String> specinfo = new HashSet<String>();
	private Set<String> cmdspy = new HashSet<String>();
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Spectator getInstance() {
		return instance;
	}
	
	/**
	 * Get a list of all spectators.
	 * 
	 * @return List of spectators.
	 */
	public Set<String> getSpectators() {
		return spectators;
	}

	/**
	 * Get a list of all players with specinfo.
	 * 
	 * @return List of players with specinfo.
	 */
	public Set<String> getSpecInfoers() {
		return specinfo;
	}

	/**
	 * Get a list of all players with commandspy.
	 * 
	 * @return List of players with commandspy.
	 */
	public Set<String> getCommandSpyers() {
		return cmdspy;
	}
	
	/**
	 * Enable spectator mode for the given player.
	 * 
	 * @param player the player enabling for.
	 */
	public void enableSpecmode(Player player) {
		ItemStack compass = new ItemStack (Material.COMPASS);
		ItemMeta compassMeta = compass.getItemMeta();
		compassMeta.setDisplayName(ChatColor.GREEN + "Teleporter");
		compassMeta.setLore(Arrays.asList(ChatColor.GRAY + "Left click to teleport to a random player.", ChatColor.GRAY + "Right click to open a player teleporter."));
		compass.setItemMeta(compassMeta);
		
		ItemStack vision = new ItemStack (Material.INK_SACK, 1, (short) 12);
		ItemMeta visionMeta = vision.getItemMeta();
		visionMeta.setDisplayName(ChatColor.GREEN + "Toggle Night Vision");
		visionMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to toggle the night vision effect."));
		vision.setItemMeta(visionMeta);
		
		ItemStack nether = new ItemStack (Material.LAVA_BUCKET, 1);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName(ChatColor.GREEN + "Players in the nether");
		netherMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to get a list of players in the nether."));
		nether.setItemMeta(netherMeta);
		
		ItemStack tp = new ItemStack (Material.FEATHER);
		ItemMeta tpMeta = tp.getItemMeta();
		tpMeta.setDisplayName(ChatColor.GREEN + "Teleport to 0,0");
		tpMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to teleport to 0,0."));
		tp.setItemMeta(tpMeta);
		
		PlayerInventory inv = player.getInventory();
		
		// remove the spectator items just to make sure they won't drop.
		inv.removeItem(compass);
		inv.removeItem(vision);
		inv.removeItem(nether);
		inv.removeItem(tp);
		
		// loop thru the players inventory contents and drop any found to the ground.
		for (ItemStack content : inv.getContents()) {
			// the current loop value is null, hop over.
			if (content == null) {
				continue;
			}
			
			BlockUtils.dropItem(player.getLocation(), content);
		}

		// loop thru the players armor contents and drop any found to the ground.
		for (ItemStack armorContent : inv.getArmorContents()) {
			// armor contents never seem to be null so i'm checking the type for air as well.
			if (armorContent == null || armorContent.getType() == Material.AIR) {
				continue;
			}
			
			BlockUtils.dropItem(player.getLocation(), armorContent);
		}
		
		// check if the player has any experience, if so, drop it.
		if (player.getTotalExperience() > 0) {
			ExperienceOrb exp = player.getWorld().spawn(player.getLocation(), ExperienceOrb.class);
			exp.setExperience(player.getTotalExperience());
		}
		
		User user = User.get(player);
		
		// reset the players inventory, xp and effects
		user.resetInventory();
		user.resetExp();
		user.resetEffects();
		
		// set them in spectator mode and reset their fly/walk speed.
		player.setGameMode(GameMode.SPECTATOR);
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);

		TeamManager teams = TeamManager.getInstance();
		Game game = Game.getInstance();
		
		// if the game isn't an recorded round, join the spec team.
		if (!game.isRecordedRound()) {
			teams.joinTeam("spec", player);
		}

		// add them as a spectator and enable spec info.
		spectators.add(player.getName());
		specinfo.add(player.getName());
		
		// check for permission before enabling commandspy.
		if (player.hasPermission("uhc.cmdspy")) {
			cmdspy.add(player.getName());
		}
		
		// set the items at the correct spots.
		player.getInventory().setItem(1, tp);
		player.getInventory().setItem(3, compass);
		player.getInventory().setItem(5, nether);
		player.getInventory().setItem(7, vision);
		
		// loop all players, vanish the player and show him the other spectators and show the spectators him.
		for (Player online : PlayerUtils.getPlayers()) {
			if (isSpectating(online)) {
				online.showPlayer(player);
			} else {
				online.hidePlayer(player);
			}
			
			player.showPlayer(online);
		}
	}
	
	/**
	 * Disable spectator mode for the given player.
	 * 
	 * @param player the player disabling for.
	 * @param force force the disabling.
	 */
	public void disableSpecmode(Player player) {
		// set their gamemode back to survival and reset their walk/fly speed.
		player.setGameMode(GameMode.SURVIVAL);
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		
		TeamManager teams = TeamManager.getInstance();
		Game game = Game.getInstance();
		
		// if the game isn't an recorded round, leave their team.
		if (!game.isRecordedRound()) {
			teams.leaveTeam(player);
		}

		// remove them them as a spectator and disable spec info and commandspy.
		spectators.remove(player.getName());
		specinfo.remove(player.getName());
		cmdspy.remove(player.getName());
		
		User user = User.get(player);
		
		// reset the players inventory, xp and effects
		user.resetInventory();
		user.resetExp();
		user.resetEffects();
		
		// loop all players, hide the spectators for the player and unvanish him for everyone else.
		for (Player online : PlayerUtils.getPlayers()) {
			if (isSpectating(online)) {
				player.hidePlayer(online);
			} else {
				player.showPlayer(online);
			}
			
			online.showPlayer(player);
		}
	}
	
	/**
	 * Toggles the given player's spectator mode.
	 * 
	 * @param player the player toggling for.
	 */
	public void toggle(Player player) {
		if (isSpectating(player)) {
			enableSpecmode(player);
		} else {
			disableSpecmode(player);
		}
	}
	
	/**
	 * Check whether the given player is spectating or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player is speccing, <code>false</code> otherwise.
	 */
	public boolean isSpectating(Player player) {
		return isSpectating(player.getName());
	}
	
	/**
	 * Check whether the given string is in the spectator list.
	 * 
	 * @param entry the string cheking.
	 * @return <code>true</code> if the string is in the spectator list, <code>false</code> otherwise.
	 */
	public boolean isSpectating(String entry) {
		if (entry.equals("CONSOLE")) {
			return true;
		}
		
		return spectators.contains(entry);
	}

	/**
	 * Hides all the spectators for the given player.
	 * 
	 * @param player the player.
	 */
	public void hideSpectators(Player player) {
		// loop all players, hide the spectators for the player and unvanish everyone else.
		for (Player online : PlayerUtils.getPlayers()) {
			if (isSpectating(online)) {
				player.hidePlayer(online);
			} else {
				player.showPlayer(online);
			}
			
			online.showPlayer(player);
		}
	}
	
	/**
	 * Check whether the given player has specinfo or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player has specinfo, <code>false</code> otherwise.
	 */
	public boolean hasSpecInfo(Player player) {
		return specinfo.contains(player.getName());
	}
	
	/**
	 * Check whether the given player has cmdspy or not.
	 * 
	 * @param player the player cheking.
	 * @return <code>true</code> if the player has cmdspy, <code>false</code> otherwise.
	 */
	public boolean hasCommandSpy(Player player) {
		return cmdspy.contains(player.getName());
	}
	
	/**
	 * SpecInfo class for all the specinfo broadcasting.
	 * <p>
	 * Contains EventHandlers and Listeners for all info SpecInfo needs.
	 * 
	 * @author LeonTG77
	 */
	public static class SpecInfo implements Listener {
		// first string is the player name, then the ore type and the integer is the amount of that type.
		private static Map<String, Map<Material, Integer>> total = new HashMap<String, Map<Material, Integer>>();
		
		private Set<Location> locs = new HashSet<Location>();
		private Spectator spec = Spectator.getInstance();

		/**
		 * Get a map of the material of the ore and the amount the player has mined.
		 * 
		 * @param player The player that owns the map.
		 * @return The map.
		 */
		public static Map<Material, Integer> getTotal(Player player) {
			if (!total.containsKey(player.getName())) {
				total.put(player.getName(), new HashMap<Material, Integer>());
			}
			
			for (Material type : Material.values()) {
				if (total.get(player.getName()).containsKey(type)) {
					continue;
				}
				
				if (!isOre(type)) {
					continue;
				}
				
				total.get(player.getName()).put(type, 0);
			}
			
			return total.get(player.getName());
		}

		/**
		 * Check if the given material is an ore.
		 * 
		 * @param ore The material checking.
		 * @return True if its an ore, false otherwise.
		 */
		private static boolean isOre(Material ore) {
			switch (ore) {
			case REDSTONE_ORE:
			case COAL_ORE:
			case DIAMOND_ORE:
			case EMERALD_ORE:
			case GOLD_ORE:
			case IRON_ORE:
			case LAPIS_ORE:
			case QUARTZ_ORE:
				return true;
			default:
				return false;
			}
		}
		
		/**
		 * Broadcast the given message to all people with specinfo.
		 * 
		 * @param message The message broadcasted.
		 */
		private void broadcast(String message) {
			for (Player online : PlayerUtils.getPlayers()) {
				if (!spec.hasSpecInfo(online)) {
					continue;
				}
				
				online.sendMessage("§8[§9S§8] §f" + message);
			}
		}

		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		public void onBlockBreak(BlockBreakEvent event) {
			Player player = event.getPlayer();
			Block block = event.getBlock();

			Location loc = block.getLocation();
			Material type = block.getType();
			
			if (!isOre(type)) {
				return;
			}
			
			if (locs.contains(loc)) {
				locs.remove(loc);
				return;
			}
			
			int amount = 0;
			
			for (int x = loc.getBlockX() - 2; x <= loc.getBlockX() + 2; x++) {
				for (int y = loc.getBlockY() - 2; y <= loc.getBlockY() + 2; y++) {
					for (int z = loc.getBlockZ() - 2; z <= loc.getBlockZ() + 2; z++) {
						Block locBlock = loc.getWorld().getBlockAt(x, y, z);
						
						if (locBlock.getType().equals(type)) {
							locs.add(loc.getWorld().getBlockAt(x, y, z).getLocation());
							amount++;
						}
					}
				}
			}
			
			Map<Material, Integer> total = getTotal(player);
			total.put(type, total.get(type) + amount);
			
			if (block.getType() == Material.GOLD_ORE) {
				broadcast("§7" + player.getName() + "§f:§6GOLD §f[V:§6" + amount + "§f] [T:§6" + total.get(type) + "§f]");
			} else if (block.getType() == Material.DIAMOND_ORE) {
				broadcast("§7" + player.getName() + "§f:§3DIAMOND §f[V:§3" + amount + "§f] [T:§3" + total.get(type) + "§f]");
			}
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void onPlayerTeleport(PlayerTeleportEvent event) {
			if (event.getCause() != TeleportCause.ENDER_PEARL) {
				return;
			}
			
			Player player = event.getPlayer();
			broadcast("§5Pearl: §a" + player.getName() + " §f<-> D:§d" + NumberUtils.convertDouble(event.getFrom().distance(event.getTo())) + "m.");
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void onPlayerPortal(PlayerPortalEvent event) {
			if (event.getTo() == null) {
			    return;
			}
			
			Player player = event.getPlayer();
			
			World from = event.getFrom().getWorld();
			World to = event.getTo().getWorld();

			String fromEnv = from.getEnvironment().name().toLowerCase();
			String toEnv = to.getEnvironment().name().toLowerCase();

			broadcast("§dPortal:§6" + player.getName() + "§f from §a" + fromEnv.replaceAll("normal", "overworld") + "§f to §c" + toEnv.replaceAll("normal", "overworld"));
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
			Player player = event.getPlayer();
			ItemStack item = event.getItem();
			
			if (event.getItem().getType() == Material.GOLDEN_APPLE) {
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
					broadcast("§aHeal: §6" + player.getName() + "§f<->§5Golden Head");
					return;
				}
				
				broadcast("§aHeal: §6" + player.getName() + "§f<->§6Golden Apple");
				return;
			}
			
			if (event.getItem().getType() == Material.POTION) {
				Potion pot;

				if (item.getDurability() == 8261) {
					pot = new Potion(PotionType.INSTANT_HEAL, 1);
				} else if (item.getDurability() == 16453) {
					pot = new Potion(PotionType.INSTANT_HEAL, 1);
				} else {
					try {
						pot = Potion.fromItemStack(item);
					} catch (Exception e) {
						return;
					}
				}
				
				for (PotionEffect effect : pot.getEffects()) {
					String potName = NameUtils.getPotionName(effect.getType());
					int duration = effect.getDuration() / 20;
					
					if ((effect.getDuration() / 20) > 0) {
						broadcast("§5Potion: §a" + player.getName() + "§f <-> P:§d" + potName + " §fT:§d" + pot.getLevel() + " §fD:§d" + DateUtils.ticksToString(duration) + " §fV:§dNormal");
						continue;
					}
					
					broadcast("§5Potion: §a" + player.getName() + "§f <-> P:§d" + potName + " §fT:§d" + pot.getLevel() + " §fV:§dNormal");
				}
			}
		}
		
		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void onPotionSplash(PotionSplashEvent event) {
			if (!(event.getPotion().getShooter() instanceof Player)) {
				return;
			}
			
			Player player = (Player) event.getPotion().getShooter();
			ItemStack item = event.getPotion().getItem();
			
			Potion pot;

			if (item.getDurability() == 16453) {
				pot = new Potion(PotionType.INSTANT_HEAL, 1);
			} else if (item.getDurability() == 16421) {
				pot = new Potion(PotionType.INSTANT_HEAL, 2);
			} else {
				try {
					pot = Potion.fromItemStack(item);
				} catch (Exception e) {
					return;
				}
			}
			
			for (PotionEffect effect : pot.getEffects()) {
				String potName = NameUtils.getPotionName(effect.getType());
				int duration = effect.getDuration() / 20;
				
				if ((effect.getDuration() / 20) > 0) {
					broadcast("§5Potion: §a" + player.getName() + "§f <-> P:§d" + potName + " §fT:§d" + pot.getLevel() + " §fD:§d" + DateUtils.ticksToString(duration) + " §fV:§dSplash");
					continue;
				}
				
				broadcast("§5Potion: §a" + player.getName() + "§f <-> P:§d" + potName + " §fT:§d" + pot.getLevel() + " §fV:§dSplash");
			}
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void onCraftItem(CraftItemEvent event) {
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getRecipe().getResult();
			
			if (item.getType() == Material.GOLDEN_APPLE) {
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
					broadcast("§2Craft§f: §a" + player.getName() + "§f<->§5Golden Head");
					return;
				}
				
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§6Golden Apple");
				return;
			}
			
			if (item.getType() == Material.DIAMOND_HELMET) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§bDia. Helmet");
				return;
			}
			
			if (item.getType() == Material.DIAMOND_CHESTPLATE) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§bDia. Chestplate");
				return;
			}
			
			if (item.getType() == Material.DIAMOND_LEGGINGS) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§bDia. Leggings");
				return;
			}
			
			if (item.getType() == Material.DIAMOND_BOOTS) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§bDia. Boots");
				return;
			}
			
			if (item.getType() == Material.DIAMOND_SWORD) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§bDia. Sword");
				return;
			}
			
			if (item.getType() == Material.BOW) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§dBow");
				return;
			}
			
			if (item.getType() == Material.ANVIL) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§dAnvil");
				return;
			}
			
			if (item.getType() == Material.ENCHANTMENT_TABLE) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§dEnchant. Table");
				return;
			}
			
			if (item.getType() == Material.BREWING_STAND_ITEM) {
				broadcast("§2Craft§f: §a" + player.getName() + "§f<->§dBrewing Stand");
			}
		}
		
		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void onDamage(final EntityDamageEvent event) {
			if (!(event.getEntity() instanceof Player)) {
				return;
			}

			final Player player = (Player) event.getEntity();
			
			if (event instanceof EntityDamageByEntityEvent) {
				onEntityDamageByEntity(player, (EntityDamageByEntityEvent) event);
				return;
			}
			
			final DamageCause cause = event.getCause();
			final double olddamage = player.getHealth();

			new BukkitRunnable() {
				public void run() {
					String name = NameUtils.capitalizeString(cause.name().replace("_TICK", ""), true);
					double damage = olddamage - player.getHealth();
					
					if (damage <= 0) {
						return;
					}
					
					String health = NumberUtils.convertDouble((player.getHealth() / 2));
					String taken = NumberUtils.convertDouble((damage / 2));
					
					broadcast("§5PvE§f:§c" + player.getName() + "§f<-§d" + name + " §f[§c" + health + "§f] [§6" + taken + "§f]");
				}
			}.runTaskLater(Main.plugin, 1);
		}

		private void onEntityDamageByEntity(final Player player, final EntityDamageByEntityEvent event) {
			final double olddamage = player.getHealth();
			
			new BukkitRunnable() {
				public void run() {
					double damage = olddamage - player.getHealth();
					Entity damager = event.getDamager();

					String pHealth = NumberUtils.convertDouble((player.getHealth() / 2));
					String taken = NumberUtils.convertDouble((damage / 2));
					
					if (damager instanceof Player) {
						Player killer = (Player) damager;
						
						if (spec.isSpectating(killer)) {
							return;
						}
						
						String kHealth = NumberUtils.convertDouble((killer.getHealth() / 2));
						
						broadcast("§4PvP§f:§a" + killer.getName() + "§f-M>§c" + player.getName() + " §f[§a" + kHealth + "§f:§c" + pHealth + "§f] [§6" + taken + "§f]");
						return;
					}
					
					if (damager instanceof Projectile) {
						Projectile proj = (Projectile) damager;
						ProjectileSource source = proj.getShooter();
						
						if (source instanceof Player) {
							Player shooter = (Player) source;

							String kHealth = NumberUtils.convertDouble((shooter.getHealth() / 2));
							
							if (proj instanceof Arrow) {
								broadcast("§4PvP§f:§a" + shooter.getName() + "§f-B>§c" + player.getName() + " §f[§a" + kHealth + "§f:§c" + pHealth + "§f] [§6" + taken + "§f]");
							} else if (proj instanceof Snowball) {
								broadcast("§4PvP§f:§a" + shooter.getName() + "§f-S>§c" + player.getName() + " §f[§a" + kHealth + "§f:§c" + pHealth + "§f] [§6" + taken + "§f]");
							} else if (proj instanceof Egg) {
								broadcast("§4PvP§f:§a" + shooter.getName() + "§f-E>§c" + player.getName() + " §f[§a" + kHealth + "§f:§c" + pHealth + "§f] [§6" + taken + "§f]");
							} else if (!(proj instanceof FishHook)) {
								broadcast("§4PvP§f:§a" + shooter.getName() + "§f-?P>§c" + player.getName() + " §f[§a" + kHealth + "§f:§c" + pHealth + "§f] [§6" + taken + "§f]");
							}
							return;
						} 
							
						if (proj.getShooter() instanceof LivingEntity) {
							LivingEntity entity = (LivingEntity) proj.getShooter();
							broadcast("§5PvE§f:§c" + player.getName() + "§f<-§d" + EntityUtils.getMobName(entity.getType()) + " §f[§c" + pHealth + "§f] [§6" + taken + "§f]");
							return;
						}
						
						broadcast("§5PvE§f:§c" + player.getName() + "§f<-§d?P §f[§c" + pHealth + "§f] [§6" + taken + "§f]");
						return;
					} 

					Entity entity = event.getDamager();
					broadcast("§5PvE§f:§c" + player.getName() + "§f<-§d" + EntityUtils.getMobName(entity.getType()) + " §f[§c" + pHealth + "§f] [§6" + taken + "§f]");
				}
			}.runTaskLater(Main.plugin, 1);
		}
	}
}