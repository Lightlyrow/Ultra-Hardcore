package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;

/**
 * Timeleft command class.
 * 
 * @author LeonTG77
 */
public class TimeLeftCommand extends UHCCommand {

	public TimeLeftCommand() {
		super("timeleft", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) {
		if (game.isRecordedRound()) {
			sender.sendMessage(Main.PREFIX + "Current Episode: §a" + Timers.meetup);
			sender.sendMessage(Main.PREFIX + "Time to next episode: §a" + Timers.time + " minutes");
			return true;
		}
		
		if (game.getTeamSize().startsWith("No") || game.getTeamSize().startsWith("Open")) {
			sender.sendMessage(Main.PREFIX + "There are no matches running.");
			return true;
		}
		
		if (!State.isState(State.INGAME)) {
			sender.sendMessage(Main.PREFIX + "The game has not started yet.");
			return true;
		}
		
		sender.sendMessage(Main.PREFIX + "Displaying game timers:");
		sender.sendMessage("§8» §7Time since start: §a" + DateUtils.ticksToString(Timers.timeSeconds));
		sender.sendMessage(Timers.pvpSeconds <= 0 ? "§8» §aPvP is enabled." : "§8» §7PvP in: §a" + DateUtils.ticksToString(Timers.pvpSeconds));
		sender.sendMessage(Timers.meetupSeconds <= 0 ? "§8» §6Meetup is now!" : "§8» §7Meetup in: §a" + DateUtils.ticksToString(Timers.meetupSeconds));
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
}