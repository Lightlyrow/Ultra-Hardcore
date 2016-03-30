package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * GoodGame+ scenario class.
 * 
 * @author LeonTG77
 */
public class GoodGamePlus extends Scenario implements Listener {
	private static final String PREFIX = "§aGood Game+ §8» §7";
	
	private final Game game;
	
	public GoodGamePlus(Game game) {
		super("GoodGame+", "The first person to say GG after someone dies will get a random reward, whether it be a diamond, gold, cake, iron, or a firework. The first person to say: ez, easy, eazy, rekt, your bad, ur bad, bad, or kys will get blindness, poison or wither.");
		
		this.game = game;
	}
	
	private final Random rand = new Random();

	private boolean checkforToxic = false; // :3 :3 :3 :3 :3 :3
	private boolean checkForGG = false;
	
	@EventHandler
	public void on(PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}

		checkForGG = true;
		checkforToxic = true;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onGG(AsyncPlayerChatEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		if (!checkForGG) {
			return;
		}
		
		String message = event.getMessage();
		
		if (!)
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onToxic(AsyncPlayerChatEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		if (!checkforToxic) {
			return;
		}
		
		
	}
}