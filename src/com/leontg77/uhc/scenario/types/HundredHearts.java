package com.leontg77.uhc.scenario.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * 100Hearts scenario class
 * 
 * @author LeonTG77
 */
public class HundredHearts extends Scenario implements Listener {

	public HundredHearts() {
		super("100Hearts", "Everyone has 100 hearts, golden apples heal 20% of your max health.");
	}

	@Override
	public void onDisable() {
		for (Player online : PlayerUtils.getPlayers()) {
			online.setMaxHealth(20);
		}
	}

	@Override
	public void onEnable() {
		for (Player online : PlayerUtils.getPlayers()) {
			online.setMaxHealth(200);
			online.setHealth(200);
		}
	}

	@EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
    	Player player = event.getPlayer();
    	ItemStack item = event.getItem();

        if (item.getType() != Material.GOLDEN_APPLE) {
        	return;
        }
        
        player.removePotionEffect(PotionEffectType.REGENERATION);

        double ticks = (player.getMaxHealth() / 5) * 25;
        int ticksRounded = (int) ticks;

        double excessHealth = ticks - ticksRounded;

        player.setHealth(player.getHealth() + excessHealth);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticksRounded, 1));
    }
}