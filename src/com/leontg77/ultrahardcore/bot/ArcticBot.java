package com.leontg77.ultrahardcore.bot;

import org.bukkit.Bukkit;

import com.leontg77.ultrahardcore.Main;

public class ArcticBot {
	public static final String PREFIX = "§3§lArctic§4§lBot §8» §7";
	
	private static ArcticBot bot = new ArcticBot();
	private static Answer answer = new Answer();
	
	public static ArcticBot getBot() {
		return bot;
	}
	
	public static Answer getAnswerer() {
		return answer;
	}
	
	public void setup() {
		Bukkit.getPluginManager().registerEvents(new BotListener(), Main.plugin);
	}
}
