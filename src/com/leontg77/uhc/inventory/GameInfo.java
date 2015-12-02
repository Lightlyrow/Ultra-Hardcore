package com.leontg77.uhc.inventory;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.User.Rank;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.scenario.scenarios.Moles;
import com.leontg77.uhc.utils.FileUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.NameUtils;

public class GameInfo extends InvGUI implements Listener {
	private Inventory inv = Bukkit.getServer().createInventory(null, 45, "» §7Game Information");
	
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
	
	public void update() {
		ArrayList<String> lore = new ArrayList<String>();
		Game game = Game.getInstance();
		
		// general info item
		ItemStack general = new ItemStack (Material.SIGN);
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
		inv.setItem(0, general);
		lore.clear();
		
		ItemStack chat = new ItemStack (Material.PAPER);
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
		inv.setItem(2, chat);
		lore.clear();
		
		ItemStack pvp = new ItemStack (Material.IRON_SWORD);
		ItemMeta pvpMeta = pvp.getItemMeta();
		pvpMeta.setDisplayName("§8» §6PvP Rules §8«");
		lore.add(" ");
		lore.add("§8» §7iPvP: §cNot Allowed before pvp.");
		lore.add("§8» §7Team Killing: " + ((GameUtils.getTeamSize().startsWith("r") || GameUtils.getTeamSize().isEmpty()) && !ScenarioManager.getInstance().getScenario(Moles.class).isEnabled() ? "§cNot Allowed." : "§aAllowed."));
		lore.add("§8» §7Stalking: §aAllowed. §c(Not excessive)");
		lore.add("§8» §7Stealing: §aAllowed.");
		lore.add("§8» §7Crossteaming: §cNot Allowed.");
		lore.add(" ");
		pvpMeta.setLore(lore);
		pvpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		pvp.setItemMeta(pvpMeta);
		inv.setItem(4, pvp);
		lore.clear();
		
		ItemStack mining = new ItemStack (Material.DIAMOND_PICKAXE);
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
			lore.add("§8» §7Blastmining: §aAllowed.");
			lore.add("§8» §7Staircasing: §aAllowed.");
			lore.add("§8» §7Rollercoastering: §aAllowed.");
			lore.add("§8» §7Digging to sounds: §aAllowed.");
			lore.add("§8» §7Digging to entities: §aAllowed.");
			lore.add("§8» §7Digging to players: §aAllowed.");
			lore.add(" ");
		} else {
			lore.add("§8» §7AntiStripmine is disabled.");
			lore.add(" ");
			lore.add("§8» §7Stripmining: §cNot Allowed.");
			lore.add("§8» §7Branchmining: §cNot Allowed.");
			lore.add("§8» §7Pokeholing: §cNot Allowed.");
			lore.add("§8» §7Blastmining: §aAllowed.");
			lore.add("§8» §7Staircasing: §aAllowed.");
			lore.add("§8» §7Rollercoastering: §aAllowed.");
			lore.add("§8» §7Digging to sounds: §aAllowed.");
			lore.add("§8» §7Digging to entities: §aAllowed.");
			lore.add("§8» §7Digging to players: §cOnly if you see them.");
			lore.add(" ");
		}
		miningMeta.setLore(lore);
		miningMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		mining.setItemMeta(miningMeta);
		inv.setItem(6, mining);
		lore.clear();
		
		ItemStack misc = new ItemStack (Material.NETHER_STAR);
		ItemMeta miscMeta = misc.getItemMeta();
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
		misc.setItemMeta(miscMeta);
		inv.setItem(8, misc);
		lore.clear();
		
		ItemStack commands = new ItemStack (Material.BANNER, 1, (short) 1);
		ItemMeta commandsMeta = commands.getItemMeta();
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
		inv.setItem(21, commands);
		lore.clear();
		
		ItemStack scenario = new ItemStack (Material.BANNER, 1, (short) 14);
		ItemMeta scenarioMeta = scenario.getItemMeta();
		scenarioMeta.setDisplayName("§8» §6Game Information §8«");
		if (GameUtils.getTeamSize().startsWith("No")) {
			lore.add(" ");
			lore.add("§8» §7There are no games running.");
			lore.add(" ");
		} else if (GameUtils.getTeamSize().startsWith("Open")) {
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
			lore.add("§8» §7" + GameUtils.getAdvancedTeamSize());
			lore.add(" ");
			lore.add("§8» §cScenarios:");
			for (String scen : game.getScenarios().split(" ")) {
				lore.add("§8» §7" + scen);
			}
			lore.add(" ");
		}
		scenarioMeta.setLore(lore);
		scenario.setItemMeta(scenarioMeta);
		inv.setItem(23, scenario);
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
			lore.add("§8» §7Strength: " + (game.strength() ? (game.nerfedStrength() ? "§cNerfed" : "§aVanilla") : "§cDisabled"));
			lore.add("§8» §7Tier 2: " + (game.tier2() ? "§aEnabled." : "§cDisabled."));
			lore.add("§8» §7Splash: " + (game.splash() ? "§aEnabled." : "§cDisabled."));
			lore.add(" ");
			lore.add("§8» §7Golden Melon: §6" + (game.goldenMelonNeedsIngots() ? "Gold Ingots." : "Golden Nuggets."));
			lore.add("§8» §7Ghast Drop: §6" + (game.ghastDropGold() ? "Gold Ingot." : "Ghast Tear."));
			lore.add("§8» §7Quartz XP: §c50% reduced.");
			lore.add(" ");
		}
		netherMeta.setLore(lore);
		nether.setItemMeta(netherMeta);
		inv.setItem(36, nether);
		lore.clear();
		
		ItemStack healing = new ItemStack (Material.GOLDEN_APPLE);
		ItemMeta healingMeta = healing.getItemMeta();
		healingMeta.setDisplayName("§8» §6Healing Info §8«");
		lore.add(" ");
		lore.add("§8» §7Absorption: " + (game.absorption() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7Golden Heads: " + (game.goldenHeads() ? "§aEnabled." : "§cDisabled."));
		if (game.goldenHeads()) {
			lore.add("§8» §7Heads Heal: §6" + game.goldenHeadsHeal() + " hearts.");
		}
		lore.add("§8» §7Notch Apples: " + (game.notchApples() ? "§aEnabled." : "§cDisabled."));
		lore.add(" ");
		healingMeta.setLore(lore);
		healing.setItemMeta(healingMeta);
		inv.setItem(38, healing);
		lore.clear();
		
		ItemStack rates = new ItemStack (Material.FLINT);
		ItemMeta ratesMeta = rates.getItemMeta();
		ratesMeta.setDisplayName("§8» §6Rates Info §8«");
		lore.add(" ");
		lore.add("§8» §7Apple Rates: §6" + game.getAppleRates() + "%");
		lore.add("§8» §7Shears: " + (game.shears() ? "§aWork." : "§cDoes not work.") + "");
		lore.add("§8» §7Flint Rates: §6" + game.getFlintRates() + "%");
		lore.add(" ");
		lore.add("§8» §7Mob Rates: §6Vanilla.");
		lore.add("§8» §7Ore Rates: §6Vanilla.");
		lore.add("§8» §7Cave Rates: §6Vanilla.");
		lore.add(" ");
		ratesMeta.setLore(lore);
		rates.setItemMeta(ratesMeta);
		inv.setItem(40, rates);
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
		inv.setItem(42, horse);
		lore.clear();
		
		ItemStack miscI = new ItemStack (Material.ENDER_PEARL);
		ItemMeta miscIMeta = miscI.getItemMeta();
		miscIMeta.setDisplayName("§8» §6Misc. Info §8«");
		lore.add(" ");
		lore.add("§8» §7Death Lightning: " + (game.deathLightning() ? "§aEnabled." : "§cDisabled."));
		lore.add("§8» §7Saturation Fix: §aEnabled.");
		lore.add(" ");
		lore.add("§8» §7Enderpearl Damage: " + (game.pearlDamage() ? "§aEnabled, deals 1 heart." : "§cDisabled."));
		lore.add(" ");
		lore.add("§8» §7Border shrinks: §6" + NameUtils.fixString(game.getBorderShrink().getPreText(), false) + game.getBorderShrink().name().toLowerCase() + ".");
		lore.add("§8» §7The border will kill you if you go outside!");
		lore.add(" ");
		lore.add("§8» §7At meetup you can do everything you want");
		lore.add("§8» §7as long as you are inside the border and");
		lore.add("§8» §7on the surface, border can shrink to 100x100.");
		lore.add(" ");
		miscIMeta.setLore(lore);
		miscI.setItemMeta(miscIMeta);
		inv.setItem(44, miscI);
		lore.clear();
	}
	
	public void updateStaff() {
		StringBuilder staffs = new StringBuilder();
		StringBuilder owners = new StringBuilder();
		StringBuilder hosts = new StringBuilder();
		StringBuilder specs = new StringBuilder();
		
		ArrayList<String> hostL = new ArrayList<String>();
		ArrayList<String> staffL = new ArrayList<String>();
		ArrayList<String> specL = new ArrayList<String>();
		
		int i = 1;
		int j = -1;
		
		for (FileConfiguration config : FileUtils.files) {
			if (config.getString("rank").equals(Rank.ADMIN.name())) {
				hostL.add(config.getString("username"));
			}

			if (config.getString("rank").equals(Rank.HOST.name())) {
				hostL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.TRIAL.name())) {
				hostL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.STAFF.name())) {
				staffL.add(config.getString("username"));
			}
			
			if (config.getString("rank").equals(Rank.SPEC.name())) {
				specL.add(config.getString("username"));
			}
		}
		
		for (String sL : hostL) {
			if (hosts.length() > 0) {
				if (hostL.size() == i) {
					hosts.append(" and ");
				} else {
					hosts.append(", ");
				}
			}
			
			if (j == 2) {
				hosts.append("-");
				j = 0;
			} else {
				j++;
			}
			
			hosts.append(sL);
			i++;
		}
		
		i = 1;
		j = -1;
		
		for (String sL : staffL) {
			if (staffs.length() > 0) {
				if (staffL.size() == i) {
					staffs.append(" and ");
				} else {
					staffs.append(", ");
				}
			}
			
			if (j == 2) {
				staffs.append("-");
				j = 0;
			} else {
				j++;
			}
			
			staffs.append(sL);
			i++;
		}
		
		i = 1;
		j = -1;
		
		for (String pL : specL) {
			if (specs.length() > 0) {
				if (specL.size() == i) {
					specs.append(" and ");
				} else {
					specs.append(", ");
				}
			}
			
			if (j == 2) {
				specs.append("-");
				j = 0;
			} else {
				j++;
			}
			
			specs.append(pL);
			i++;
		}
		
		i = 1;
		j = -1;
		
		for (OfflinePlayer ops : Bukkit.getServer().getOperators()) {
			if (owners.length() > 0) {
				if (Bukkit.getOperators().size() == i) {
					owners.append(" and ");
				} else {
					owners.append(", ");
				}
			}
			
			if (j == 2) {
				hosts.append("-");
				j = 0;
			} else {
				j++;
			}
			
			owners.append(ops.getName());
			i++;
		}
		
		ArrayList<String> lore = new ArrayList<String>();

		ItemStack staff = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta staffMeta = (SkullMeta) staff.getItemMeta();
		staffMeta.setDisplayName("§8» §6Staff §8«");
		
		lore.add(" ");
		lore.add("§8» §4Owners:");
		
		for (String split : owners.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
		
		lore.add(" ");
		lore.add("§8» §4Hosts:");
		
		for (String split : hosts.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
		
		lore.add(" ");
		lore.add("§8» §cStaff:");
		
		for (String split : staffs.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
		
		lore.add(" ");
		lore.add("§8» §9Specs:");
		
		for (String split : specs.toString().split("-")) {
			lore.add("§8» §7" + split);
		}
		
		lore.add(" ");
		staffMeta.setLore(lore);
		staffMeta.setOwner("LeonTG77");
		staff.setItemMeta(staffMeta);
		inv.setItem(19, staff);
		lore.clear();
	}
}