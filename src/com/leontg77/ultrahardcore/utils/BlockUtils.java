package com.leontg77.ultrahardcore.utils;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.leontg77.ultrahardcore.Main;

/**
 * Block utilities class.
 * <p>
 * Contains block related methods.
 * 
 * @author LeonTG77
 */
public class BlockUtils {
	
	/**
	 * Display the block breaking sound and particles.
	 * 
	 * @param player The player who mined the block.
	 * @param block The block that was broken.
	 */
	@SuppressWarnings("deprecation")
	public static void blockBreak(Player player, Block block) {
		// Loop all players that are in the world.
		for (Player worldPlayer : block.getWorld().getPlayers()) {
			// we do not want to display it to the breaker himself, that is done automaticly.
        	if (player != null && worldPlayer == player) {
        		continue;
        	}
        	
        	// play the effect STEP_SOUND with the given block id at the blocks location.
        	worldPlayer.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
        }
	}
	
	/**
	 * Drop the given item to drop on the given location as if a normal block break would drop it.
	 * 
	 * @param loc The location dropping at.
	 * @param toDrop The item dropping.
	 */
	public static void dropItem(final Location loc, final ItemStack toDrop) {
		// wait 2 ticks before dropping the item, because then the block won't push the item.
		new BukkitRunnable() {
        	public void run() {
        		// spawn item.
    			Item item = loc.getWorld().dropItem(loc, toDrop);
    			item.setVelocity(randomOffset());
        	}
        }.runTaskLater(Main.plugin, 2);
	}

	/**
	 * Make the given players item in hand lose 1 durability 
	 * point and break if out of durability.
	 * 
	 * @param player The item owner.
	 */
	public static void degradeDurabiliy(Player player) {
		ItemStack newItem = player.getItemInHand();
        
		// if the item is air, a bow or it's max durability is 0 (only weapons/tools doesn't have it as 0) return.
        if (newItem.getType() == Material.AIR || newItem.getType() == Material.BOW || newItem.getType().getMaxDurability() == 0) {
            return;
        }
        
        short durability = newItem.getDurability();
        durability++;
        
        // if theres no more durability left, play the break sound, remove the item and return.
        if (durability >= newItem.getType().getMaxDurability()) {
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            player.setItemInHand(new ItemStack(Material.AIR));
            return;
        }
        
        // set the new durability and update their item in hand.
        newItem.setDurability(durability);
        player.setItemInHand(newItem);
	}

	/**
	 * Get a random offset for item dropping.
	 * 
	 * @return A vector with a random offset.
	 */
	private static Vector randomOffset() {
		Random rand = new Random();
		
		// don't ask me for why these numbers, I was just testing different onces and these seemed to work the best.
		double offsetX = rand.nextDouble() / 20;
		double offsetZ = rand.nextDouble() / 20;

		offsetX = offsetX - (rand.nextDouble() / 20);
		offsetZ = offsetZ - (rand.nextDouble() / 20);
		
		return new Vector(offsetX, 0.2, offsetZ);
	}
	
	/**
	 * Get the durability of the given block.
	 * 
	 * @param block The block checking.
	 * @return The durability of the block.
	 */
	public static int getDurability(Block block) {
		return block.getState().getData().toItemStack().getDurability();
	}
}