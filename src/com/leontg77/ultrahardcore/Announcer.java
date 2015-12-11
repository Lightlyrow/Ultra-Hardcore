package com.leontg77.ultrahardcore;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.utils.PlayerUtils;

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
				
				PlayerUtils.broadcast("§c§lInfo §8» §7" + getRandomMessage());
			}
		}.runTaskTimer(Main.plugin, 10000, 10000);
		
		messages.add("Remember to use §a/uhc §7for all game information.");
		messages.add("You can view the hall of fame with §a/hof§7.");
		messages.add("If you have an questions, use §a/helpop§7.");
		messages.add("You can find the match post by doing §a/post§7.");
		messages.add("The server runs off a custom UHC plugin by §6LeonTG77§7.");
		messages.add("Wonder if you are lagging? Use §a/ms §7or §a/tps§7.");
		messages.add("Follow our twitter for games and updates, §a@ArcticUHC§7!");
		messages.add("You can apply for a rank at §6http://goo.gl/forms/O6tklNcrEu");
		messages.add("You can view your stats by using §a/stats§7.");
		messages.add("You can view the top stats with §a/top [stat]§7.");
		messages.add("View the border size with §a/border§7.");
		messages.add("How long til pvp/meetup? Use §a/timeleft§7.");
		messages.add("View the enabled scenario with info by doing §a/scen§7.");
		
		Main.plugin.getLogger().info("The announcer has been setup.");
	}

	/**
	 * Get a random announcements from the presets.
	 * 
	 * @return A random announcement.
	 */
	private String getRandomMessage() {
		Random rand = new Random();
		int size = messages.size();
		
		String message = messages.get(rand.nextInt(size));
		return message;
	}
}