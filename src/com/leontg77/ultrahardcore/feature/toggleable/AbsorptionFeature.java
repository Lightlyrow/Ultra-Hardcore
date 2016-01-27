package com.leontg77.ultrahardcore.feature.toggleable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Absorption feature class.
 * 
 * @author LeonTG77
 */
public class AbsorptionFeature extends ToggleableFeature implements Listener {

	public AbsorptionFeature() {
		super("Absorption", "Golden apples gives absorption when consumed.", new ItemStack(Material.GOLDEN_APPLE), 1);
	}
	
	@EventHandler
	public void on(PlayerItemConsumeEvent event) {
		if (isEnabled()) {
			return;
		}
		
		final Player player = event.getPlayer();
		final ItemStack item = event.getItem();
		
		if (item.getType() != Material.GOLDEN_APPLE) {
			return;
		}
		
		new BukkitRunnable() {
			public void run() {
				player.removePotionEffect(PotionEffectType.ABSORPTION);
			}
        }.runTask(Main.plugin);
	}
}