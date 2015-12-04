package com.leontg77.uhc.user;

/**
 * The ranking enum class.
 * 
 * @author LeonTG77
 */
public enum Rank {
	USER(1), DONATOR(2), SPEC(3), STAFF(4), TRIAL(5), HOST(6), OWNER(7);
	
	int level;
	
	private Rank(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}
}