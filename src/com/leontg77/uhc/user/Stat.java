package com.leontg77.uhc.user;

/**
 * The stats enum class.
 * 
 * @author LeonTG77
 */
public enum Stat {
	DEATHS("Deaths"), 
	KILLS("Kills"), 
	WINS("Wins"), 
	GAMESPLAYED("Games played"), 
	ARENAKILLS("Arena Kills"), 
	ARENADEATHS("Arena Deaths"), 
	ARENAKILLSTREAK("Highest Arena Killstreaks"), 
	GOLDENAPPLESEATEN("Golden Apples Eaten"),
	GOLDENHEADSEATEN("Golden Heads Eaten"), 
	HORSESTAMED("Horses Tamed"), 
	WOLVESTAMED("Wolves Tamed"), 
	NETHER("Went to Nether"), 
	END("Went to The End"), 
	DIAMONDS("Mined diamonds"),
	GOLD("Mined gold"),
	HOSTILEMOBKILLS("Killed a monster"),
	ANIMALKILLS("Killed an animal"), 
	KILLSTREAK("Highest Killstreaks"), 
	DAMAGETAKEN("Damage taken"), 
	POTIONS("Potions Drunk");
	
	private String name;
	
	private Stat(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Stat getStat(String stat) {
		try {
			return valueOf(stat);
		} catch (Exception e) {
			for (Stat stats : values()) {
				if (stats.getName().startsWith(stat)) {
					return stats;
				}
			}
		}
		
		return null;
	}
}