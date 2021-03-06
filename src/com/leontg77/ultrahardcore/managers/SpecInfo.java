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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PotionSplashEvent;
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
			
			online.sendMessage("�8[�bS�8] �7" + message);
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
			return;
		}
		
		int amount = 0;
		
		for (int x = loc.getBlockX() - 2; x <= loc.getBlockX() + 2; x++) {
			for (int y = loc.getBlockY() - 2; y <= loc.getBlockY() + 2; y++) {
				for (int z = loc.getBlockZ() - 2; z <= loc.getBlockZ() + 2; z++) {
					Block locBlock = loc.getWorld().getBlockAt(x, y, z);
					
					if (locBlock.getType().equals(type)) {
						locs.add(locBlock.getLocation());
						amount++;
					}
				}
			}
		}
		
		Map<Material, Integer> total = getTotal(player);
		total.put(type, total.get(type) + amount);
		
		if (block.getType() == Material.GOLD_ORE) {
			broadcast(player.getName() + " �8� �6Gold �8[�7V: �6" + amount + "�8] [�7T: �6" + total.get(type) + "�8]");
		} else if (block.getType() == Material.DIAMOND_ORE) {
			broadcast(player.getName() + " �8� �bDiamonds �8[�7V: �b" + amount + "�8] [�7T: �b" + total.get(type) + "�8]");
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerTeleportEvent event) {
		if (event.getCause() != TeleportCause.ENDER_PEARL) {
			return;
		}
		
		Player player = event.getPlayer();
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		broadcast("�8(�5Pearl�8) �7" + player.getName() + " �8� �d" + NumberUtils.formatDouble(from.distance(to)) + " blocks");
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerPortalEvent event) {
		if (event.getTo() == null) {
		    return;
		}
		
		Player player = event.getPlayer();
		
		World from = event.getFrom().getWorld();
		World to = event.getTo().getWorld();

		String fromEnv;
		String toEnv;
		
		switch (from.getEnvironment()) {
		case NETHER:
			fromEnv = "�cnether";
			break;
		case NORMAL:
			fromEnv = "�aoverworld";
			break;
		case THE_END:
			fromEnv = "�bthe end";
			break;
		default:
			fromEnv = "�e" + from.getEnvironment().name().toLowerCase();
			break;
		}
		
		switch (to.getEnvironment()) {
		case NETHER:
			toEnv = "�cnether";
			break;
		case NORMAL:
			toEnv = "�aoverworld";
			break;
		case THE_END:
			toEnv = "�bthe end";
			break;
		default:
			toEnv = "�e" + to.getEnvironment().name().toLowerCase();
			break;
		}
		
		broadcast("�8(�dPortal�8) �7" + player.getName() + "�8 � " + fromEnv + " �7-� " + toEnv);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void on(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		switch (item.getType()) {
		case GOLDEN_APPLE:
			if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("�6Golden Head")) {
				broadcast("�8(�aHeal�8) �7" + player.getName() + "�8 � �5Golden Head");
				return;
			}
			
			if (item.getDurability() == 1) {
				broadcast("�8(�aHeal�8) �7" + player.getName() + "�8 � �dNotch Apple");
				return;
			}
			
			broadcast("�8(�aHeal�8) �7" + player.getName() + "�8 � �6Golden Apple");
			return;
		case POTION:
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
				
				if (duration > 0) {
					broadcast("�8(�5Potion�8) �7" + player.getName() + "�8 � �d" + potName + " �8[�7T: �a" + pot.getLevel() + "�8] [�7D: �a" + DateUtils.ticksToString(duration) + "�8] �7�oDrink");
					continue;
				}
				
				broadcast("�8(�5Potion�8) �7" + player.getName() + "�8 � �d" + potName + " �8[�7T: �a" + pot.getLevel() + "�8] �7�oDrink");
			}
			return;
		default:
			return;
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

		if (item.getDurability() == 16453) { // for some reason health potions doesn't work with Potion#fromItemStack(ItemStack)
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
			
			if (duration > 0) {
				broadcast("�8(�5Potion�8) �7" + player.getName() + "�8 � �d" + potName + " �8[�7T: �a" + pot.getLevel() + "�8] [�7D: �a" + DateUtils.ticksToString(duration) + "�8] �7�oSplash");
				continue;
			}
			
			broadcast("�8(�5Potion�8) �7" + player.getName() + "�8 � �d" + potName + " �8[�7T: �a" + pot.getLevel() + "�8] �7�oSplash");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(CraftItemEvent event) {
		ItemStack item = event.getInventory().getResult();
		Player player = (Player) event.getWhoClicked();
		
		switch (item.getType()) {
		case GOLDEN_APPLE:
			if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("�6Golden Head")) {
				broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �5Golden Head");
				return;
			}
			
			if (item.getDurability() == 1) {
				broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �dNotch Apple");
				return;
			}
			
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �6Golden Apple");
			return;
		case DIAMOND_HELMET:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �bDia. Helmet");
			return;
		case DIAMOND_CHESTPLATE:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �bDia. Chestplate");
			return;
		case DIAMOND_LEGGINGS:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �bDia. Leggings");
			return;
		case DIAMOND_BOOTS:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �bDia. Boots");
			return;
		case DIAMOND_SWORD:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �bDia. Sword");
			return;
		case BOW:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �aBow");
			return;
		case ANVIL:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �aAnvil");
			return;
		case ENCHANTMENT_TABLE:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �dEnchant. Table");
			return;
		case BREWING_STAND_ITEM:
			broadcast("�8(�2Craft�8) �7" + player.getName() + "�8 � �aBrewing Stand");
			return;
		default:
			return;
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
		final double oldHealth = player.getHealth();
		
		final String name;
		
		switch (cause) {
		case BLOCK_EXPLOSION:
			name = "Explosion";
			break;
		case CONTACT:
			name = "Cactus";
			break;
		case FALLING_BLOCK:
			name = "Anvil";
			break;
		case FIRE_TICK:
			name = "Fire";
			break;
		case MAGIC:
			name = "Potion";
			break;
		default:
			name = NameUtils.capitalizeString(cause.name(), true);
			break;
		}

		new BukkitRunnable() {
			public void run() {
				double damage = oldHealth - player.getHealth();
				
				if (damage <= 0) {
					return;
				}
				
				String health = NumberUtils.makePercent(player.getHealth()).substring(2) + "%";
				String taken = NumberUtils.makePercent(damage).substring(2) + "%";
				
				broadcast("�8(�5PvE�8) �7" + player.getName() + " �a" + health + "�8 -� �d" + name + " �8[�7D: �6" + taken + "�8]");
			}
		}.runTaskLater(plugin, 1);
	}

	private void on(final Player player, final EntityDamageByEntityEvent event) {
		final double oldHealth = player.getHealth();
		
		new BukkitRunnable() {
			public void run() {
				double damage = oldHealth - player.getHealth();
				Entity damager = event.getDamager();

				String pHealth = NumberUtils.makePercent(player.getHealth()).substring(2) + "%";
				String taken = NumberUtils.makePercent(damage).substring(2) + "%";
				
				if (damager instanceof Player) {
					Player killer = (Player) damager;
					
					if (spec.isSpectating(killer)) {
						return;
					}
					
					String kHealth = NumberUtils.makePercent(killer.getHealth()).substring(2) + "%";
					
					broadcast("�8(�cPvP�8) �7" + killer.getName() + " �a" + kHealth + "�8 -� �7" + player.getName() + " �a" + pHealth + " �8[�6" + taken + "�8] �7�oMelee");
					return;
				}
				
				if (damager instanceof Projectile) {
					Projectile proj = (Projectile) damager;
					ProjectileSource source = proj.getShooter();
					
					if (source instanceof Player) {
						Player shooter = (Player) source;

						String kHealth = NumberUtils.makePercent(shooter.getHealth()).substring(2) + "%";
						
						if (proj instanceof Arrow) {
							broadcast("�8(�cPvP�8) �7" + shooter.getName() + " �a" + kHealth + "�8 -� �7" + player.getName() + " �a" + pHealth + " �8[�7D: �6" + taken + "�8] �7�oArrow");
						} else if (proj instanceof Snowball) {
							broadcast("�8(�cPvP�8) �7" + shooter.getName() + " �a" + kHealth + "�8 -� �7" + player.getName() + " �a" + pHealth + " �8[�7D: �6" + taken + "�8] �7�oSnowball");
						} else if (proj instanceof Egg) {
							broadcast("�8(�cPvP�8) �7" + shooter.getName() + " �a" + kHealth + "�8 -� �7" + player.getName() + " �a" + pHealth + " �8[�7D: �6" + taken + "�8] �7�oEgg");
						} else if (!(proj instanceof FishHook)) {
							broadcast("�8(�cPvP�8) �7" + shooter.getName() + " �a" + kHealth + "�8 -� �7" + player.getName() + " �a" + pHealth + " �8[�7D: �6" + taken + "�8] �7�o???");
						}
						return;
					} 
						
					if (proj.getShooter() instanceof LivingEntity) {
						LivingEntity entity = (LivingEntity) proj.getShooter();
						broadcast("�8(�5PvE�8) �7" + player.getName() + " �a" + pHealth + "�8 -� �d" + EntityUtils.getMobName(entity.getType()) + " �8[�7D: �6" + taken + "�8]");
						return;
					}
					
					broadcast("�8(�5PvE�8) �7" + player.getName() + " �a" + pHealth + "�8 -� �d??? �8[�7D: �6" + taken + "�8]");
					return;
				} 

				Entity entity = event.getDamager();
				broadcast("�8(�5PvE�8) �7" + player.getName() + " �a" + pHealth + "�8 -� �d" + EntityUtils.getMobName(entity.getType()) + " �8[�7D: �6" + taken + "�8]");
			}
		}.runTaskLater(plugin, 1);
	}
}