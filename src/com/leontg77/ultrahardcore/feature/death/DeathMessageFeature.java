package com.leontg77.ultrahardcore.feature.death;

import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Death Messages feature class.
 * 
 * @author LeonTG77
 */
public class DeathMessageFeature extends Feature implements Listener {

	public DeathMessageFeature() {
		super("Death Messages", "Modified death messages.");
	}

	@EventHandler
	public void on(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final Arena arena = Arena.getInstance();
		
		// the arena has it's own way of doing deaths.
		if (arena.isEnabled() && arena.hasPlayer(player)) {
			return;
		} 

		final List<World> worlds = game.getWorlds();
		
	    String deathMessage = event.getDeathMessage();
	    event.setDeathMessage(null);
	    
	    // I don't care about the rest if it hasn't started or they're not in a game world.
	    if (!State.isState(State.INGAME) || !worlds.contains(player.getWorld())) {
	    	return;
	    }

		final Player killer = player.getKiller();
		
		if (deathMessage == null || deathMessage.isEmpty()) {
			return;
		}

		if (killer == null) {
			PlayerUtils.broadcast("§8» §f" + deathMessage);
			return;
		}
		
		final ItemStack item = killer.getItemInHand();
		
		final double distance = killer.getLocation().distance(player.getLocation());
		final String shotDistance = " §8(§6" + NumberUtils.formatDouble(distance) + " §7blocks§8)";
		
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !deathMessage.contains(killer.getName()) || (!deathMessage.contains("slain") && !deathMessage.contains("shot"))) {
			if (deathMessage.contains("shot")) {
				deathMessage += shotDistance;
			}
			
			PlayerUtils.broadcast("§8» §f" + deathMessage);	
			return;
		}
			
		final String name = item.getItemMeta().getDisplayName();
		
		final ComponentBuilder builder = new ComponentBuilder("§8» §f" + deathMessage.replace("[" + name + "]", ""));
		final String color = killer.getItemInHand().getEnchantments().isEmpty() ? "§f" : "§b";

		final StringBuilder colored = new StringBuilder();
		
		for (String entry : name.split(" ")) {
			colored.append(color + "§o" + entry).append(" ");
		}
		
		builder.append(color + "[" + colored.toString().trim() + color + "]");
		builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] {new TextComponent( convertToJson(item)) }));

		if (deathMessage.contains("shot")) {
			builder.append(shotDistance, FormatRetention.NONE);
		}
		
		final BaseComponent[] result = builder.create();

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.spigot().sendMessage(result);
		}
		
		Bukkit.getLogger().info("§8» §f" + deathMessage);
	}
	
	/**
	* Converts an {@link ItemStack} to a Json string
	* for sending with {@link BaseComponent}'s.
	*
	* @param itemStack the item to convert
	* @return the Json string representation of the item
	*/
	public static String convertToJson(ItemStack stack) {
		net.minecraft.server.v1_8_R3.ItemStack item = CraftItemStack.asNMSCopy(stack);
		final NBTTagCompound tag = item.save(new NBTTagCompound());
		
		return tag.toString();
	}
}