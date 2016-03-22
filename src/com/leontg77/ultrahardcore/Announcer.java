package com.leontg77.ultrahardcore;

import java.util.List;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Announcer class.
 * <p>
 * Used for sending useful information every so often.
 * 
 * @author LeonTG77
 */
public class Announcer {
	private final Main plugin;
	private final Game game;
	
	/**
	 * Announcer class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 */
	protected Announcer(Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}
	
	/**
	 * List of announcements.
	 */
	private static final List<String> ANNOUNCEMENTS = ImmutableList.of(
			"Remember to use �a/uhc �7for all game information.",
			"You can look at the hall of fame with �a/hof�7.",
			"If you have any questions, ask in �a/helpop�7.",
			"You can find the match post by doing �a/post�7.",
			"The server runs off a custom UHC plugin by �6LeonTG77�7.",
			"Wonder if you are lagging? Use �a/ms.",
			"Follow our twitter for games and updates, �atwitter.com/ArcticUHC�7!",
			"You can apply for a rank at �6redd.it/45gxj0",
			"You can report staff at �6redd.it/45gxj0",
			"You can give us suggestions at �6redd.it/45gxj0",
			"You can view your stats by using �a/stats�7.",
			"You can view the top 10 stats with �a/top�7.",
			"View the border size with �a/border�7.",
			"Wondering how long it is until PvP/Meetup? Use �a/timeleft�7.",
			"View the enabled scenario(s) by doing �a/scenario�7."
	);
	
	private final Random rand = new Random();
	
	/**
	 * Setup the announcer class.
	 */
	public void setup() {
		new BukkitRunnable() {
			public void run() {
				if (game.isRecordedRound() || State.isState(State.SCATTER)) {
					return;
				}
				
				PlayerUtils.broadcast(Main.INFO_PREFIX + ANNOUNCEMENTS.get(rand.nextInt(ANNOUNCEMENTS.size())));
			}
		}.runTaskTimer(plugin, 10000, 10000);
		
		plugin.getLogger().info("The announcer have been setup.");
	}
}