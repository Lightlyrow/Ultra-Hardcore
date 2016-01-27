package com.leontg77.ultrahardcore.feature.toggleable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.utils.BlockUtils;

public class GoldenHeadsFeature extends ToggleableFeature implements Listener {
	private static final ItemStack PLAYER_SKULL = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	private static final String HEAD_NAME = ChatColor.GOLD + "Golden Head";
    
	private static final int TICKS_PER_HALF_HEART = 25;
	private static final short PLAYER_HEAD_DATA = 3;
	
    private int healAmount;

	public GoldenHeadsFeature() {
		super("Golden Heads", "", new ItemStack(Material.SKULL_ITEM, 1, PLAYER_HEAD_DATA), 2);

		ShapedRecipe modified = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1))
        	.shape("AAA", "ABA", "AAA")
        	.setIngredient('A', Material.GOLD_INGOT)
        	.setIngredient('B', PLAYER_SKULL.getData());

		Bukkit.addRecipe(modified);	
	}

	/**
	 * Set the amount of hearts heads should heal.
	 * 
	 * @param amount The amount of hearts.
	 */
    public void setHealAmount(int amount) {
        this.healAmount = amount;
        
        settings.getConfig().set("feature." + getName().toLowerCase() + ".heal amount", healAmount);
        settings.saveConfig();
    }

    /**
     * Get the amount of hearts heads heal.
     * 
     * @return Heal amount.
     */
    public int getHealAmount() {
        return healAmount;
    }

	@EventHandler
	public void on(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		
		// incase an explotion, wait a tick.
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				Block block = player.getLocation().getBlock();
				
				block.setType(Material.NETHER_FENCE);
				block = block.getRelative(BlockFace.UP);
				block.setType(Material.SKULL);
			    
				Skull skull;
				
				try {
			        skull = (Skull) block.getState();
				} catch (Exception e) {
					// the skull wasn't placed (outside of the world probs), tell the console so and stop.
					Bukkit.getLogger().warning("Could not place player skull.");
					return;
				}
				
			    skull.setSkullType(SkullType.PLAYER);
			    skull.setRotation(BlockUtils.getBlockDirection(player.getLocation()));
			    skull.setOwner(player.getName());
			    skull.update();
			    
			    block.setData((byte) 0x1, true);
			}
		}.runTaskLater(Main.plugin, 1);
	}

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if(isEnabled() && isGoldenHead(event.getItem())) {
            event.getPlayer().addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION,
                    TICKS_PER_HALF_HEART * healAmount,
                    1
            ));
        }
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult().getType() != Material.GOLDEN_APPLE) return;

        ItemStack centre = event.getInventory().getMatrix()[4];

        if (centre == null || centre.getType() != Material.SKULL_ITEM) return;

        if (!isEnabled()) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }

        SkullMeta meta = (SkullMeta) centre.getItemMeta();
        event.getInventory().setResult(getGoldenHeadItem(meta.hasOwner() ? meta.getOwner() : "Manually Crafted"));
    }

    public ItemStack getGoldenHeadItem(String name) {
    	final ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE, 1);
        final ItemMeta meta = itemStack.getItemMeta();
        
        meta.setDisplayName(HEAD_NAME);

        // add lore
        meta.setLore(ImmutableList.of(
                "Some say consuming the head of a",
                "fallen foe strengthens the blood",
                ChatColor.AQUA + "Made from the head of: " + name
        ));

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public boolean isGoldenHead(ItemStack item) {
        if (item.getType() != Material.GOLDEN_APPLE) {
        	return false;
        }

        final ItemMeta meta = item.getItemMeta();

        return meta.hasLore() && meta.hasDisplayName() && meta.getDisplayName().equals(HEAD_NAME);
    }
}