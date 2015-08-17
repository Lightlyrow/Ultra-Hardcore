package com.leontg77.uhc.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Settings;

/**
 * Player utilities class.
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class PlayerUtils {
	
	/**
	 * Get a list of players online.
	 * @return A list of online players.
	 */
	public static List<Player> getPlayers() {
		ArrayList<Player> list = new ArrayList<Player>();
		
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			list.add(online);
		}
		
		return list;
	}
	
	/**
	 * Gets an offline player by a name.
	 * <p>
	 * This is just because of the deprecation on Bukkit.getOfflinePlayer(String) 
	 * @param name The name.
	 * @return the offline player.
	 */
	public static OfflinePlayer getOfflinePlayer(String name) {
		return Bukkit.getServer().getOfflinePlayer(name);
	}
	
	/**
	 * Broadcasts a message to everyone online with a specific permission.
	 * @param message the message.
	 * @param permission the permission.
	 */
	public static void broadcast(String message, String permission) {
		for (Player online : getPlayers()) {
			if (online.hasPermission(permission)) {
				online.sendMessage(message);
			}
		}
		
		Bukkit.getLogger().info(message.replaceAll("�l", ""));
	}
	
	/**
	 * Broadcasts a message to everyone online.
	 * @param message the message.
	 */
	public static void broadcast(String message) {
		for (Player online : getPlayers()) {
			online.sendMessage(message);
		}
		
		Bukkit.getLogger().info(message.replaceAll("�l", ""));
	}

	/**
	 * Get the inv size of # players online.
	 * @return the inv size.
	 */
	public static int playerInvSize() {
		int length = getPlayers().size();
		length = (length - Main.spectating.size());
		
		if (length <= 9) {
			return 9;
		} else if (length > 9 && length <= 18) {
			return 18;
		} else if (length > 18 && length <= 27) {
			return 27;
		} else if (length > 27 && length <= 36) {
			return 36;
		} else if (length > 36 && length <= 45) {
			return 45;
		} else if (length > 45 && length <= 54) {
			return 54;
		}
		
		return 54;
	}
	
	/**
	 * Get a list of entites within a distance of a location.
	 * @param loc the location.
	 * @param distance the distance.
	 * @return A list of entites nearby.
	 */
	public static List<Entity> getNearby(Location loc, int distance) {
		List<Entity> list = new ArrayList<Entity>();
		
		for (Entity e : loc.getWorld().getEntities()) {
			if (e instanceof Player) {
				continue;
			}
			
			if (loc.distance(e.getLocation()) <= distance) {
				list.add(e);
			}
		}
		
		for (Player online : getPlayers()) {
			if (online.getWorld() == loc.getWorld()) {
				if (loc.distance(online.getLocation()) <= distance) {
					list.add(online);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Get a players ping.
	 * @param player the player
	 * @return the players ping
	 */
	public static int getPing(Player player) {
		CraftPlayer craft = (CraftPlayer) player;
		return craft.getHandle().ping;
	}
	
	/**
	 * Sets a tablist for a player.
	 * @param player the player.
	 */
	public static void setTabList(Player player) {
		CraftPlayer craft = (CraftPlayer) player;

        IChatBaseComponent headerJSON = ChatSerializer.a("{text:'Ultra Hardcore',color:'dark_red',bold:'true'}");
        IChatBaseComponent footerJSON = ChatSerializer.a("{text:'" + GameUtils.getTeamSize() + " " + Settings.getInstance().getConfig().getString("game.scenarios") + "',color:'gray'}");

        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(headerJSON);
 
        try {
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, footerJSON);
        }
        catch (Exception e) {
            Bukkit.getServer().getLogger().severe("�cCould not send tab list packets to " + player.getName());
        }
        finally {
            craft.getHandle().playerConnection.sendPacket(headerPacket);
        }
	}
	
	/**
	 * Sends a message in action bar to a player.
	 * @param player the player.
	 * @param msg the message.
	 */
	public static void sendAction(Player player, String msg) {
		CraftPlayer craft = (CraftPlayer) player;

        IChatBaseComponent actionJSON = ChatSerializer.a("{text:'" + msg + "'}");
        PacketPlayOutChat actionPacket = new PacketPlayOutChat(actionJSON, (byte) 2);
        
        craft.getHandle().playerConnection.sendPacket(actionPacket);
	}
	
	/**
	 * Sends a title message to a player.
	 * @param player the player displaying to.
	 * @param title the title.
	 * @param subtitle the subtitle.
	 * @param in how long it uses to fade in.
	 * @param out how long it uses to fade out.
	 * @param stay how long it stays.
	 */
	public static void sendTitle(Player player, String title, String subtitle, int in, int stay, int out) {
        CraftPlayer craft = (CraftPlayer) player;
        
        IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
        IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");
        
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, in, stay, out);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        
        craft.getHandle().playerConnection.sendPacket(titlePacket);
        craft.getHandle().playerConnection.sendPacket(subtitlePacket);
    }

	/**
	 * Handle the permissions for a player.
	 * @param player the player.
	 */
	public static void handlePermissions(Player player) {
		
	}
}