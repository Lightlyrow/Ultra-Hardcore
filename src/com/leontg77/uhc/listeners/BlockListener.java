package com.leontg77.uhc.listeners;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.utils.BlockUtils;

/**
 * Block listener class.
 * <p> 
 * Contains all eventhandlers for block releated events.
 * 
 * @author LeonTG77
 */
public class BlockListener implements Listener {
	private Game game = Game.getInstance();
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	final Player player = event.getPlayer();
		final Block block = event.getBlock();
    	
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (block.getType() == Material.QUARTZ_ORE) {
			event.setExpToDrop(event.getExpToDrop() / 2);
			return;
		}
		
		if (block.getType() == Material.DIAMOND_ORE) {
			User user = User.get(player);
			user.increaseStat(Stat.DIAMONDS);
			return;
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			User user = User.get(player);
			user.increaseStat(Stat.GOLD);
			return;
		}
		
		if (block.getType() == Material.GLOWSTONE) {
			if (game.tier2()) {
				return;
			}
            
            BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
            BlockUtils.dropItem(block.getLocation(), new ItemStack(Material.GLOWSTONE));
			
			event.setCancelled(true);
            block.setType(Material.AIR);
			return;
		}
    	
		final Random rand = new Random();
		
		if (block.getType() == Material.GRAVEL) {
			BlockUtils.blockBreak(player, block);
            BlockUtils.degradeDurabiliy(player);
            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(rand.nextInt(99) < game.getFlintRates() ? Material.FLINT : Material.GRAVEL));
			
			event.setCancelled(true);
            block.setType(Material.AIR);
			return;
		}
		
		if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
			return;
		}
		
		short damage = block.getState().getData().toItemStack().getDurability();
		
		BlockUtils.blockBreak(player, block);
		BlockUtils.degradeDurabiliy(player);
		
		event.setCancelled(true);
		block.setType(Material.AIR);
		
		if (block.getType() == Material.LEAVES) {
			if (rand.nextInt(99) < 5) {
				BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES, damage));
			}
			
			if (damage != 0 && damage != 4 && damage != 8 && damage != 12) {
				return;
			}

			ItemStack hand = player.getItemInHand();

			if (game.shears() && hand != null && hand.getType() == Material.SHEARS) {
				if (rand.nextInt(99) >= game.getShearRates()) {
					return;
				}

	            BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
				return;
			} 
			
			if (rand.nextInt(99) >= game.getAppleRates()) {
				return;
			}

			BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
			return;
		}
		
		if (block.getType() == Material.LEAVES_2) {
			if (rand.nextInt(99) < 5) {
				BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES_2, damage));
			}
			
			if (damage != 1 && damage != 5 && damage != 9 && damage != 13) {
				return;
			}

			ItemStack hand = player.getItemInHand();
			
			if (game.shears() && hand != null && hand.getType() == Material.SHEARS) {
				if (rand.nextInt(99) >= game.getShearRates()) {
					return;
				}

				BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
				return;
			} 
				
			if (rand.nextInt(99) >= game.getAppleRates()) {
				return;
			}

			BlockUtils.dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
		}
    }

	@EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
		Block block = event.getBlock();
    	
    	if (block.getWorld().getName().equals("lobby")) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (block.getType() != Material.LEAVES && block.getType() != Material.LEAVES_2) {
			return;
		}
    	
    	Location loc = block.getLocation();
    	Material type = block.getType();
    	
		short damage = block.getState().getData().toItemStack().getDurability();
		Random rand = new Random();

    	event.setCancelled(true);
    	block.setType(Material.AIR);
		
		if (type == Material.LEAVES) {
			if (rand.nextInt(99) < 5) {
	            BlockUtils.dropItem(loc.clone().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES, damage));
			}
			
			if (damage != 0 && damage != 4 && damage != 8 && damage != 12) {
				return;
			}

			if (rand.nextInt(99) >= game.getAppleRates()) {
				return;
			}

			BlockUtils.dropItem(loc.clone().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
			return;
		}
		
		if (type == Material.LEAVES_2) {
			if (rand.nextInt(99) < 5) {
				BlockUtils.dropItem(loc.clone().add(0.5, 0.7, 0.5), BlockUtils.getSaplingFor(Material.LEAVES_2, damage));
			}
			
			if (damage != 1 && damage != 5 && damage != 9 && damage != 13) {
				return;
			}

			if (rand.nextInt(99) >= game.getAppleRates()) {
				return;
			}

			BlockUtils.dropItem(loc.clone().add(0.5, 0.7, 0.5), new ItemStack(Material.APPLE, 1));
		}
    }
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		
		if (!isADoor(block.getType())) {
			return;
		}
		
		block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
	}
	
	private boolean isADoor(Material type) {
		switch (type) {
		case ACACIA_DOOR:
		case BIRCH_DOOR:
		case DARK_OAK_DOOR:
		case JUNGLE_DOOR:
		case SPRUCE_DOOR:
		case WOODEN_DOOR:
			return true;
		default:
			return false;
		}
	}
}