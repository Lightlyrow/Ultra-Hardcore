package com.leontg77.ultrahardcore.feature.enchants;

import org.bukkit.Material;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.protocol.EnchantPreview;

/**
 * Enchant Preview feature class.
 * 
 * @author LeonTG77
 */
public class EnchantmentPreviewFeature extends ToggleableFeature {

	public EnchantmentPreviewFeature() {
		super("Enchant Preview", "A preview of 1 of the enchants you'll get on your enchanting weapon.");
		
		icon.setType(Material.ENCHANTMENT_TABLE);
		slot = 47;
	}

	@Override
	public void onDisable() {
		EnchantPreview.enable();
	}
	
	@Override
	public void onEnable() {
		EnchantPreview.disable();
	}
}