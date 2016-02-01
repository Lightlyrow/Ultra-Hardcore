package com.leontg77.ultrahardcore.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Moles;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

public class GameInfo extends InvGUI implements Listener {
	private Inventory inv = Bukkit.createInventory(null, 45, "» §7Game Information");
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Inventory inv = event.getInventory();
		
		if (!inv.getTitle().equals(this.inv.getTitle())) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	/**
	 * Get the GameInfo inventory.
	 * 
	 * @return The inventory.
	 */
	public Inventory get() {
		return inv;
	}
	
	/**
	 * Update the item lores in the inventory.
	 */
	public void update() {
		ArrayList<String> lore = new ArrayList<String>();
		Game game = Game.getInstance();
		
		// general info item
		ItemStack general = new ItemStack(Material.SIGN);
		ItemMeta generalMeta = general.getItemMeta();
		generalMeta.setDisplayName("§8» §6General Info §8«");
		lore.add(" ");;
		lore.add("§8» §7Teaming in the arena: §cNot Allowed.");
		lore.add("§8» §7Starter food: §cNone.");
		lore.add(" ");
		lore.add("§8» §7Towering: §aAllowed, but come down at meetup.");
		lore.add("§8» §7Forting: §aAllowed before meetup.");
		lore.add(" ");
		lore.add("§8» §7You can follow our twitter @ArcticUHC to find");
		lore.add(" §7out when our next games are.");
		lore.add(" ");
		lore.add("§8» §7Final heal is 20 seconds after start, ");
		lore.add(" §7no more are given after that.");
		lore.add(" ");
		lore.add("§8» §7Our UHC plugin is custom coded by LeonTG77.");
		lore.add(" ");
		generalMeta.setLore(lore);
		general.setItemMeta(generalMeta);
		lore.clear();
		
		ItemStack chat = new ItemStack(Material.PAPER);
		ItemMeta chatMeta = chat.getItemMeta();
		chatMeta.setDisplayName("§8» §6Chat Rules §8«");
		lore.add(" ");
		lore.add("§8» §7Excessive rage: §eKick.");
		lore.add(" ");
		lore.add("§8» §7Talking other languages in chat: §cMute.");
		lore.add("§8» §7Excessive Swearing: §cMute.");
		lore.add("§8» §7Homophobic: §cMute.");
		lore.add("§8» §7Spamming: §cMute.");
		lore.add("§8» §7Insults: §cMute.");
		lore.add("§8» §7Racism: §cMute.");
		lore.add(" ");
		lore.add("§8» §7Helpop abuse: §4Ban.");
		lore.add("§8» §7Disrespect: §4Ban.");
		lore.add(" ");
		lore.add("§8» §7Spoiling when alive: §aAllowed.");
		lore.add("§8» §7Spoiling when dead: §cNot allowed.");
		lore.add(" ");
		chatMeta.setLore(lore);
		chat.setItemMeta(chatMeta);
		lore.clear();
		
		String teamKilling;
		
		if (game.getTeamSize().startsWith("rTo")) {
			if (ScenarioManager.getInstance().getScenario(Moles.class).isEnabled()) {
				teamKilling = "§aAllowed";
			} else {
				teamKilling = "§cNot Allowed";
			}
		}
		else if (game.getTeamSize().equals("FFA")) {
			teamKilling = "§cNo teaming at all";
		}
		else if (game.getTeamSize().startsWith("cTo")) {
			teamKilling = "§aAllowed";
		}
		else {
			teamKilling = "§cNot Allowed";
		}
		
		ItemStack pvp = new ItemStack (Material.DIAMOND_SWORD);
		ItemMeta pvpMeta = pvp.getItemMeta();
		pvpMeta.setDisplayName("§8» §6PvP Rules §8«");
		lore.add(" ");
		lore.add("§8» §7iPvP: §cNot Allowed before pvp.");
		lore.add(" ");
		lore.add("§8» §7Stalking: §aAllowed. §c(Not excessive)");
		lore.add("§8» §7Stealing: §aAllowed.");
		lore.add(" ");
		lore.add("§8» §7Team Killing: " + teamKilling);
		lore.add("§8» §7Crossteaming: §cNot Allowed.");
		lore.add(" ");
		pvpMeta.setLore(lore);
		pvpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		pvp.setItemMeta(pvpMeta);
		lore.clear();
		
		ItemStack mining = new ItemStack (Material.IRON_PICKAXE);
		ItemMeta miningMeta = mining.getItemMeta();
		miningMeta.setDisplayName("§8» §6Mining Rules §8«");
		lore.add(" ");
		lore.add("§8» §4Important info:");
		if (game.antiStripmine()) {
			lore.add("§8» §7Gold, diamonds and lapis only spawns near caves.");
			lore.add(" ");
			lore.add("§8» §7Stripmining: §aAllowed.");
			lore.add("§8» §7Branchmining: §aAllowed.");
			lore.add("§8» §7Pokeholing: §aAllowed.");
			lore.add(" ");
			lore.add("§8» §7Blastmining: §aAllowed.");
			lore.add("§8» §7Staircasing: §aAllowed.");
			lore.add("§8» §7Rollercoastering: §aAllowed.");
			lore.add(" ");
			lore.add("§8» §7Digging to sounds: §aAllowed.");
			lore.add("§8» §7Digging to entities: §aAllowed.");
			lore.add("§8» §7Digging to players: §aAllowed.");
		} else {
			lore.add("§8» §7\"Ores in caves\" feature is disabled.");
			lore.add(" ");
			lore.add("§8» §7Stripmining: §cNot Allowed.");
			lore.add("§8» §7Branchmining: §cNot Allowed.");
			lore.add("§8» §7Pokeholing: §cOnly to caves.");
			lore.add(" ");
			lore.add("§8» §7Blastmining: §aAllowed.");
			lore.add("§8» §7Staircasing: §aAllowed, §ey:32➷.");
			lore.add("§8» §7Rollercoastering: §aAllowed, §ey:2➹y:32➷y:2➹y:32.");
			lore.add(" ");
			lore.add("§8» §7Digging to sounds: §aAllowed.");
			lore.add("§8» §7Digging to entities: §aAllowed.");
			lore.add("§8» §7Digging to players: §cOnly if you see them.");
		}
		lore.add(" ");
		lore.add("§8» §7Digging straight down: §aAllowed.");
		lore.add("§8» §7Digging around lava: §aAllowed.");
		lore.add(" ");
		miningMeta.setLore(lore);
		miningMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		mining.setItemMeta(miningMeta);
		lore.clear();
		
		ItemStack meetup = new ItemStack (Material.EYE_OF_ENDER);
		ItemMeta miscMeta = meetup.getItemMeta();
		miscMeta.setDisplayName("§8» §6Misc. Rules §8«");
		lore.add(" ");
		lore.add("§8» §7Suiciding in random team games: §cNot Allowed.");
		lore.add("§8» §7TS in random teams games: §cRequired.");
		lore.add(" ");
		lore.add("§8» §7Xray/Cavefinder: §cNot Allowed.");
		lore.add("§8» §7Hacked Client: §cNot Allowed.");
		lore.add("§8» §7Fast Break: §cNot Allowed.");
		lore.add(" ");
		lore.add("§8» §7F3+A Spam: §cNot Allowed.");
		lore.add("§8» §7Full Bright: §aAllowed.");
		lore.add(" ");
		lore.add("§8» §7Benefitting: §4Ban.");
		lore.add("§8» §7Bug abuse: §4Ban.");
		lore.add("§8» §7PvP Log: §4Ban.");
		lore.add("§8» §7PvE Log: §4Ban.");
		lore.add(" ");
		miscMeta.setLore(lore);
		meetup.setItemMeta(miscMeta);
		lore.clear();
		
		ItemStack commands = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta commandsMeta = (SkullMeta) commands.getItemMeta();
		commandsMeta.setOwner("MHF_Exclamation");
		commandsMeta.setDisplayName("§8» §6Useful commands §8«");
		lore.add(" ");
		lore.add("§a/uhc §8» §7§oView this menu :o");
		lore.add("§a/helpop §8» §7§oAsk for help by the staff.");
		lore.add("§a/list §8» §7§oView online players.");
		lore.add(" ");
		lore.add("§a/post §8» §7§oGet a link to the matchpost.");
		lore.add("§a/scen §8» §7§oView the enabled scenarios.");
		lore.add(" ");
		lore.add("§a/timeleft §8» §7§oView the timer.");
		lore.add("§a/border §8» §7§oView the current border size.");
		lore.add(" ");
		lore.add("§a/team §8» §7§oView the team help menu.");
		lore.add("§a/hof §8» §7§oView the hall of fame.");
		lore.add(" ");
		lore.add("§a/lag §8» §7§oView the server performance.");
		lore.add("§a/ms §8» §7§oView your or someones ping.");
		lore.add(" ");
		lore.add("§a/pm §8» §7§oTalk in team chat.");
		lore.add("§a/tl §8» §7§oTell your team your coords.");
		lore.add(" ");
		commandsMeta.setLore(lore);
		commands.setItemMeta(commandsMeta);
		lore.clear();
		
		ItemStack scenario = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta scenarioMeta = (SkullMeta) commands.getItemMeta();
		scenarioMeta.setOwner("MHF_Question");
		scenarioMeta.setDisplayName("§8» §6Game Information §8«");
		if (GameUtils.getTeamSize(false, false).startsWith("No")) {
			lore.add(" ");
			lore.add("§8» §7There are no games running.");
			lore.add(" ");
		} else if (GameUtils.getTeamSize(false, false).startsWith("Open")) {
			lore.add(" ");
			lore.add("§8» §7Currently having Open " + game.getScenarios());
			lore.add(" ");
		} else {
			lore.add(" ");
			lore.add("§8» §7Match post: §a" + game.getMatchPost());
			lore.add("§8» §7Max player slots: §a" + game.getMaxPlayers());
			lore.add(" ");
			lore.add("§8» §7PvP enabled after: §a" + game.getPvP() + " minutes.");
			lore.add("§8» §7Meetup is after: §a" + game.getMeetup() + " minutes.");
			lore.add(" ");
			lore.add("§8» §cTeamsize:");
			lore.add("§8» §7" + GameUtils.getTeamSize(true, false));
			lore.add(" ");
			lore.add("§8» §cScenarios:");
			for (String scen : game.getScenarios().split(", ")) {
				lore.add("§8» §7" + scen);
			}
			lore.add(" ");
		}
		scenarioMeta.setLore(lore);
		scenario.setItemMeta(scenarioMeta);
		lore.clear();
		
		ItemStack nether = new ItemStack (Material.NETHER_STALK);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName("§8» §6Nether Info §8«");
		lore.add(" ");
		lore.add("§8» §7Nether: " + (game.nether() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7The End: " + (game.theEnd() ? "§aEnabled." : "§cDisabled."));
		lore.add(" ");
		if (game.nether()) {
			lore.add("§8» §7Trapping: " + (game.theEnd() ? "§cNot allowed." : "§aAllowed."));
			lore.add("§8» §7Camping: §aAllowed.");
			lore.add(" ");
			lore.add("§8» §7Strength: " + (game.strength() ? "§cNerfed" : "§cDisabled"));
			lore.add("§8» §7Tier 2: " + (game.tier2() ? "§aEnabled." : "§cDisabled."));
			lore.add("§8» §7Splash: " + (game.splash() ? "§aEnabled." : "§cDisabled."));
			lore.add(" ");
			lore.add("§8» §7Golden Melon: §6Gold Ingots.");
			lore.add("§8» §7Ghast Drop: §6Gold Ingot.");
			lore.add("§8» §7Quartz XP: §c50% reduced.");
			lore.add(" ");
		}
		netherMeta.setLore(lore);
		nether.setItemMeta(netherMeta);
		lore.clear();
		
		ItemStack healing = new ItemStack (Material.GOLDEN_APPLE);
		ItemMeta healingMeta = healing.getItemMeta();
		healingMeta.setDisplayName("§8» §6Healing Info §8«");
		lore.add(" ");
		lore.add("§8» §7Absorption: " + (game.absorption() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7Golden Heads: " + (game.goldenHeads() ? "§aEnabled." : "§cDisabled."));
		if (game.goldenHeads()) {
			lore.add("§8» §7Heads Heal: §6" + NumberUtils.formatDouble(((double) game.goldenHeadsHeal()) / 2) + " hearts.");
		}
		lore.add("§8» §7Notch Apples: " + (game.notchApples() ? "§aEnabled." : "§cDisabled."));
		lore.add(" ");
		healingMeta.setLore(lore);
		healing.setItemMeta(healingMeta);
		lore.clear();
		
		ItemStack rates = new ItemStack (Material.FLINT);
		ItemMeta ratesMeta = rates.getItemMeta();
		ratesMeta.setDisplayName("§8» §6Rates Info §8«");
		lore.add(" ");
		lore.add("§8» §7Apple Rates: §6" + NumberUtils.formatDouble(game.getAppleRates() * 100) + "%");
		lore.add("§8» §7Shears: " + (game.shears() ? "§aWork." : "§cDoes not work."));
		lore.add("§8» §7Flint Rates: §6" + NumberUtils.formatDouble(game.getFlintRates() * 100) + "%");
		lore.add(" ");
		lore.add("§8» §7Mob Rates: §6Vanilla, §eDifficulty Hard");
		lore.add("§8» §7Ore Rates: §6Vanilla.");
		lore.add("§8» §7Cave Rates: §6Vanilla.");
		lore.add(" ");
		lore.add("§8» §7Witch Health Pot: §630% (100% when poisoned)");
		lore.add(" ");
		ratesMeta.setLore(lore);
		rates.setItemMeta(ratesMeta);
		lore.clear();
		
		ItemStack horse = new ItemStack (Material.SADDLE);
		ItemMeta horseMeta = horse.getItemMeta();
		horseMeta.setDisplayName("§8» §6Horse Info §8«");
		lore.add(" ");
		lore.add("§8» §7Horses: " + (game.horses() ? "§aEnabled." : "§cDisabled."));
		if (game.horses()) {
			lore.add("§8» §7Horse Healing: " + (game.horseHealing() ? "§aEnabled." : "§cDisabled."));
			lore.add("§8» §7Horse Armor: " + (game.horseArmor() ? "§aEnabled." : "§cDisabled."));
		}
		lore.add(" ");
		horseMeta.setLore(lore);
		horse.setItemMeta(horseMeta);
		lore.clear();
		
		ItemStack misc = new ItemStack (Material.ENDER_PEARL);
		ItemMeta miscIMeta = misc.getItemMeta();
		miscIMeta.setDisplayName("§8» §6Misc. Info §8«");
		lore.add(" ");
		lore.add("§8» §7Death Lightning: " + (game.deathLightning() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7Saturation Fix: §aEnabled.");
		lore.add(" ");
		lore.add("§8» §7Enderpearl Damage: " + (game.pearlDamage() != 0 ? "§aDeals " + (game.pearlDamage() == 1 ? "1 heart." : game.pearlDamage() + " hearts."): "§cDisabled."));
		lore.add("§8» §7Bookshelves: " + (game.bookshelves() ? "§aEnabled." : "§cDisabled."));
		lore.add(" ");
		lore.add("§8» §7Border shrinks: §6" + NameUtils.capitalizeString(game.getBorderShrink().getPreText(), false) + game.getBorderShrink().name().toLowerCase() + ".");
		lore.add("§8» §7The border will kill you if you go outside!");
		lore.add(" ");
		lore.add("§8» §7At meetup you can do everything you want");
		lore.add("§8» §7as long as you are inside the border and");
		lore.add("§8» §7on the surface, border can shrink to 100x100.");
		lore.add(" ");
		miscIMeta.setLore(lore);
		misc.setItemMeta(miscIMeta);
		lore.clear();

		inv.setItem(0, general);
		inv.setItem(2, chat);
		inv.setItem(4, pvp);
		inv.setItem(6, mining);
		inv.setItem(8, meetup);
		
		inv.setItem(21, commands);
		inv.setItem(23, scenario);
		
		inv.setItem(36, healing);
		inv.setItem(38, nether);
		inv.setItem(40, rates);
		inv.setItem(42, horse);
		inv.setItem(44, misc);
		
		final ItemStack black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		final ItemStack gray = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		
		inv.setItem(1, gray);
		inv.setItem(3, gray);
		inv.setItem(5, gray);
		inv.setItem(7, gray);
		inv.setItem(9, gray);
		inv.setItem(11, gray);
		inv.setItem(13, gray);
		inv.setItem(15, gray);
		inv.setItem(17, gray);
		inv.setItem(27, gray);
		inv.setItem(29, gray);
		inv.setItem(31, gray);
		inv.setItem(33, gray);
		inv.setItem(35, gray);
		inv.setItem(37, gray);
		inv.setItem(39, gray);
		inv.setItem(41, gray);
		inv.setItem(43, gray);
		
		inv.setItem(10, black);
		inv.setItem(12, black);
		inv.setItem(14, black);
		inv.setItem(16, black);
		inv.setItem(18, black);
		inv.setItem(20, black);
		inv.setItem(22, black);
		inv.setItem(24, black);
		inv.setItem(26, black);
		inv.setItem(28, black);
		inv.setItem(30, black);
		inv.setItem(32, black);
		inv.setItem(34, black);
	}
	
	/**
	 * Update the timer item in the inventory.
	 */
	public void updateTimer() {
		ItemStack timer = new ItemStack (Material.WATCH);
		ItemMeta timerMeta = timer.getItemMeta();
		timerMeta.setDisplayName("§8» §6Timers §8«");
		
		List<String> lore = new ArrayList<String>();
		lore.add(" ");
		
		if (Game.getInstance().isRecordedRound()) {
			lore.add("§8» §7Current Episode: §a" + Timers.meetup);
			lore.add("§8» §7Time to next episode: §a" + Timers.time + " mins");
		} else if (GameUtils.getTeamSize(false, false).startsWith("No") || GameUtils.getTeamSize(false, false).startsWith("Open")) {
			lore.add("§8» §7There are no matches running.");
		} else if (!State.isState(State.INGAME)) {
			lore.add("§8» §7The game has not started yet.");
		} else {
			lore.add("§8» §7Time since start: §a" + DateUtils.ticksToString(Timers.timeSeconds));
			lore.add(Timers.pvpSeconds <= 0 ? "§8» §cPvP is enabled." : "§8» §7PvP in: §a" + DateUtils.ticksToString(Timers.pvpSeconds));
			lore.add(Timers.meetupSeconds <= 0 ? "§8» §cMeetup is now!" : "§8» §7Meetup in: §a" + DateUtils.ticksToString(Timers.meetupSeconds));
		}
		
		lore.add(" ");
		timerMeta.setLore(lore);
		timer.setItemMeta(timerMeta);
		inv.setItem(25, timer);
		lore.clear();
	}
	
	/**
	 * Update the staff item in the inventory.
	 */
	public void updateStaff() {
		List<String> lore = new ArrayList<String>();

		ItemStack staff = new ItemStack (Material.BOOK, 1);
		ItemMeta staffMeta = staff.getItemMeta();
		staffMeta.setDisplayName("§8» §6Staff §8«");
		
		lore.add(" ");
		lore.add("§8» §4Owners:");
		
		for (String split : getRankList(Rank.OWNER).toString().split("-")) {
			lore.add("  §7" + split);
		}
		
		lore.add(" ");
		lore.add("§8» §4Hosts:");
		
		for (String split : getRankList(Rank.TRIAL, Rank.HOST).toString().split("-")) {
			lore.add("  §7" + split);
		}
		
		lore.add(" ");
		lore.add("§8» §cStaff:");
		
		for (String split : getRankList(Rank.STAFF).toString().split("-")) {
			lore.add("  §7" + split);
		}
		
		lore.add(" ");
		lore.add("§8» §9Specs:");
		
		for (String split : getRankList(Rank.SPEC).split("-")) {
			lore.add("  §7" + split);
		}
		
		lore.add(" ");
		staffMeta.setLore(lore);
		staff.setItemMeta(staffMeta);
		inv.setItem(19, staff);
		lore.clear();
	}
	
	/**
	 * Make a list of people having the given rank.
	 * 
	 * @param rank The rank to get a list for.
	 * @return A list of people with the rank.
	 */
	private String getRankList(Rank... ranks) {
		StringBuilder list = new StringBuilder();
		List<String> peopleWithRank = new ArrayList<String>();
		
		int i = 1;
		int j = -1;
		
		for (FileConfiguration config : FileUtils.getUserFiles()) {
			for (Rank rank : ranks) {
				if (config.getString("rank", "USER").equals(rank.name())) {
					peopleWithRank.add(config.getString("username"));
				}
			}
		}
		
		for (String loopRanks : peopleWithRank) {
			if (list.length() > 0) {
				if (peopleWithRank.size() == i) {
					list.append(" and ");
				} else {
					list.append(", ");
				}
			}
			
			if (j == 2) {
				list.append("-");
				j = 0;
			} else {
				j++;
			}
			
			list.append(loopRanks);
			i++;
		}
		
		return list.toString();
	}
}