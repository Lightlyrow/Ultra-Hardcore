package com.leontg77.ultrahardcore.commands.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Arena command class.
 * 
 * @author LeonTG77
 */
public class ArenaCommand extends UHCCommand {	
	private final BoardManager board;
	
	private final Arena arena;
	private final Game game;

	public ArenaCommand(Arena arena, Game game, BoardManager board) {
		super("arena", "[enable|disable|reset|board|leave]");
		
		this.board = board;
		
		this.arena = arena;
		this.game = game;
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (sender.hasPermission("uhc.arena.admin")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("enable")) {
					if (arena.isEnabled()) {
						throw new CommandException("The arena is already enabled.");
					}
					
					PlayerUtils.broadcast(Arena.PREFIX + "The arena has been enabled, use §a/a §7to join it.");
					arena.enable();
					return true;
				} 

				if (args[0].equalsIgnoreCase("disable")) {
					if (!arena.isEnabled()) {
						throw new CommandException("The arena is not enabled.");
					}

					PlayerUtils.broadcast(Arena.PREFIX + "The arena has been disabled.");
					arena.disable();
					return true;
				} 

				if (args[0].equalsIgnoreCase("reset")) {
					arena.reset();
					return true;
				} 

				if (args[0].equalsIgnoreCase("board")) {
					if (!arena.isEnabled()) {
						throw new CommandException("The arena is not enabled.");
					}
					
					if (game.arenaBoard()) {
						for (String entry : arena.sb.getEntries()) {
							arena.resetScore(entry);
						}
						
						PlayerUtils.broadcast(Arena.PREFIX + "The arena board has been disabled.");
						game.setArenaBoard(false);
						
						board.getKillsObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
					} else {
						PlayerUtils.broadcast(Arena.PREFIX + "The arena board has been enabled.");
						arena.killboard.setDisplaySlot(DisplaySlot.SIDEBAR);
						
						game.setPregameBoard(false);
						game.setArenaBoard(true);

						arena.setScore("§8» §c§oPvE", 1);
						arena.setScore("§8» §c§oPvE", 0);
					}
					return true;
				}
			}
		}
		
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can use the arena.");
		}
		
		Player player = (Player) sender;
		
		if (args.length > 0 && args[0].equalsIgnoreCase("leave")) {
			if (!arena.isEnabled()) {
				throw new CommandException("The arena is currently disabled.");
			}
			
			if (!arena.hasPlayer(player)) {
				throw new CommandException("You are not in the arena.");
			}
			
			arena.removePlayer(player, false);;
			return true;
		}
		
		if (!arena.isEnabled()) {
			throw new CommandException("The arena is currently disabled.");
		}

		if (arena.hasPlayer(player)) {
			throw new CommandException("You are already in the arena.");
		}
		
		arena.addPlayer(player);
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			toReturn.add("leave");
			
			if (sender.hasPermission("uhc.arena.admin")) {
				toReturn.add("enable");
				toReturn.add("disable");
				toReturn.add("reset");
				toReturn.add("board");
			}
		}
		
		return toReturn;
	}
}