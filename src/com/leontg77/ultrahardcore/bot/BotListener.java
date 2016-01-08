package com.leontg77.ultrahardcore.bot;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * The listener for detecting questions in the chat.
 * 
 * @author LeonTG77
 */
public class BotListener extends ArcticBot implements Listener {
	private static final String SPLIT_STRING = " ";
	private final Answer answer = getAnswerer();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(AsyncPlayerChatEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();
		
		if (message.equalsIgnoreCase("Hi ArcticBot") || message.equalsIgnoreCase("Hey ArcticBot") || message.equalsIgnoreCase("Hello ArcticBot")) {
			player.sendMessage(PREFIX + "Hello §a" + player.getName() + "§7!");
			return;
		}
		
		if (message.equalsIgnoreCase("ArcticBot sucks")) {
			player.sendMessage(PREFIX + ":(");
			return;
		}
		
		String lastSplit = null;
		
		for (String split : message.split(SPLIT_STRING)) {
			if (lastSplit != null && split.equalsIgnoreCase(lastSplit)) {
				break;
			}
			
			lastSplit = split;
			
			if (answer.getAnswer(split) == null) {
				continue;
			}
			
			player.sendMessage(PREFIX + "Hey §a" + player.getName() + "§7! §c" + answer.getAnswer(split) + "§7, Please use §6/uhc §7in the future!");
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void on(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		Player player = event.getPlayer();
		
		if (!message.split(SPLIT_STRING)[0].equalsIgnoreCase("/helpop")) {
			return;
		}
		
		if (message.equalsIgnoreCase("Hi ArcticBot") || message.equalsIgnoreCase("Hey ArcticBot") || message.equalsIgnoreCase("Hello ArcticBot")) {
			player.sendMessage(PREFIX + "Hello §a" + player.getName() + "§7!");
			return;
		}
		
		if (message.equalsIgnoreCase("ArcticBot sucks")) {
			player.sendMessage(PREFIX + ":(");
			return;
		}
		
		String lastSplit = null;
		
		for (String split : message.split(SPLIT_STRING)) {
			if (lastSplit != null && split.equalsIgnoreCase(lastSplit)) {
				break;
			}
			
			lastSplit = split;
			
			if (answer.getAnswer(split) == null) {
				continue;
			}
			
			player.sendMessage(PREFIX + "Hey §a" + player.getName() + "§7! §c" + answer.getAnswer(split) + "§7, Please use §6/uhc §7in the future!");
		}
	}
}