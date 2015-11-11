package com.leontg77.uhc.scenario.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.BlockUtils;

/**
 * VengefulSpirits scenario class
 * 
 * @author LeonTG77
 */
public class VengefulSpirits extends Scenario implements Listener {
	private static final String PREFIX = "§fThe Spirit of ";
	
	public VengefulSpirits() {
		super("VengefulSpirits", "When a player dies, above y 60 a ghast spawns and bellow y 60 a blaze spawns, you can only get their head by killing that mob.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {
		// I don't want the code to place the normal skulls so then
		// I disable them instead since I have a custom dropping
		Game game = Game.getInstance();
		game.setGoldenHeads(false);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		// check if the entity is a blaze or a ghast, if not return.
		if (!(entity instanceof Blaze) && !(entity instanceof Ghast)) {
			return;
		}
		
		// if the entity has no name or it doesn't start with the prefix, return.
		if (entity.getCustomName() == null || !entity.getCustomName().startsWith(PREFIX)) {
			return;
		}
		
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(entity.getCustomName().substring(PREFIX.length())); // Set the owner to the name, but remove the prefix from it.
		skull.setItemMeta(skullMeta);
		
		// drop the created item.
		BlockUtils.dropItem(entity.getLocation(), skull);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		Location loc = player.getLocation();
		World world = player.getWorld();
		
		// if the player is below y=60 we want to spawn a blaze.
		if (loc.getBlockY() < 60) {
			// spawn the blaze and name it.
			Blaze blaze = world.spawn(loc, Blaze.class);
			blaze.setCustomName(PREFIX + player.getName());
			return;
		} 

		// they're above y=60, spawn a ghast and name it.
		Ghast ghast = world.spawn(loc, Ghast.class);
		ghast.setCustomName(PREFIX + player.getName());
	}
}