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

import com.leontg77.ultrahardcore.Game;

public class Config extends InvGUI implements Listener {
	private Inventory inv = Bukkit.createInventory(null, 54, "» §7Game configuration");
	
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
		
		ItemStack absorption = new ItemStack (Material.GOLDEN_APPLE);
		ItemMeta absorptionMeta = absorption.getItemMeta();
		absorptionMeta.setDisplayName((game.absorption() ? "§a" : "§c") + "Absorption");
		absorption.setItemMeta(absorptionMeta);
		inv.setItem(0, absorption);
		
		ItemStack heads = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headsMeta = heads.getItemMeta();
		headsMeta.setDisplayName((game.goldenHeads() ? "§a" : "§c") + "Golden Heads");
		heads.setItemMeta(headsMeta);
		inv.setItem(1, heads);
		
		ItemStack notchApples = new ItemStack (Material.GOLDEN_APPLE, 1, (short) 1);
		ItemMeta notchMeta = notchApples.getItemMeta();
		notchMeta.setDisplayName((game.notchApples() ? "§a" : "§c") + "Notch Apples");
		notchApples.setItemMeta(notchMeta);
		inv.setItem(3, notchApples);
		
		ItemStack hearts = new ItemStack (Material.INK_SACK, 1, (short) 1);
		ItemMeta heartsMeta = hearts.getItemMeta();
		heartsMeta.setDisplayName((game.heartsOnTab() ? "§a" : "§c") + "Hearts on tab");
		hearts.setItemMeta(heartsMeta);
		inv.setItem(5, hearts);
		
		ItemStack hardcore = new ItemStack (Material.REDSTONE);
		ItemMeta hardcoreMeta = hardcore.getItemMeta();
		hardcoreMeta.setDisplayName((game.hardcoreHearts() ? "§a" : "§c") + "Hardcore Hearts");
		hardcore.setItemMeta(hardcoreMeta);
		inv.setItem(6, hardcore);
		
		ItemStack tab = new ItemStack (Material.SIGN);
		ItemMeta tabMeta = tab.getItemMeta();
		tabMeta.setDisplayName((game.tabShowsHealthColor() ? "§a" : "§c") + "Tab health color");
		tab.setItemMeta(tabMeta);
		inv.setItem(7, tab);
		
		ItemStack rr = new ItemStack (Material.PAINTING);
		ItemMeta rrMeta = rr.getItemMeta();
		rrMeta.setDisplayName((game.isRecordedRound() ? "§a" : "§c") + "Recorded Round");
		rr.setItemMeta(rrMeta);
		inv.setItem(8, rr);
		
		ItemStack nether = new ItemStack (Material.NETHER_STALK);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName((game.nether() ? "§a" : "§c") + "Nether");
		nether.setItemMeta(netherMeta);
		inv.setItem(18, nether);
		
		ItemStack end = new ItemStack (Material.ENDER_PORTAL_FRAME);
		ItemMeta endMeta = end.getItemMeta();
		endMeta.setDisplayName((game.theEnd() ? "§a" : "§c") + "The End");
		end.setItemMeta(endMeta);
		inv.setItem(19, end);
		
		ItemStack strip = new ItemStack (Material.DIAMOND_PICKAXE);
		ItemMeta stripMeta = strip.getItemMeta();
		stripMeta.setDisplayName((game.antiStripmine() ? "§a" : "§c") + "Anti Stripmine");
		stripMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		strip.setItemMeta(stripMeta);
		inv.setItem(21, strip);
		
		ItemStack death = new ItemStack (Material.BLAZE_ROD);
		ItemMeta deathMeta = death.getItemMeta();
		deathMeta.setDisplayName((game.deathLightning() ? "§a" : "§c") + "Death Lightning");
		death.setItemMeta(deathMeta);
		inv.setItem(22, death);
		
		ItemStack horse = new ItemStack (Material.SADDLE);
		ItemMeta horseMeta = horse.getItemMeta();
		horseMeta.setDisplayName((game.horses() ? "§a" : "§c") + "Horses");
		horse.setItemMeta(horseMeta);
		inv.setItem(24, horse);
		
		ItemStack armor = new ItemStack (Material.IRON_BARDING);
		ItemMeta armorMeta = armor.getItemMeta();
		armorMeta.setDisplayName((game.horseArmor() ? "§a" : "§c") + "Horse Armor");
		armor.setItemMeta(armorMeta);
		inv.setItem(25, armor);
		
		ItemStack healing = new ItemStack (Material.BREAD);
		ItemMeta healingMeta = healing.getItemMeta();
		healingMeta.setDisplayName((game.horseHealing() ? "§a" : "§c") + "Horse Healing");
		healing.setItemMeta(healingMeta);
		inv.setItem(26, healing);
		
		ItemStack ghast = new ItemStack (Material.GHAST_TEAR);
		ItemMeta ghastMeta = ghast.getItemMeta();
		ghastMeta.setDisplayName((game.ghastDropGold() ? "§a" : "§c") + "Ghast drop gold");
		ghast.setItemMeta(ghastMeta);
		inv.setItem(43, ghast);
		
		ItemStack melon = new ItemStack (Material.SPECKLED_MELON);
		ItemMeta melonMeta = melon.getItemMeta();
		melonMeta.setDisplayName((game.goldenMelonNeedsIngots() ? "§a" : "§c") + "Golden Melon needs ingots");
		melon.setItemMeta(melonMeta);
		inv.setItem(44, melon);
		
		ItemStack shears = new ItemStack (Material.SHEARS);
		ItemMeta shearsMeta = shears.getItemMeta();
		shearsMeta.setDisplayName((game.shears() ? "§a" : "§c") + "Shears");
		shears.setItemMeta(shearsMeta);
		inv.setItem(45, shears);
		
		ItemStack terrain = new ItemStack (Material.STONE, 1, (short) 1);
		ItemMeta terrainMeta = terrain.getItemMeta();
		terrainMeta.setDisplayName((game.newStone() ? "§a" : "§c") + "1.8 Stone");
		terrain.setItemMeta(terrainMeta);
		inv.setItem(46, terrain);
		
		ItemStack bookshelves = new ItemStack (Material.BOOKSHELF);
		ItemMeta bookMeta = bookshelves.getItemMeta();
		bookMeta.setDisplayName((game.bookshelves() ? "§a" : "§c") + "Bookshelves");
		bookshelves.setItemMeta(bookMeta);
		inv.setItem(47, bookshelves);
		
		ItemStack tier2 = new ItemStack (Material.GLOWSTONE_DUST);
		ItemMeta tier2Meta = tier2.getItemMeta();
		tier2Meta.setDisplayName((game.tier2() ? "§a" : "§c") + "Tier 2");
		tier2.setItemMeta(tier2Meta);
		inv.setItem(50, tier2);
		
		ItemStack splash = new ItemStack (Material.POTION, 1, (short) 16424);
		ItemMeta splashMeta = splash.getItemMeta();
		splashMeta.setDisplayName((game.splash() ? "§a" : "§c") + "Splash");
		splash.setItemMeta(splashMeta);
		inv.setItem(51, splash);
		
		ItemStack str = new ItemStack (Material.BLAZE_POWDER);
		ItemMeta strMeta = str.getItemMeta();
		strMeta.setDisplayName((game.strength() ? "§a" : "§c") + "Strength");
		str.setItemMeta(strMeta);
		inv.setItem(52, str);
		
		ItemStack nerf = new ItemStack (Material.POTION, 1, (short) 8233);
		ItemMeta nerfMeta = nerf.getItemMeta();
		nerfMeta.setDisplayName((game.nerfedStrength() ? "§a" : "§c") + "Nerfed Strength");
		nerf.setItemMeta(nerfMeta);
		inv.setItem(53, nerf);
	}
}