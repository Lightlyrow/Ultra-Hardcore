package com.leontg77.ultrahardcore;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main.BorderShrink;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.protocol.HardcoreHearts;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Game management class.
 * <p>
 * This class contains all setters and getters for all togglable features.
 * 
 * @author LeonTG77
 */
public class Game {
	private static Game instance = new Game();
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Game getInstance() {
		return instance;
	}

	private Settings settings = Settings.getInstance();
	
	// ############################ BASIC ############################
	// TODO: <-- just for the marker.

	/**
	 * Enable or disable pre whitelists.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setPreWhitelists(boolean enable) {
		settings.getConfig().set("misc.prewl", enable);
		settings.saveConfig();
	}

	/**
	 * Check if pre whitelists are enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean preWhitelists() {
		return settings.getConfig().getBoolean("misc.prewl", true);
	}
	
	/**
	 * Set the team size of the game.
	 * 
	 * @param teamSize the new teamsize.
	 */
	public void setTeamSize(String teamSize) {
		BoardManager score = BoardManager.getInstance();
		
		if (pregameBoard()) {
			score.resetScore("§8» §7" + GameUtils.getTeamSize(true, false));
		}
		
		settings.getConfig().set("teamsize", teamSize);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
		
		if (pregameBoard()) {
			score.setScore("§8» §7" + GameUtils.getTeamSize(true, false), 6);
		}
		
		InvGUI.getGameInfo().update();
	}
	
	/**
	 * Get the game teamsize.
	 * 
	 * @return The teamsize.
	 */
	public String getTeamSize() {
		return settings.getConfig().getString("teamsize", "No");
	}
	
	/**
	 * Set the scenarios of the game.
	 * 
	 * @param scenarios The new scenarios.
	 */
	public void setScenarios(String scenarios) {
		BoardManager score = BoardManager.getInstance();
		
		if (pregameBoard()) {
			for (String scen : getScenarios().split(", ")) {
				score.resetScore("§8» §7" + scen);
			}
			
			for (String scen : scenarios.split(", ")) {
				score.setScore("§8» §7" + scen, 3);
			}
		}
		
		settings.getConfig().set("scenarios", scenarios);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
		
		InvGUI.getGameInfo().update();
	}

	public String getScenarios() {
		return settings.getConfig().getString("scenarios", "games running");
	}
	
	/**
	 * Set the host of the game.
	 * 
	 * @param name The new host.
	 */
	public void setHost(String name) {
		settings.getConfig().set("host", name);
		settings.saveConfig();
		
		if (!isRecordedRound()) {
			BoardManager board = BoardManager.getInstance();
			
			board.kills.setDisplayName("§4§lUHC §r§8- §7§o" + name + "§r");
		}
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
		
		InvGUI.getGameInfo().update();
	}

	/**
	 * Get the host of the game.
	 * 
	 * @return The host.
	 */
	public String getHost() {
		return settings.getConfig().getString("host", "None");
	}

	/**
	 * Set the max players that will be able to join.
	 * 
	 * @param maxplayers The max player limit.
	 */
	public void setMaxPlayers(int maxplayers) {
		settings.getConfig().set("maxplayers", maxplayers);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}
	
	/**
	 * Get the max players that can join the server.
	 * 
	 * @return The max player limit.
	 */
	public int getMaxPlayers() {
		return settings.getConfig().getInt("maxplayers", 150);
	}

	/**
	 * Set the matchpost for the game.
	 * 
	 * @param matchpost The new matchpost.
	 */
	public void setMatchPost(String matchpost) {
		settings.getConfig().set("matchpost", matchpost);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	/**
	 * Get the matchpost for the game.
	 * 
	 * @return The game's matchpost.
	 */
	public String getMatchPost() {
		return settings.getConfig().getString("matchpost", "No_Post_Set");
	}
	
	/**
	 * Set the world to be used for the game.
	 * <p> 
	 * This will automaticly use all the same worlds 
	 * that has the world name with _end or _nether at the end.
	 * 
	 * @param name The new world name.
	 */
	public void setWorld(String name) {
		settings.getConfig().set("world", name);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	/**
	 * Get the world to be used for the game.
	 * 
	 * @return The game world
	 */
	public World getWorld() {
		return Bukkit.getWorld(settings.getConfig().getString("world", "girhgqeruiogh"));
	}

	/**
	 * Set the time of the pvp for the game.
	 * 
	 * @param meetup The time in minutes.
	 */
	public void setPvP(int pvp) {
		settings.getConfig().set("time.pvp", pvp);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	/**
	 * Get the time when pvp will be enabled after the start.
	 * 
	 * @return The time in minutes.
	 */
	public int getPvP() {
		return settings.getConfig().getInt("time.pvp", 15);
	}

	/**
	 * Set the time of the meetup of the game.
	 * 
	 * @param meetup The time in minutes.
	 */
	public void setMeetup(int meetup) {
		settings.getConfig().set("time.meetup", meetup);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	/**
	 * Get the time when meetup will occur after the start.
	 * 
	 * @return The time in minutes.
	 */
	public int getMeetup() {
		return settings.getConfig().getInt("time.meetup", 90);
	}

	// ############################ MISC ############################
	// TODO: <-- just for the marker.

	/**
	 * Enable or disable team management.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setTeamManagement(boolean enable) {
		settings.getConfig().set("misc.team", enable);
		settings.saveConfig();
	}
	
	/**
	 * Get if team management is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean teamManagement() {
		return settings.getConfig().getBoolean("misc.team", false);
	}

	/**
	 * Enable or disable the pregame board.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setPregameBoard(boolean enable) {
		settings.getConfig().set("misc.board.pregame", enable);
		settings.saveConfig();
	}

	/**
	 * Get if the pregame board is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean pregameBoard() {
		return settings.getConfig().getBoolean("misc.board.pregame", false);
	}

	/**
	 * Enable or disable the arena board.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setArenaBoard(boolean enable) {
		settings.getConfig().set("misc.board.arena", enable);
		settings.saveConfig();
	}

	/**
	 * Get if the arena board is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean arenaBoard() {
		return settings.getConfig().getBoolean("misc.board.arena", false);
	}

	/**
	 * Enable or disable recordedround mode.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setRecordedRound(boolean enable) {
		settings.getConfig().set("recordedround.enabled", enable);
		settings.saveConfig();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
		
		BoardManager board = BoardManager.getInstance();
		
		if (enable) {
			board.kills.setDisplayName("§6" + getRRName());
		} else {
			board.kills.setDisplayName("§4§lUHC §r§8- §7§o" + getHost() + "§r");
		}
	}

	/**
	 * Get if recordedround mode is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isRecordedRound() {
		return settings.getConfig().getBoolean("recordedround.enabled", false);
	}

	/**
	 * Get if the game is a private game.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isPrivateGame() {
		return getHost().equals("LeonsPrivate");
	}
	
	/**
	 * Set the name of the recorded round.
	 * <p>
	 * Note: If recordedround mode is disabled, this will have no effect.
	 * 
	 * @param name The new name of the RR.
	 */
	public void setRRName(String name) {
		settings.getConfig().set("recordedround.name", name);
		settings.saveConfig();
		
		if (isRecordedRound()) {
			BoardManager board = BoardManager.getInstance();
			
			board.kills.setDisplayName("§6" + name);
		}
	}

	/**
	 * Get the recorded round name.
	 * @return
	 */
	public String getRRName() {
		return settings.getConfig().getString("recordedround.name", "ANAMEHERE");
	}

	/**
	 * Mute or unmute the chat
	 * 
	 * @param mute True to mute, false to unmute.
	 */
	public void setMuted(boolean mute) {
		settings.getData().set("muted", mute);
		settings.saveData();
	}

	/**
	 * Check if the chat is muted.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isMuted() {
		return settings.getData().getBoolean("muted", false);
	}

	// ############################ RATES ############################
	// TODO: <-- just for the marker.

	/**
	 * Set the apple rates of the game.
	 * 
	 * @param appleRate The apple rate.
	 */
	public void setAppleRates(double appleRate) {
		settings.getConfig().set("rates.apple.rate", (appleRate / 100));
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}
	
	/**
	 * Get the apple rates of the game.
	 * 
	 * @return The apple rates.
	 */
	public double getAppleRates() {
		return settings.getConfig().getDouble("rates.apple.rate", 0.0055);
	}
	
	/**
	 * Set the flint rates of the game.
	 * 
	 * @param flintRate The flint rate.
	 */
	public void setFlintRates(double flintRate) {
		settings.getConfig().set("rates.flint.rate", (flintRate / 100));
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	/**
	 * Get the flint rates of the game.
	 * 
	 * @return The flint rates.
	 */
	public double getFlintRates() {
		return settings.getConfig().getDouble("rates.flint.rate", 0.1);
	}

	/**
	 * Enable or disable shears.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setShears(boolean enable) {
		settings.getConfig().set("rates.shears.enabled", enable);
		settings.saveConfig();
	}
	
	/**
	 * Get if shears is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean shears() {
		return settings.getConfig().getBoolean("rates.shears.enabled", true);
	}
	
	/**
	 * Set the shears rates of the game.
	 * <p>
	 * Note: This will have no effect if shears are disabled.
	 * 
	 * @param shearRate The shears rate.
	 */
	public void setShearRates(double shearRate) {
		settings.getConfig().set("rates.shears.rate", (shearRate / 100));
		settings.saveConfig();
	}
	
	/**
	 * Get the shear rates of the game.
	 * 
	 * @return The shear rates, 0 if shears are disabled.
	 */
	public double getShearRates() {
		if (!shears()) {
			return 0;
		}
		
		return settings.getConfig().getDouble("rates.shears.rate", 0.5);
	}
	
	// ############################ FEATURES ############################
	// TODO: <-- just for the marker.
	
	
	/**
	 * Everything below is getters and setters, guess yourself what they do by looking above
	 * I might write the java docs one day..
	 */
	
	/**
	 * Enable or disable anti stripmine.
	 * 
	 * @param enable True to enable, false to disable.
	 */
	public void setAntiStripmine(boolean enable) {
		settings.getConfig().set("feature.antiStripmine.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	/**
	 * Check if anti stripmine is enabled.
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean antiStripmine() {
		return settings.getConfig().getBoolean("feature.antiStripmine.enabled", true);
	}
	
	public void setBorderShrink(BorderShrink border) {
		settings.getConfig().set("feature.border.shrinkAt", border.name().toLowerCase());
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}
	
	public BorderShrink getBorderShrink() {
		return BorderShrink.valueOf(settings.getConfig().getString("feature.border.shrinkAt", BorderShrink.MEETUP.name()).toUpperCase());
	}

	public boolean absorption() {
		return settings.getConfig().getBoolean("feature.absorption.enabled", false);
	}
	
	public void setAbsorption(boolean enable) {
		settings.getConfig().set("feature.absorption.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean goldenHeads() {
		return settings.getConfig().getBoolean("feature.goldenheads.enabled", true);
	}
	
	public void setGoldenHeads(boolean enable) {
		settings.getConfig().set("feature.goldenheads.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}
	
	public int goldenHeadsHeal() {
		return settings.getConfig().getInt("feature.goldenheads.heal", 8);
	}
	
	public void setGoldenHeadsHeal(double headheals) {
		settings.getConfig().set("feature.goldenheads.heal", (int) (headheals * 2));
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public double pearlDamage() {
		return settings.getConfig().getDouble("feature.pearldamage.enabled", 0);
	}
	
	public void setPearlDamage(double damage) {
		settings.getConfig().set("feature.pearldamage.enabled", damage);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean notchApples() {
		return settings.getConfig().getBoolean("feature.notchapples.enabled", true);
	}
	
	public void setNotchApples(boolean enable) {
		settings.getConfig().set("feature.notchapples.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean deathLightning() {
		return settings.getConfig().getBoolean("feature.deathlightning.enabled", true);
	}
	
	public void setDeathLightning(boolean enable) {
		settings.getConfig().set("feature.deathlightning.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean nether() {
		return settings.getConfig().getBoolean("feature.nether.enabled", false);
	}
	
	public void setNether(boolean enable) {
		settings.getConfig().set("feature.nether.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean theEnd() {
		return settings.getConfig().getBoolean("feature.theend.enabled", false);
	}
	
	public void setTheEnd(boolean enable) {
		settings.getConfig().set("feature.theend.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean ghastDropGold() {
		return settings.getConfig().getBoolean("feature.ghastdrops.enabled", true);
	}
	
	public void setGhastDropGold(boolean enable) {
		settings.getConfig().set("feature.ghastdrops.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean strength() {
		return settings.getConfig().getBoolean("feature.strength.enabled", true);
	}
	
	public void setStrength(boolean enable) {
		settings.getConfig().set("feature.strength.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean nerfedStrength() {
		return settings.getConfig().getBoolean("feature.strength.nerfed", true);
	}
	
	public void setNerfedStrength(boolean enable) {
		settings.getConfig().set("feature.strength.nerfed", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean tabShowsHealthColor() {
		return settings.getConfig().getBoolean("feature.tabShowsHealthColor.enabled", false);
	}
	
	public void setTabShowsHealthColor(boolean enable) {
		settings.getConfig().set("feature.tabShowsHealthColor.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
		
		for (Player online : PlayerUtils.getPlayers()) {
			online.setPlayerListName(null);
		}
	}

	public boolean goldenMelonNeedsIngots() {
		return settings.getConfig().getBoolean("feature.goldenMelonNeedsIngots.enabled", true);
	}
	
	public void setGoldenMelonNeedsIngots(boolean enable) {
		settings.getConfig().set("feature.goldenMelonNeedsIngots.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}
	
	public void setTier2(boolean enable) {
		settings.getConfig().set("feature.tier2.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean tier2() {
		return settings.getConfig().getBoolean("feature.tier2.enabled", true);
	}
	
	public void setSplash(boolean enable) {
		settings.getConfig().set("feature.splash.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean splash() {
		return settings.getConfig().getBoolean("feature.splash.enabled", true);
	}
	
	public void setHorses(boolean enable) {
		settings.getConfig().set("feature.horse.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean horses() {
		return settings.getConfig().getBoolean("feature.horse.enabled", true);
	}
	
	public void setHorseHealing(boolean enable) {
		settings.getConfig().set("feature.horse.healing", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean horseHealing() {
		return settings.getConfig().getBoolean("feature.horse.healing", true);
	}
	
	public void setHorseArmor(boolean enable) {
		settings.getConfig().set("feature.horse.armor", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean horseArmor() {
		return settings.getConfig().getBoolean("feature.horse.armor", true);
	}
	
	public void setHeartsOnTab(boolean enable) {
		settings.getConfig().set("feature.heartsOnTab.enabled", enable);
		settings.saveConfig();
		
		InvGUI.getGameInfo().update();
	}

	public boolean heartsOnTab() {
		return settings.getConfig().getBoolean("feature.heartsOnTab.enabled", false);
	}
	
	public void setHardcoreHearts(boolean enable) {
		settings.getConfig().set("feature.hardcoreHearts.enabled", enable);
		settings.saveConfig();
		
		if (enable) {
			HardcoreHearts.enable();
		} else {
			HardcoreHearts.disable();
		}
	}

	public boolean hardcoreHearts() {
		return settings.getConfig().getBoolean("feature.hardcoreHearts.enabled", true);
	}

	public void setNewStone(boolean enable) {
		settings.getConfig().set("feature.newStone.enabled", enable);
		settings.saveConfig();
	}

	public boolean newStone() {
		return settings.getConfig().getBoolean("feature.newStone.enabled", true);
	}

	public void setBookshelves(boolean enable) {
		settings.getConfig().set("feature.bookshelves.enabled", enable);
		settings.saveConfig();
	}

	public boolean bookshelves() {
		return settings.getConfig().getBoolean("feature.bookshelves.enabled", true);
	}
}