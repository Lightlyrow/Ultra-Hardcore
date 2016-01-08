package com.leontg77.ultrahardcore.bot;

import com.leontg77.ultrahardcore.Game;

public class Answer extends ArcticBot {

	public String getAnswer(String question) {
		final Game game = Game.getInstance();
		
		switch (question.toLowerCase()) {
		case "nether?":
			return "Nether is " + (game.nether() ? "enabled" : "disabled");
		case "toggle":
		case "toggle?":
			return "We never toggle on this server";
		case "artic":
		case "articbot":
			return "It's spelled Arctic, not Artic";
		case "stalking?":
			return "Stalking is allowed but not excessive";
		case "horses?":
			return "Horses are " + (game.horses() ? "Enabled, Armor is " + (game.horseArmor() ? "on" : "off") + ", healing is" + (game.horseHealing() ? "on" : "off") : "Disabled");
		}
		
		return null;
	}
}