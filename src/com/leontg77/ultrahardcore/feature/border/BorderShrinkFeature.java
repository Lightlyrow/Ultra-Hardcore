package com.leontg77.ultrahardcore.feature.border;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.events.uhc.GameStartEvent;
import com.leontg77.ultrahardcore.events.uhc.MeetupEvent;
import com.leontg77.ultrahardcore.events.uhc.PvPEnableEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.GameUtils;

/**
 * Border shrink feature class.
 * 
 * @author LeonTG77
 */
public class BorderShrinkFeature extends Feature implements Listener {
	private static final String PATH = "feature.border.shrinkAt";
	private BorderShrink shrink;

	public BorderShrinkFeature() {
		super("Border Shrink", "A shrinking border once ### event happends.");
		
		try {
			shrink = BorderShrink.valueOf(settings.getConfig().getString(PATH, "MEETUP").toUpperCase());
		} catch (Exception e) {
			shrink = BorderShrink.MEETUP;
		}
	}
	
	/**
	 * Set the time the border will shrink at.
	 * 
	 * @param border The new border shrink time.
	 */
	public void setBorderShrink(BorderShrink border) {
		this.shrink = border;
		
		settings.getConfig().set(PATH, border.name().toLowerCase());
		settings.saveConfig();
	}
	
	/**
	 * Get the time the border will shrink at.
	 * 
	 * @return The shrink time.
	 */
	public BorderShrink getBorderShrink() {
		return shrink;
	}

	@EventHandler
	public void on(GameStartEvent event) {
		if (!shrink.equals(BorderShrink.START)) {
			return;
		}
		
		final Timers timer = event.getTimers();
		
		for (World world : GameUtils.getGameWorlds()) {
			world.getWorldBorder().setSize(300, timer.getMeetupInSeconds());
		}
	}

	@EventHandler
	public void on(PvPEnableEvent event) {
		if (!shrink.equals(BorderShrink.START)) {
			return;
		}
		
		final Timers timer = event.getTimers();
		
		for (World world : GameUtils.getGameWorlds()) {
			world.getWorldBorder().setSize(300, timer.getMeetupInSeconds());
		}
	}

	@EventHandler
	public void on(MeetupEvent event) {
		if (shrink.equals(BorderShrink.NEVER)) {
			return;
		}

		final int timeToNextShrink;
		
		if (shrink.equals(BorderShrink.MEETUP)) {
			timeToNextShrink = 120;
		} else {
			timeToNextShrink = 1920;
		}
		
		new BorderRunnable(timeToNextShrink + 1).runTaskTimer(Main.plugin, 0, 20);
	}
	
	/**
	 * Border Shrink enum.
	 * <p>
	 * Contains all the possible times when the border can shrink.
	 * 
	 * @author LeonTG77
	 */
	public enum BorderShrink {
		NEVER(""), START("from "), PVP("at "), MEETUP("at ");
		
		private String preText;
		
		/**
		 * Constructor for BorderShrink.
		 * 
		 * @param preText The text that fits before the shink name.
		 */
		private BorderShrink(String preText) {
			this.preText = preText;
		}
		
		/**
		 * Get the border pre text.
		 * 
		 * @return Pre text.
		 */
		public String getPreText() {
			return preText;
		}
	}
}