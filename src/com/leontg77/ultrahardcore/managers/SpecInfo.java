package com.leontg77.ultrahardcore.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.EntityUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * SpecInfo class for all the specinfo broadcasting.
 * <p>
 * Contains EventHandlers and Listeners for all info SpecInfo needs.
 * 
 * @author LeonTG77
 */
public class SpecInfo implements Listener {
	private final SpecManager spec;
	private final Main plugin;
	
	/**
	 * SpecInfo class constructor.
	 * 
	 * @param plugin The main class.
	 * @param spec The spectator manager class.
	 */
	public SpecInfo(Main plugin, SpecManager spec) {
		this.plugin = plugin;
		this.spec = spec;
	}
	
	// first string is the player name, then the ore type and the integer is the amount of that type.
	private final Map<String, Map<Material, Integer>> total = new HashMap<String, Map<Material, Integer>>();
	
	private final Set<Location> locs = new HashSet<Location>();

	/**
	 * Get a map of the material of the ore and the amount the player has mined.
	 * 
	 * @param player The player that owns the map.
	 * @return The map.
	 */
	public Map<Material, Integer> getTotal(Player player) {
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
	private boolean isOre(Material ore) {
		return ore == Material.DIAMOND_ORE || ore == Material.GOLD_ORE || ore == Material.IRON_ORE;
	}
	
	/**
	 * Broadcast the given message to all people with specinfo.
	 * 
	 * @param message The message broadcasted.
	 */
	private void broadcast(String message) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (!spec.hasSpecInfo(online)) {
				continue;
			}
			
			online.sendMessage("§8[§9S§8] §7" + message);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void on(BlockBreakEvent event) {
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
			broadcast("§7" + player.getName() + "§8:§6GOLD §8[V:§6" + amount + "§8] [T:§6" + total.get(type) + "§8]");
		} else if (block.getType() == Material.DIAMOND_ORE) {
			broadcast("§7" + player.getName() + "§8:§3DIAMOND §8[V:§3" + amount + "§8] [T:§3" + total.get(type) + "§8]");
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerTeleportEvent event) {
		if (event.getCause() != TeleportCause.ENDER_PEARL) {
			return;
		}
		
		Player player = event.getPlayer();
		broadcast("§5Pearl: §a" + player.getName() + " §f<-> D:§d" + NumberUtils.formatDouble(event.getFrom().distance(event.getTo())) + "m.");
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerPortalEvent event) {
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
	public void on(PlayerItemConsumeEvent event) {
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
	public void on(PotionSplashEvent event) {
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
	public void on(CraftItemEvent event) {
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
	public void on(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity();
		
		if (event instanceof EntityDamageByEntityEvent) {
			on(player, (EntityDamageByEntityEvent) event);
			return;
		}
		
		final DamageCause cause = event.getCause();
		final double olddamage = player.getHealth();

		new BukkitRunnable() {
			public void run() {
				String name = NameUtils.capitalizeString(cause.name().replace("_TICK", "").replace("BLOCK_E", "E"), true);
				double damage = olddamage - player.getHealth();
				
				if (damage <= 0) {
					return;
				}
				
				String health = NumberUtils.makePercent(player.getHealth()).substring(2) + "%";
				String taken = NumberUtils.makePercent(damage).substring(2) + "%";
				
				broadcast("§5PvE§f:§c" + player.getName() + "§f<-§d" + name + " §f[§c" + health + "§f] [§6" + taken + "§f]");
			}
		}.runTaskLater(plugin, 1);
	}

	private void on(final Player player, final EntityDamageByEntityEvent event) {
		final double olddamage = player.getHealth();
		
		new BukkitRunnable() {
			public void run() {
				double damage = olddamage - player.getHealth();
				Entity damager = event.getDamager();

				String pHealth = NumberUtils.makePercent(player.getHealth()).substring(2) + "%";
				String taken = NumberUtils.makePercent(damage).substring(2) + "%";
				
				if (damager instanceof Player) {
					Player killer = (Player) damager;
					
					if (spec.isSpectating(killer)) {
						return;
					}
					
					String kHealth = NumberUtils.makePercent(killer.getHealth()).substring(2) + "%";
					
					broadcast("§4PvP§f:§a" + killer.getName() + "§f-M>§c" + player.getName() + " §f[§a" + kHealth + "§f:§c" + pHealth + "§f] [§6" + taken + "§f]");
					return;
				}
				
				if (damager instanceof Projectile) {
					Projectile proj = (Projectile) damager;
					ProjectileSource source = proj.getShooter();
					
					if (source instanceof Player) {
						Player shooter = (Player) source;

						String kHealth = NumberUtils.makePercent(shooter.getHealth()).substring(2) + "%";
						
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
		}.runTaskLater(plugin, 1);
	}
}