package com.leontg77.ultrahardcore.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

public class Config extends InvGUI implements Listener {
	private final Inventory inv = Bukkit.createInventory(null, 54, "» §7Game configuration");

	/**
	 * Get the top stats inventory.
	 * 
	 * @return The inventory.
	 */
	public Inventory get() {
		return inv;
	}

	/**
	 * Update the top stats inventory.
	 */
	public void update() {
		Game game = Game.getInstance();

		ItemStack absorption = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta absorptionMeta = absorption.getItemMeta();
		absorptionMeta.setDisplayName((game.absorption() ? "§a" : "§c")
				+ "Absorption");
		absorption.setItemMeta(absorptionMeta);

		ItemStack heads = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headsMeta = heads.getItemMeta();
		headsMeta.setDisplayName((game.goldenHeads() ? "§a" : "§c")
				+ "Golden Heads");
		heads.setItemMeta(headsMeta);
		
		ItemStack notchApples = new ItemStack(Material.GOLDEN_APPLE, 1,
				(short) 1);
		ItemMeta notchMeta = notchApples.getItemMeta();
		notchMeta.setDisplayName((game.notchApples() ? "§a" : "§c")
				+ "Notch Apples");
		notchApples.setItemMeta(notchMeta);

		ItemStack hearts = new ItemStack(Material.INK_SACK, 1, (short) 1);
		ItemMeta heartsMeta = hearts.getItemMeta();
		heartsMeta.setDisplayName((game.heartsOnTab() ? "§a" : "§c")
				+ "Hearts on tab");
		hearts.setItemMeta(heartsMeta);

		ItemStack hardcore = new ItemStack(Material.REDSTONE);
		ItemMeta hardcoreMeta = hardcore.getItemMeta();
		hardcoreMeta.setDisplayName((game.hardcoreHearts() ? "§a" : "§c")
				+ "Hardcore Hearts");
		hardcore.setItemMeta(hardcoreMeta);

		ItemStack tab = new ItemStack(Material.SIGN);
		ItemMeta tabMeta = tab.getItemMeta();
		tabMeta.setDisplayName((game.tabShowsHealthColor() ? "§a" : "§c")
				+ "Tab health color");
		tab.setItemMeta(tabMeta);

		ItemStack rr = new ItemStack(Material.PAINTING);
		ItemMeta rrMeta = rr.getItemMeta();
		rrMeta.setDisplayName((game.isRecordedRound() ? "§a" : "§c")
				+ "Recorded Round");
		rr.setItemMeta(rrMeta);

		ItemStack nether = new ItemStack(Material.NETHER_STALK);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName((game.nether() ? "§a" : "§c") + "Nether");
		nether.setItemMeta(netherMeta);

		ItemStack end = new ItemStack(Material.ENDER_PORTAL_FRAME);
		ItemMeta endMeta = end.getItemMeta();
		endMeta.setDisplayName((game.theEnd() ? "§a" : "§c") + "The End");
		end.setItemMeta(endMeta);

		ItemStack strip = new ItemStack(Material.DIAMOND_PICKAXE);
		ItemMeta stripMeta = strip.getItemMeta();
		stripMeta.setDisplayName((game.antiStripmine() ? "§a" : "§c")
				+ "Anti Stripmine");
		stripMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		strip.setItemMeta(stripMeta);

		ItemStack death = new ItemStack(Material.BLAZE_ROD);
		ItemMeta deathMeta = death.getItemMeta();
		deathMeta.setDisplayName((game.deathLightning() ? "§a" : "§c")
				+ "Death Lightning");
		death.setItemMeta(deathMeta);

		ItemStack horse = new ItemStack(Material.SADDLE);
		ItemMeta horseMeta = horse.getItemMeta();
		horseMeta.setDisplayName((game.horses() ? "§a" : "§c") + "Horses");
		horse.setItemMeta(horseMeta);

		ItemStack armor = new ItemStack(Material.IRON_BARDING);
		ItemMeta armorMeta = armor.getItemMeta();
		armorMeta.setDisplayName((game.horseArmor() ? "§a" : "§c")
				+ "Horse Armor");
		armor.setItemMeta(armorMeta);

		ItemStack healing = new ItemStack(Material.BREAD);
		ItemMeta healingMeta = healing.getItemMeta();
		healingMeta.setDisplayName((game.horseHealing() ? "§a" : "§c")
				+ "Horse Healing");
		healing.setItemMeta(healingMeta);

		ItemStack ghast = new ItemStack(Material.GHAST_TEAR);
		ItemMeta ghastMeta = ghast.getItemMeta();
		ghastMeta.setDisplayName((game.ghastDropGold() ? "§a" : "§c")
				+ "Ghast drop gold");
		ghast.setItemMeta(ghastMeta);

		ItemStack melon = new ItemStack(Material.SPECKLED_MELON);
		ItemMeta melonMeta = melon.getItemMeta();
		melonMeta.setDisplayName((game.goldenMelonNeedsIngots() ? "§a" : "§c")
				+ "Golden Melon needs ingots");
		melon.setItemMeta(melonMeta);

		ItemStack shears = new ItemStack(Material.SHEARS);
		ItemMeta shearsMeta = shears.getItemMeta();
		shearsMeta.setDisplayName((game.shears() ? "§a" : "§c") + "Shears");
		shears.setItemMeta(shearsMeta);

		ItemStack terrain = new ItemStack(Material.STONE, 1, (short) 1);
		ItemMeta terrainMeta = terrain.getItemMeta();
		terrainMeta.setDisplayName((game.newStone() ? "§a" : "§c")
				+ "1.8 Stone");
		terrain.setItemMeta(terrainMeta);

		ItemStack bookshelves = new ItemStack(Material.BOOKSHELF);
		ItemMeta bookMeta = bookshelves.getItemMeta();
		bookMeta.setDisplayName((game.bookshelves() ? "§a" : "§c")
				+ "Bookshelves");
		bookshelves.setItemMeta(bookMeta);

		ItemStack tier2 = new ItemStack(Material.GLOWSTONE_DUST);
		ItemMeta tier2Meta = tier2.getItemMeta();
		tier2Meta.setDisplayName((game.tier2() ? "§a" : "§c") + "Tier 2");
		tier2.setItemMeta(tier2Meta);

		ItemStack splash = new ItemStack(Material.POTION, 1, (short) 16424);
		ItemMeta splashMeta = splash.getItemMeta();
		splashMeta.setDisplayName((game.splash() ? "§a" : "§c") + "Splash");
		splash.setItemMeta(splashMeta);

		ItemStack str = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta strMeta = str.getItemMeta();
		strMeta.setDisplayName((game.strength() ? "§a" : "§c") + "Strength");
		str.setItemMeta(strMeta);

		ItemStack nerf = new ItemStack(Material.POTION, 1, (short) 8233);
		ItemMeta nerfMeta = nerf.getItemMeta();
		nerfMeta.setDisplayName((game.nerfedStrength() ? "§a" : "§c")
				+ "Nerfed Strength");
		nerf.setItemMeta(nerfMeta);
		

		inv.setItem(0, absorption);
		inv.setItem(1, heads);
		inv.setItem(3, notchApples);
		inv.setItem(5, hearts);
		inv.setItem(6, hardcore);
		inv.setItem(7, tab);
		inv.setItem(8, rr);
		inv.setItem(18, nether);
		inv.setItem(19, end);
		inv.setItem(21, strip);
		inv.setItem(22, death);
		inv.setItem(24, horse);
		inv.setItem(25, armor);
		inv.setItem(26, healing);
		inv.setItem(43, ghast);
		inv.setItem(44, melon);
		inv.setItem(45, shears);
		inv.setItem(46, terrain);
		inv.setItem(47, bookshelves);
		inv.setItem(50, tier2);
		inv.setItem(51, splash);
		inv.setItem(52, str);
		inv.setItem(53, nerf);

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() == null) {
			return;
		}

		BoardManager board = BoardManager.getInstance();
		Game game = Game.getInstance();

		Inventory inv = event.getInventory();
		ItemStack item = event.getCurrentItem();

		if (!inv.getTitle().equals(this.inv.getTitle())) {
			return;
		}

		event.setCancelled(true);

		if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}

		String name = item.getItemMeta().getDisplayName().substring(2);

		if (name.equalsIgnoreCase("Absorption")) {
			if (game.absorption()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Absorption has been disabled.");
				game.setAbsorption(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Absorption has been enabled.");
				game.setAbsorption(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Golden Heads")) {
			if (game.goldenHeads()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Golden Heads has been disabled.");
				game.setGoldenHeads(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Golden Heads has been enabled.");
				game.setGoldenHeads(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Notch Apples")) {
			if (game.notchApples()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Notch Apples has been disabled.");
				game.setNotchApples(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Notch Apples has been enabled.");
				game.setNotchApples(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Hearts on tab")) {
			if (game.heartsOnTab()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Tab will now show % health.");
				game.setHeartsOnTab(false);

				board.tabHealth.setDisplaySlot(DisplaySlot.PLAYER_LIST);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils
						.broadcast(Main.PREFIX + "Tab will now show hearts.");
				game.setHeartsOnTab(true);

				board.hearts.setDisplaySlot(DisplaySlot.PLAYER_LIST);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Hardcore Hearts")) {
			if (game.hardcoreHearts()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Hardcore Hearts has been disabled.");
				game.setHardcoreHearts(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Hardcore Hearts has been enabled.");
				game.setHardcoreHearts(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Tab health color")) {
			if (game.tabShowsHealthColor()) {
				PlayerUtils
						.broadcast(Main.PREFIX
								+ "The tab list will no longer have the color of your health.");
				game.setTabShowsHealthColor(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils
						.broadcast(Main.PREFIX
								+ "The tab list will now have the color of your health.");
				game.setTabShowsHealthColor(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Recorded Round")) {
			if (game.isRecordedRound()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "The server is no longer in recorded round mode.");
				game.setRecordedRound(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "The server is now in recorded round mode.");
				game.setRecordedRound(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Nether")) {
			if (game.nether()) {
				PlayerUtils
						.broadcast(Main.PREFIX + "Nether has been disabled.");
				game.setNether(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Nether has been enabled.");
				game.setNether(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("The End")) {
			if (game.theEnd()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "The End has been disabled.");
				game.setTheEnd(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils
						.broadcast(Main.PREFIX + "The End has been enabled.");
				game.setTheEnd(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Anti Stripmine")) {
			if (game.antiStripmine()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Stripmining is no longer allowed.");
				game.setAntiStripmine(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Stripmining is now allowed.");
				game.setAntiStripmine(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Death Lightning")) {
			if (game.deathLightning()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Death Lightning has been disabled.");
				game.setDeathLightning(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Death Lightning has been enabled.");
				game.setDeathLightning(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Horses")) {
			if (game.horses()) {
				PlayerUtils
						.broadcast(Main.PREFIX + "Horses has been disabled.");
				game.setHorses(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Horses has been enabled.");
				game.setHorses(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Horse Armor")) {
			if (game.horseArmor()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Horse Armor has been disabled.");
				game.setHorseArmor(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Horse Armor has been enabled.");
				game.setHorseArmor(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Horse Healing")) {
			if (game.horseHealing()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Horse Healing has been disabled.");
				game.setHorseHealing(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Horse Healing has been enabled.");
				game.setHorseHealing(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Shears")) {
			if (game.shears()) {
				PlayerUtils
						.broadcast(Main.PREFIX + "Shears has been disabled.");
				game.setShears(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Shears has been enabled.");
				game.setShears(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Bookshelves")) {
			if (game.bookshelves()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Bookshelves has been disabled.");
				game.setBookshelves(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Bookshelves has been enabled.");
				game.setBookshelves(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("1.8 Stone")) {
			if (game.newStone()) {
				PlayerUtils
						.broadcast(Main.PREFIX
								+ "The world will no longer have Granite, Diorite and Andesite.");
				game.setNewStone(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils
						.broadcast(Main.PREFIX
								+ "The world will now have Granite, Diorite and Andesite.");
				game.setNewStone(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Ghast drop gold")) {
			if (game.ghastDropGold()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Ghasts will now drop ghast tears.");
				game.setGhastDropGold(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Ghasts will now drop gold ingots.");
				game.setGhastDropGold(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Golden Melon needs ingots")) {
			if (game.goldenMelonNeedsIngots()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Golden Melons now require nuggets.");
				game.setGoldenMelonNeedsIngots(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Golden Melons now require ingots.");
				game.setGoldenMelonNeedsIngots(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Tier 2")) {
			if (game.tier2()) {
				PlayerUtils
						.broadcast(Main.PREFIX + "Tier 2 has been disabled.");
				game.setTier2(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Tier 2 has been enabled.");
				game.setTier2(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Splash")) {
			if (game.splash()) {
				PlayerUtils
						.broadcast(Main.PREFIX + "Splash has been disabled.");
				game.setSplash(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Splash has been enabled.");
				game.setSplash(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Strength")) {
			if (game.strength()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Strength has been disabled.");
				game.setStrength(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Strength has been enabled.");
				game.setStrength(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
			return;
		}

		if (name.equalsIgnoreCase("Nerfed Strength")) {
			if (game.nerfedStrength()) {
				PlayerUtils.broadcast(Main.PREFIX
						+ "Strength is no longer nerfed.");
				game.setNerfedStrength(false);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c" + name);
				item.setItemMeta(meta);
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Strength is now nerfed.");
				game.setNerfedStrength(true);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + name);
				item.setItemMeta(meta);
			}
		}
	}
}