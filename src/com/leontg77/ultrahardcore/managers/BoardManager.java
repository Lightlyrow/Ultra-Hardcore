package com.leontg77.ultrahardcore.managers;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;

/**
 * The class for managing scoreboards.
 * <p>
 * This class contains methods for setting scores, getting scores, setting up boards and resettings scores.
 * 
 * @author LeonTG77
 */
public class BoardManager {
	private static final BoardManager INSTANCE = new BoardManager();
	
	private final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

	private Objective nameHealth = board.getObjective("nameHealth");
	private Objective tabHealth = board.getObjective("tabHealth");
	private Objective hearts = board.getObjective("hearts");
	
	private Objective backup = board.getObjective("backup");
	private Objective kills = board.getObjective("kills");
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return the instance.
	 */
	public static BoardManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Get the main scoreboard.
	 * 
	 * @return The scoreboard.
	 */
	public Scoreboard getBoard() {
		return board;
	}

	/**
	 * Get the health below the name objective.
	 * 
	 * @return The objective.
	 */
	public Objective getNameHealObjective() {
		return nameHealth;
	}

	/**
	 * Get the percent health in tab objective.
	 * 
	 * @return The objective.
	 */
	public Objective getTabHealthObjective() {
		return tabHealth;
	}

	/**
	 * Get the tab hearts objective.
	 * 
	 * @return The objective.
	 */
	public Objective getTabHeartsObjective() {
		return hearts;
	}
	
	/**
	 * Get the sidebar kills objective.
	 * 
	 * @return The objective.
	 */
	public Objective getKillsObjective() {
		return kills;
	}
	
	/**
	 * Get the sidebar kill backup objective.
	 * 
	 * @return The objective.
	 */
	public Objective getBackupObjective() {
		return backup;
	}
	
	/**
	 * Setup the scoreboard objectives.
	 */
	public void setup() {
		if (board.getObjective("kills") == null) {
			kills = board.registerNewObjective("kills", "dummy");
		}
		
		if (board.getObjective("backup") == null) {
			backup = board.registerNewObjective("backup", "dummy");
		}
		
		if (board.getObjective("tabHealth") == null) {
			tabHealth = board.registerNewObjective("tabHealth", "dummy");
		}
		
		if (board.getObjective("nameHealth") == null) {
			nameHealth = board.registerNewObjective("nameHealth", "dummy");
		}
		
		if (board.getObjective("hearts") == null) {
			hearts = board.registerNewObjective("hearts", "health");
		}
		
		final Game game = Game.getInstance();
		
		if (game.isRecordedRound()) {
			kills.setDisplayName("Kills");
		} else {
			kills.setDisplayName("§4Arctic §8» §7§o" + game.getHost().substring(0, Math.min(game.getHost().length(), 13)) + "§r");
		}
		
		if (!game.arenaBoard()) {
			kills.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		nameHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
		nameHealth.setDisplayName("§4♥");
		
		Main.plugin.getLogger().info("Scoreboards has been setup.");
	}
	
	/**
	 * Sets the score of the given player.
	 * 
	 * @param entry the player setting it for.
	 * @param newScore the new score.
	 */
	public void setScore(final String entry, final int newScore) {
		final Score backupScore = backup.getScore(entry);
		final Score killScore = kills.getScore(entry);

		backupScore.setScore(getActualScore(entry) + newScore);
		killScore.setScore(newScore);
	}

	/**
	 * Gets a score for the given string.
	 * 
	 * @param entry the wanted string.
	 * @return The score of the string.
	 */
	public int getScore(final String entry) {
		return kills.getScore(entry).getScore();
	}

	/**
	 * Gets a actual score for the given string.
	 * 
	 * @param entry the wanted string.
	 * @return The actual score of the string.
	 */
	public int getActualScore(final String entry) {
		return backup.getScore(entry).getScore();
	}

	/**
	 * Reset the score of the given string.
	 * 
	 * @param entry the string resetting.
	 */
	public void resetScore(final String entry) {
		int score = getActualScore(entry);
		
		board.resetScores(entry);
		
		// this should never be reset, so i'm adding it back! :D
		backup.getScore(entry).setScore(score);
	}
}