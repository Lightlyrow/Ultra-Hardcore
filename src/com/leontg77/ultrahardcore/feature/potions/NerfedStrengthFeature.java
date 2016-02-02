package com.leontg77.ultrahardcore.feature.potions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Nerfed Strength feature class.
 * 
 * @author LeonTG77
 */
public class NerfedStrengthFeature extends Feature implements Listener {

	public NerfedStrengthFeature() {
		super("Nerfed Strength", "Make strength do the same damage as in minecraft 1.5");
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		final Entity damager = event.getDamager();
		
		if (!(damager instanceof Player)) {
			return;
		}
		
		final Player player = (Player) damager;
		
		if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			return;
		}
		
		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (!effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				continue;
			}
			
			int level = effect.getAmplifier() + 1;

			double newDamage = event.getDamage(DamageModifier.BASE) / (level * 1.3D + 1.0D) + 6 * level;
			double damagePercent = newDamage / event.getDamage(DamageModifier.BASE);
			
			try {
				event.setDamage(DamageModifier.ARMOR, event.getDamage(DamageModifier.ARMOR) * damagePercent);
			} catch (Exception e) {}
			
			try {
				event.setDamage(DamageModifier.MAGIC, event.getDamage(DamageModifier.MAGIC) * damagePercent);
			} catch (Exception e) {}
			
			try {
				event.setDamage(DamageModifier.RESISTANCE, event.getDamage(DamageModifier.RESISTANCE) * damagePercent);
			} catch (Exception e) {}
			
			try {
				event.setDamage(DamageModifier.BLOCKING, event.getDamage(DamageModifier.BLOCKING) * damagePercent);
			} catch (Exception e) {}
			
			event.setDamage(DamageModifier.BASE, newDamage);
			break;
		}
	}
}