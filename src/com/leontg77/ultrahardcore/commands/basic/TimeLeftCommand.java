package com.leontg77.ultrahardcore.commands.basic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;

/**
 * Timeleft command class.
 * 
 * @author LeonTG77
 */
public class TimeLeftCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("timeleft")) {
			if (Game.getInstance().isRecordedRound()) {
				sender.sendMessage(Main.PREFIX + "Current Episode: §a" + Timers.meetup);
				sender.sendMessage(Main.PREFIX + "Time to next episode: §a" + Timers.time + " minutes");
				return true;
			}
			
			if (GameUtils.getTeamSize(false, false).startsWith("No") || GameUtils.getTeamSize(false, false).startsWith("Open")) {
				sender.sendMessage(Main.PREFIX + "There are no matches running.");
				return true;
			}
			
			if (!State.isState(State.INGAME)) {
				sender.sendMessage(Main.PREFIX + "The game has not started yet.");
				return true;
			}
			
			sender.sendMessage(Main.PREFIX + "Game timers:");
			sender.sendMessage("§8» §7Time since start: §a" + DateUtils.ticksToString(Timers.timeSeconds));
			sender.sendMessage(Timers.pvpSeconds <= 0 ? "§8» §aPvP is enabled." : "§8» §7PvP in: §a" + DateUtils.ticksToString(Timers.pvpSeconds));
			sender.sendMessage(Timers.meetupSeconds <= 0 ? "§8» §6Meetup is now!" : "§8» §7Meetup in: §a" + DateUtils.ticksToString(Timers.meetupSeconds));
		}
		return true;
	}
}