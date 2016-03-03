package com.leontg77.ultrahardcore.utils;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;

/**
 * Packet utilities class.
 * <p>
 * Contains methods for sending action titles, normal titles and setting the tab list for players.
 * 
 * @author LeonTG77
 */
public class PacketUtils {
	
	/**
	 * Sets a tablist for the given player.
	 * 
	 * @param player the player.
	 */
	public static void setTabList(Player player, Game game) {
		if (game.isRecordedRound()) {
	        return;
		} 
		
		final CraftPlayer craft = (CraftPlayer) player;
		
		final IChatBaseComponent headerJSON = ChatSerializer.a(
	      	"{text:'§4§lArctic UHC§r §8- §a§o@ArcticUHC§r\n"
	      	+ "§7Follow us for games and updates!\n'}"
	    );

		final String gamemode = game.getAdvancedTeamSize(false, true).replaceAll("-", "§8-§7") + game.getScenarios().replaceAll(",", "§8,§7");
		final String host = game.getHost();
		final String teamsize = game.getTeamSize().toLowerCase();
	        
		final IChatBaseComponent footerJSON = ChatSerializer.a(
			"{text:'\n§7" + gamemode + (!teamsize.startsWith("no") && !teamsize.startsWith("open") ? 
			"\n§4Host §8» §a" + host : "") + "'}"
		);
	        
		final PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(headerJSON);
	 
		try {
			final Field field = headerPacket.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(headerPacket, footerJSON);
		} catch (Exception e) {
			Bukkit.getServer().getLogger().severe("§cCould not send tab list packets to " + player.getName());
			return;
		}
		        
		craft.getHandle().playerConnection.sendPacket(headerPacket);
	}
	
	/**
	 * Sends a message in action bar to the given player.
	 * 
	 * @param player the player.
	 * @param msg the message.
	 */
	public static void sendAction(final Player player, final String msg) {
		final CraftPlayer craft = (CraftPlayer) player;

		final IChatBaseComponent actionJSON = ChatSerializer.a("{text:'" + msg + "'}");
		final PacketPlayOutChat actionPacket = new PacketPlayOutChat(actionJSON, (byte) 2);
        
        craft.getHandle().playerConnection.sendPacket(actionPacket);
	}
	
	/**
	 * Sends a title message to the given player.
	 * 
	 * @param player the player displaying to.
	 * @param title the title.
	 * @param subtitle the subtitle.
	 * 
	 * @param in how long it uses to fade in.
	 * @param out how long it uses to fade out.
	 * @param stay how long it stays.
	 */
	public static void sendTitle(final Player player, final String title, final String subtitle, final int in, final int stay, final int out) {
		final CraftPlayer craft = (CraftPlayer) player;
        
		final IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
		final IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");
        
		final PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, in, stay, out);
        final PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        
        craft.getHandle().playerConnection.sendPacket(titlePacket);
        craft.getHandle().playerConnection.sendPacket(subtitlePacket);
    }
}