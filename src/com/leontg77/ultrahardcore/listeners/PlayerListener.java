package com.leontg77.ultrahardcore.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.VengefulSpirits;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.RecipeUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class PlayerListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMotd("§4§lArctic UHC §8» §6" + GameUtils.getMOTDMessage() + " §8« [§71.8§8] [§7EU§8]\n§8» §7§oFollow us on twitter, §a§o@ArcticUHC§7§o!");
		event.setMaxPlayers(game.getMaxPlayers());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location to = event.getTo();
		
		if (to.getWorld().getName().equals("lobby") && to.getY() <= 20) {
			Parkour parkour = Parkour.getInstance();
			
			if (parkour.isParkouring(player)) {
				if (parkour.getCheckpoint(player) != null) {
					int checkpoint = parkour.getCheckpoint(player);
					player.teleport(parkour.getLocation(checkpoint));
					return;
				}
				
				player.teleport(parkour.getLocation(0));
				return;
			}
			
			player.teleport(Main.getSpawn());
		}
	}
	
	@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        Spectator spec = Spectator.getInstance();
        InvGUI inv = InvGUI.getInstance();
        
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			inv.openSelector(player);
			return;
		} 
		
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			List<Player> list = GameUtils.getGamePlayers();
			
			if (list.isEmpty()) {
				player.sendMessage(Main.PREFIX + "Couldn't find any players.");
				return;
			}
			
			Random rand = new Random();
			Player target = list.get(rand.nextInt(list.size()));
			
			player.sendMessage(Main.PREFIX + "Teleported to §a" + target.getName() + "§7.");
			player.teleport(target.getLocation());
		}
	}
	
	@EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity clicked = event.getRightClicked();
		Player player = event.getPlayer();
		
		if (clicked instanceof Horse) {
			if (!game.horses()) {
				player.sendMessage(Main.PREFIX + "Horses are disabled.");
				event.setCancelled(true);
				return;
			}
			
			if (!game.horseHealing()) {
				ItemStack hand = player.getItemInHand();
				
				if (hand == null) {
					return;
				}
				
				Material type = hand.getType();
				
				if (type != Material.SUGAR && type != Material.WHEAT && type != Material.APPLE && type != Material.GOLDEN_CARROT && type != Material.GOLDEN_APPLE && type != Material.HAY_BLOCK) {
					return;
				}

				player.sendMessage(Main.PREFIX + "Horse healing is disabled.");
				player.updateInventory();
				event.setCancelled(true);
			}
			return;
		}
		
		if (!(clicked instanceof Player)) {
			return;
		}
	    	
		Player interacted = (Player) clicked;
				
		Spectator spec = Spectator.getInstance();
		InvGUI inv = InvGUI.getInstance();
		
		if (!spec.isSpectating(player)) {
			return;
		}
		
		if (spec.isSpectating(interacted)) {
			return;
		}
		
		inv.openPlayerInventory(player, interacted);
    }
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		Recipe recipe = event.getRecipe();
		
		CraftingInventory inv = event.getInventory();
		ItemStack item = recipe.getResult();
		
		if (item == null) {
			return;
		}
		
		/**
		 * @author Ghowden
		 */
        if (RecipeUtils.areSimilar(event.getRecipe(), Main.headRecipe)) {
            ItemMeta meta = item.getItemMeta();
            String name = "N/A";
          
            for (ItemStack content : inv.getContents()) {
                if (content.getType() == Material.SKULL_ITEM) {
                    SkullMeta skullMeta = (SkullMeta) content.getItemMeta();
                    name = skullMeta.getOwner();
                    break;
                }
            }

            List<String> list = meta.getLore();
            list.add(ChatColor.AQUA + "Made from the head of: " + (name == null ? "N/A" : name));
            meta.setLore(list);
            item.setItemMeta(meta);
            
            inv.setResult(item);
        }
		
		if (item.getType() == Material.GOLDEN_APPLE) {
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore() && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
				ScenarioManager scen = ScenarioManager.getInstance();
				
				if (scen.getScenario(VengefulSpirits.class).isEnabled()) {
					return;
				}
				
				if (!game.goldenHeads()) {
					inv.setResult(new ItemStack(Material.AIR));
				}
				return;
			}
			
			if (item.getDurability() == 1) {
				if (!game.notchApples()) {
					inv.setResult(new ItemStack(Material.AIR));
				}
			}
			return;
		}
		
		if (item.getType() == Material.SPECKLED_MELON) {
			if (recipe instanceof ShapedRecipe) {
				ShapedRecipe shaped = (ShapedRecipe) event.getRecipe();
				
				if (game.goldenMelonNeedsIngots()) {
					if (shaped.getIngredientMap().values().contains(new ItemStack (Material.GOLD_NUGGET))) {
						inv.setResult(new ItemStack(Material.AIR));
					}
				} else {
					if (shaped.getIngredientMap().values().contains(new ItemStack (Material.GOLD_INGOT))) {
						inv.setResult(new ItemStack(Material.AIR));
					}
				}
			}
			return;
		}
		
		if (item.getType() == Material.BOOKSHELF) {
			if (!game.bookshelves()) {
				inv.setResult(new ItemStack(Material.AIR));
			}
		}
    }
	
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		Spectator spec = Spectator.getInstance();
		Player player = event.getPlayer();
		
		if (!spec.isSpectating(player) && State.isState(State.INGAME)) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final ItemStack item = event.getItem();
		
		User user = User.get(player);
		
		final float before = player.getSaturation();

		new BukkitRunnable() {
			public void run() {
				float change = player.getSaturation() - before;
				player.setSaturation((float) (before + change * 2.5D));
			}
        }.runTaskLater(Main.plugin, 1);
		
		if (item.getType() == Material.GOLDEN_APPLE) {
			if (!game.absorption()) {
				player.removePotionEffect(PotionEffectType.ABSORPTION);
				
				new BukkitRunnable() {
					public void run() {
						player.removePotionEffect(PotionEffectType.ABSORPTION);
					}
		        }.runTaskLater(Main.plugin, 1);
			}
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (25 * game.goldenHeadsHeal()), 1));
				user.increaseStat(Stat.GOLDENHEADSEATEN);
			} else {
				user.increaseStat(Stat.GOLDENAPPLESEATEN);
			}
			return;
		}
		
		if (item.getType() == Material.POTION && item.getDurability() != 0) {
			user.increaseStat(Stat.POTIONS);
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		World world = player.getWorld();
		
		if (world.getName().equals("lobby")) {
			event.setCancelled(true);
			event.setFoodLevel(20);
			return;
		}
		
		if (event.getFoodLevel() < player.getFoodLevel()) {
			event.setCancelled(new Random().nextInt(100) < 66);
	    }
	}
	
	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		
		if (player.getAllowFlight() && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}
}