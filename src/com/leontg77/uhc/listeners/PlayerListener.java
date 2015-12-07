package com.leontg77.uhc.listeners;

import static com.leontg77.uhc.Main.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Parkour;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.State;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Rank;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.inventory.InvGUI;
import com.leontg77.uhc.managers.BoardManager;
import com.leontg77.uhc.managers.TeamManager;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.scenario.scenarios.VengefulSpirits;
import com.leontg77.uhc.utils.BlockUtils;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.utils.RecipeUtils;

/**
 * Player listener class.
 * <p> 
 * Contains all eventhandlers for player releated events.
 * 
 * @author LeonTG77
 */
public class PlayerListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		Arena arena = Arena.getInstance();
		
		if (game.hardcoreHearts()) {
			new BukkitRunnable() {
				public void run() {
					player.spigot().respawn();
				}
			}.runTaskLater(Main.plugin, 18);
		}
		
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 

		BoardManager board = BoardManager.getInstance();
		User user = User.get(player);
		
		player.setWhitelisted(false);
		
		if (game.deathLightning()) {
		    player.getWorld().strikeLightningEffect(player.getLocation());
		    
		    for (Player online : PlayerUtils.getPlayers()) {
		    	if (online.getWorld() == player.getWorld()) {
		    		continue;
		    	}
		    	
		    	Location loc = player.getLocation().clone();
		    	loc.setWorld(online.getWorld());
		    	
		    	online.playSound(loc, Sound.AMBIENCE_THUNDER, 1, 1);
		    }
		}
		
		List<World> worlds = GameUtils.getGameWorlds();

	    if (game.goldenHeads() && worlds.contains(player.getWorld())) {
			new BukkitRunnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					player.getLocation().getBlock().setType(Material.NETHER_FENCE);
					player.getLocation().add(0, 1, 0).getBlock().setType(Material.SKULL);
				    
					Skull skull;
					
					try {
				        skull = (Skull) player.getLocation().add(0, 1, 0).getBlock().getState();
					} catch (Exception e) {
						Bukkit.getLogger().warning("Could not place player skull.");
						return;
					}
					
				    skull.setSkullType(SkullType.PLAYER);
				    skull.setOwner(player.getName());
				    skull.setRotation(BlockUtils.getBlockDirection(player.getLocation()));
				    skull.update();
				    
				    Block b = player.getLocation().add(0, 1, 0).getBlock();
				    b.setData((byte) 0x1, true);
				}
			}.runTaskLater(Main.plugin, 1);
	    }

		final String deathMessage = event.getDeathMessage();
		final Player killer = player.getKiller();

		if (killer == null) {
			if (worlds.contains(player.getWorld()) && !game.isRecordedRound() && State.isState(State.INGAME)) {
				board.setScore("§8» §a§lPvE", board.getScore("§8» §a§lPvE") + 1);
		        board.resetScore(player.getName());
			}

			user.increaseStat(Stat.DEATHS);
			
			if (deathMessage == null) {
				return;
			}

			for (Player online : PlayerUtils.getPlayers()) {
				online.sendMessage("§8» §f" + deathMessage);
			}
			
			Bukkit.getLogger().info("§8» §f" + deathMessage);
			event.setDeathMessage(null);
			return;
		}
		
		if (deathMessage != null && !deathMessage.isEmpty()) {
			ItemStack item = killer.getItemInHand();
			
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && deathMessage.contains(killer.getName()) && (deathMessage.contains("slain") || deathMessage.contains("shot"))) {
				String name = item.getItemMeta().getDisplayName();
				
				ComponentBuilder builder = new ComponentBuilder("§8» §r" + deathMessage.replace("[" + name + "]", ""));
				StringBuilder colored = new StringBuilder();
				
				if (killer.getItemInHand().getEnchantments().isEmpty()) {
					for (String entry : name.split(" ")) {
						colored.append("§o" + entry).append(" ");
					}
					
					builder.append("§f[" + colored.toString().trim() + "§f]");
				} else {
					for (String entry : name.split(" ")) {
						colored.append("§b§o" + entry).append(" ");
					}
					
					builder.append("§b[" + colored.toString().trim() + "§b]");
				}
				
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent(NameUtils.convertToJson(item))}));
				final BaseComponent[] result = builder.create();

				for (Player online : PlayerUtils.getPlayers()) {
					online.spigot().sendMessage(result);
				}
				
				Bukkit.getLogger().info("§8» §f" + event.getDeathMessage());
				
				event.setDeathMessage(null);
			} else {
				for (Player online : PlayerUtils.getPlayers()) {
					online.sendMessage("§8» §f" + deathMessage);
				}
				
				Bukkit.getLogger().info("§8» §f" + deathMessage);
				event.setDeathMessage(null);
			}
		}
		
		Team pteam = TeamManager.getInstance().getTeam(player);
		Team team = TeamManager.getInstance().getTeam(killer);
		
		if (pteam == null || !pteam.equals(team)) {
			if (worlds.contains(player.getWorld())) {
				board.setScore(killer.getName(), board.getScore(killer.getName()) + 1);
				
				if (game.isRecordedRound()) {
					return;
				}
				
				if (State.isState(State.INGAME)) {
					board.resetScore(player.getName());
				}

				user.increaseStat(Stat.DEATHS);
				
				User killUser = User.get(killer);
				killUser.increaseStat(Stat.KILLS);
				
				if (killUser.getStat(Stat.KILLSTREAK) < board.getScore(killer.getName())) {
					killUser.setStat(Stat.ARENAKILLSTREAK, board.getScore(killer.getName()));
				}
				
				if (Main.kills.containsKey(killer.getName())) {
					Main.kills.put(killer.getName(), Main.kills.get(killer.getName()) + 1);
				} else {
					Main.kills.put(killer.getName(), 1);
				}

				if (team != null) {
					if (Main.teamKills.containsKey(team.getName())) {
						Main.teamKills.put(team.getName(), Main.teamKills.get(team.getName()) + 1);
					} else {
						Main.teamKills.put(team.getName(), 1);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		Arena arena = Arena.getInstance();
		
		event.setRespawnLocation(Main.getSpawn());
		player.setMaxHealth(20);
		
		if (arena.isEnabled() || !State.isState(State.INGAME) || game.isRecordedRound()) {
			return;
		}
		
		player.sendMessage(Main.PREFIX + "Thanks for playing this game, it really means a lot :)");
		player.sendMessage(Main.PREFIX + "Follow us on twtter to know when our next games are: §a@ArcticUHC");
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.hidePlayer(player);
		}
		
		if (!player.hasPermission("uhc.prelist")) {
			player.sendMessage(Main.PREFIX + "You may stay as long as you want (You are vanished).");
			player.sendMessage(Main.PREFIX + "Please do not spam, rage, spoil or be a bad sportsman.");
			return;
		}
		
		player.sendMessage(Main.PREFIX + "You will be put into spectator mode in 5 seconds.");
		player.sendMessage(Main.PREFIX + "Please do not spam, rage, spoil or be a bad sportsman.");
		
		new BukkitRunnable() {
			public void run() {
				Spectator spec = Spectator.getInstance();
				
				if (!State.isState(State.INGAME) || !player.isOnline() || spec.isSpectating(player)) {
					return;
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					online.showPlayer(player);
				}
				
				spec.enableSpecmode(player);
			}
		}.runTaskLater(Main.plugin, 100);
	}
	
	@EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		User user = User.get(player);

		TeamManager teams = TeamManager.getInstance();
		Team team = teams.getTeam(player);
		
		String message = event.getMessage();
		String name = (team == null || team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName());
		
		event.setCancelled(true);
    	
    	if (game.isRecordedRound()) {
    		PlayerUtils.broadcast("§7" + name + "§8 » §f" + message);
    		return;
    	}
		
		if (VoteCommand.running && (message.equalsIgnoreCase("y") || message.equalsIgnoreCase("n"))) {
			World world = player.getWorld();
			
			if (State.isState(State.INGAME) && world.getName().equals("lobby")) {
				player.sendMessage(ChatColor.RED + "You cannot vote when you are dead.");
				return;
			}
			
			Spectator spec = Spectator.getInstance();
			
			if (spec.isSpectating(player)) {
				player.sendMessage(ChatColor.RED + "You cannot vote as a spectator.");
				return;
			}
			
			if (VoteCommand.voted.contains(player.getName())) {
				player.sendMessage(ChatColor.RED + "You have already voted.");
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("y")) {
				player.sendMessage(Main.PREFIX + "You voted yes.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.yes++;
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("n")) {
				player.sendMessage(Main.PREFIX + "You voted no.");
				VoteCommand.voted.add(player.getName());
				VoteCommand.no++;
			}
			return;
		}
    	
		if (user.isMuted()) {
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date date = new Date();
			
			if (user.getUnmuteTime() == -1 || user.getUnmuteTime() > date.getTime()) {
				player.sendMessage(Main.PREFIX + "You have been muted for: §a" + user.getMutedReason());
				
				if (user.getUnmuteTime() < 0) {
					player.sendMessage(Main.PREFIX + "Your mute is permanent.");
				} else {
					player.sendMessage(Main.PREFIX + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getUnmuteTime()));
				}
				return;
			} 
			else {
				user.unmute();
			}
		}

		Spectator spec = Spectator.getInstance();

		if (user.getRank() == Rank.OWNER) {
			String uuid = player.getUniqueId().toString();
			String prefix;
			
			if (uuid.equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				prefix = "§3Owner";
			} else {
				prefix = "§4Owner";
			}
			
			PlayerUtils.broadcast("§8[" + prefix + "§8] | §f" + name + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.HOST) {
			PlayerUtils.broadcast("§8[§4Host§8] | §f" + name + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.TRIAL) {
			PlayerUtils.broadcast("§8[§4Trial§8] | §f" + name + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.STAFF) {
			PlayerUtils.broadcast("§8[§cStaff§8] | §f" + name + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', message));
			return;
		}
		
		if (user.getRank() == Rank.DONATOR) {
			if (game.isMuted()) {
				player.sendMessage(Main.PREFIX + "The chat is currently muted.");
				return;
			}

			PlayerUtils.broadcast("§8[§aDonator§8] | §f" + name + "§8 » §f" + ChatColor.translateAlternateColorCodes('&', message));
			return;
		} 
		
		if (spec.isSpectating(player)) {
			if (game.isMuted()) {
				player.sendMessage(Main.PREFIX + "The chat is currently muted.");
				return;
			}

			PlayerUtils.broadcast("§8[§9Spec§8] | §f" + name + "§8 » §f" + message);
			return;
		} 
			
		if (game.isMuted()) {
			player.sendMessage(Main.PREFIX + "The chat is currently muted.");
			return;
		}

		PlayerUtils.broadcast("§7" + name + "§8 » §f" + message);
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();
		
		Spectator spec = Spectator.getInstance();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online == player) {
				continue;
			}
			
			if (!online.hasPermission("uhc.cmdspy")) {
				continue;
			}
			
			if (!spec.hasCommandSpy(online) && State.isState(State.INGAME)) {
				continue;
			}
			
			if (online.getGameMode() != GameMode.CREATIVE && !spec.isSpectating(online)) {
				continue;
			}
			
			online.sendMessage("§e" + player.getName() + ": §7" + message);
		}
		
		String command = message.split(" ")[0].substring(1);
		
		if (command.equalsIgnoreCase("fixdata")) {
			File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
			
			for (File file : folder.listFiles()) {
				FileConfiguration config = YamlConfiguration.loadConfiguration(file);
				
				config.set("stats.arenakillstreak", config.get("stats.arenaks"));
				config.set("stats.killstreak", config.get("stats.ks"));
				config.set("stats.cks", null);
				config.set("stats.ks", null);
				config.set("stats.arenacks", null);
				config.set("stats.arenaks", null);
				config.set("lastlogoff", null);
				
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			player.sendMessage(Main.PREFIX + "Fixed.");
			event.setCancelled(true);
			return;
		}
		
		if (command.equalsIgnoreCase("me") || command.equalsIgnoreCase("kill")) {
			player.sendMessage(Main.NO_PERM_MSG);
			event.setCancelled(true);
			return;
		}
		
		if (command.startsWith("bukkit:") || command.startsWith("minecraft:")) {
			if (player.hasPermission("uhc.admin")) {
				return;
			}
			
			player.sendMessage(Main.NO_PERM_MSG);
			event.setCancelled(true);
			return;
		}
		
		if (command.equalsIgnoreCase("rl") || command.equalsIgnoreCase("reload") || command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("restart")) {
			if (!State.isState(State.INGAME)) {
				return;
			}
			
			String finalCommand = command.replace("rl", "reload");
			
			player.sendMessage(ChatColor.RED + "You may not want to " + finalCommand + " when a game is running.");
			player.sendMessage(ChatColor.RED + "If you still want to " + finalCommand + ", do it in the console.");
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMotd("§4§lArctic UHC §8» §6" + GameUtils.getMOTDMessage() + " §8« [§71.8§8] [§7EU§8]\n§8» §f§oFollow us on twitter, §a§o@ArcticUHC§7§o!");
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
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6Golden Head")) {
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
		
		if (item.getType() == Material.BLAZE_POWDER) {
			if (!game.strength()) {
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
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25 * (game.goldenHeadsHeal() * 2), 1));
				user.increaseStat(Stat.GOLDENHEADSEATEN);
			} else {
				user.increaseStat(Stat.GOLDENAPPLESEATEN);
			}
			return;
		}
		
		if (item.getType() == Material.POTION) {
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
}