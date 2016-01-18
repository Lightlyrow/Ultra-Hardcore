package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Start command class.
 * 
 * @author LeonTG77
 */
public class StartCommand extends UHCCommand {

	public StartCommand() {
		super("start", "<timefromstart> <timeuntilpvp> <timeuntilmeetup>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		final Timers timers = Timers.getInstance();
		final Game game = Game.getInstance();
		
		switch (State.getState()) {
		case NOT_RUNNING:
		case OPEN:
		case CLOSED:
			throw new CommandException("You can't start the game without scattering first.");
		case SCATTER:
			if (game.isRecordedRound()) {
				timers.startRR();
			} else {
				timers.start();
			}
			break;
		case INGAME:
			if (args.length < 3) {
				return false;
			}
			
			int timePassed = parseInt(args[0], "time from start");
			int pvp = parseInt(args[1], "time until pvp");
			int meetup = parseInt(args[2], "time until meetup");
			
			Timers.time = timePassed;
			Timers.pvp = pvp;
			Timers.meetup = meetup;
			
			Timers.timeSeconds = (timePassed > 0 ? (timePassed * 60) : 0);
			Timers.pvpSeconds = (pvp > 0 ? (pvp * 60) : 0);
			Timers.meetupSeconds = (meetup > 0 ? (meetup * 60) : 0);

			if (game.isRecordedRound()) {
				timers.timerRR();
			} else {
				timers.timer();
			}
			break;
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
}