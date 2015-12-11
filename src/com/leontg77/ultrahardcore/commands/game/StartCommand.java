package com.leontg77.ultrahardcore.commands.game;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timers;

/**
 * Start command class.
 * 
 * @author LeonTG77
 */
public class StartCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String label, String[] args) {
		if (!sender.hasPermission("uhc.start")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		Timers timers = Timers.getInstance();
		
		if (State.isState(State.NOT_RUNNING) || State.isState(State.CLOSED)) {
			sender.sendMessage(ChatColor.RED + "You cannot start the game without scattering first.");
		}
		else if (State.isState(State.OPEN)) {
			sender.sendMessage(ChatColor.RED + "You cannot start the game when whitelist is off.");
		}
		else if (State.isState(State.SCATTER)) {
			if (Game.getInstance().isRecordedRound()) {
				timers.startRR();
			} else {
				timers.start();
			}
		}
		else if (State.isState(State.INGAME)) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "Usage: /start <timepassed> <timetopvp> <timetomeetup>");
				return true;
			}
			
			int timePassed;
			int pvp;
			int meetup;
			
			try {
				timePassed = Integer.parseInt(args[0]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invaild number.");
				return true;
			}
			
			try {
				pvp = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invaild number.");
				return true;
			}
			
			try {
				meetup = Integer.parseInt(args[2]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invaild number.");
				return true;
			}
			
			Timers.time = timePassed;
			Timers.pvp = pvp;
			Timers.meetup = meetup;
			
			Timers.timeSeconds = (timePassed > 0 ? (timePassed * 60) : 0);
			Timers.pvpSeconds = (pvp > 0 ? (pvp * 60) : 0);
			Timers.meetupSeconds = (meetup > 0 ? (meetup * 60) : 0);

			if (Game.getInstance().isRecordedRound()) {
				timers.timerRR();
			} else {
				timers.timer();
			}
		}
		return true;
	}
}