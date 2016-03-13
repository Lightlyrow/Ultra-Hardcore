package com.leontg77.ultrahardcore.commands.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;

/**
 * UHC command class.
 * 
 * @author LeonTG77
 */
public class GameInfoCommand extends UHCCommand {
	private final GUIManager gui;

	public GameInfoCommand(GUIManager gui) {
		super("uhc", "");
		
		this.gui = gui;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can view the game info inventory.");
		}
		
		Player player = (Player) sender;
		
		player.openInventory(gui.getGUI(GameInfoGUI.class).get());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}