package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Announcer class.
 * <p>
 * This class contains methods for sending announcements to the server every minute.
 * 
 * @author LeonTG77
 */
public class Announcer {
	private ArrayList<String> messages = new ArrayList<String>();
	private static Announcer instance = new Announcer();
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Announcer getInstance() {
		return instance;
	}
	
	/**
	 * Setup the arena class.
	 */
	public void setup() {
		new BukkitRunnable() {
			public void run() {
				if (Game.getInstance().isRecordedRound() || State.isState(State.SCATTER)) {
					return;
				}
				
				PlayerUtils.broadcast("§8§l[§6§l!§8§l]§7 " + randomAnnouncement());
			}
		}.runTaskTimer(Main.plugin, 10000, 10000);
		
		messages.add("Remember to use §a/uhc §7for all game information.");
		messages.add("You can view the hall of fame with §a/hof§7.");
		messages.add("If you have an questions, use §a/helpop§7.");
		messages.add("You can find the match post by doing §a/post§7.");
		messages.add("The server runs off a custom uhc plugin by §6LeonTG77§7.");
		messages.add("Wonder if you are lagging? Use §a/ms §7or §a/tps§7.");
		messages.add("Follow our twitter for games and updates, §a@ArcticUHC§7!");
		messages.add("You can apply for a rank at: §6http://goo.gl/forms/O6tklNcrEu§7!");
		
		Main.plugin.getLogger().info("The announcer has been setup.");
	}

	/**
	 * Get a random announcements from the presets.
	 * 
	 * @return A random announcement.
	 */
	private String randomAnnouncement() {
		Random rand = new Random();
		int size = messages.size();
		
		String message = messages.get(rand.nextInt(size));
		return message;
	}
}