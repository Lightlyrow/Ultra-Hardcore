package com.leontg77.ultrahardcore.utils;

import java.lang.reflect.Method;
import java.util.logging.Level;

import net.md_5.bungee.api.chat.BaseComponent;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Name utilities class.
 * <p>
 * Contains name related methods.
 * 
 * @author LeonTG77
 */
public class NameUtils {
	
	/**
	 * Fix the given text with making the first letter captializsed and the rest not.
	 * 
	 * @param text the text fixing.
	 * @param replaceUnderscore True to replace all _ with a space, false otherwise.
	 * @return The new fixed text.
	 */
	public static String capitalizeString(String text, boolean replaceUnderscore) {
		if (text.isEmpty()) {
			return text;
		}
		
		if (text.length() == 1) {
			return text.toUpperCase();
		}
		
		String toReturn = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
		
		if (replaceUnderscore) {
			toReturn = toReturn.replace("_", " ");
		} 
		
		return toReturn;
	}

	/**
	 * Get the real potion name of the given potion type.
	 * 
	 * @param type the type.
	 * @return The real potion name.
	 */
	public static String getPotionName(PotionEffectType type) {
		switch (type.getName().toLowerCase()) {
		case "speed":
			return "Speed";
		case "slow":
			return "Slowness";
		case "fast_digging":
			return "Haste";
		case "slow_digging":
			return "Mining fatigue";
		case "increase_damage":
			return "Strength";
		case "heal":
			return "Instant Health";
		case "harm":
			return "Instant Damage";
		case "jump":
			return "Jump Boost";
		case "confusion":
			return "Nausea";
		case "regeneration":
			return "Regeneration";
		case "damage_resistance":
			return "Resistance";
		case "fire_resistance":
			return "Fire Resistance";
		case "water_breathing":
			return "Water breathing";
		case "invisibility":
			return "Invisibility";
		case "blindness":
			return "Blindness";
		case "night_vision":
			return "Night Vision";
		case "hunger":
			return "Hunger";
		case "weakness":
			return "Weakness";
		case "poison":
			return "Poison";
		case "wither":
			return "Wither";
		case "health_boost":
			return "Health Boost";
		case "absorption":
			return "Absorption";
		case "saturation":
			return "Saturaion";
		default:
			return "?";
		}
	}
	
	/**
	* Converts an {@link ItemStack} to a Json string
	* for sending with {@link BaseComponent}'s.
	*
	* @param itemStack the item to convert
	* @return the Json string representation of the item
	*/
	public static String convertToJson(ItemStack itemStack) {
		Class<?> craftitemstack = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
	    Method method = ReflectionUtils.getMethod(craftitemstack, "asNMSCopy", ItemStack.class);

	    Class<?> itemstack = ReflectionUtils.getNMSClass("ItemStack");
	    Class<?> nbt = ReflectionUtils.getNMSClass("NBTTagCompound");
	    Method save = ReflectionUtils.getMethod(itemstack, "save", nbt);

	    Object nbtInstance; 
	    Object nsmcopy; 
	    Object toReturn; 

	    try {
	        nbtInstance = nbt.newInstance();
	        nsmcopy = method.invoke(null, itemStack);
	        toReturn = save.invoke(nsmcopy, nbtInstance);
	    } catch (Throwable t) {
	        Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
	        return null;
	    }

	    return toReturn.toString();
	}
}