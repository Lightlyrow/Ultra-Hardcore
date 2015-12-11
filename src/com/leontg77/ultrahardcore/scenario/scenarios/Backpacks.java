package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Backpacks scenario class
 * 
 * @author LeonTG77
 */
public class Backpacks extends Scenario implements Listener, CommandExecutor {
	
	public Backpacks() {
		super("Backpacks", "Players can type /bp to open up a backpack inventory.");
		
		Bukkit.getPluginCommand("bp").setExecutor(this);
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Block block = player.getLocation().add(0, -1, 0).getBlock();
		
		block.setType(Material.CHEST);
		block.getState().update();
		
		Chest chest = (Chest) block.getState();
		chest.getInventory().setContents(player.getEnderChest().getContents());
		
		player.getEnderChest().clear();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use backpacks.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!isEnabled()) {
			player.sendMessage(ChatColor.GOLD + "Backbacks is not enabled.");
			return true;
		}
		
		if (!State.isState(State.INGAME)) {
			player.sendMessage(ChatColor.RED + "The game hasn't started!");
			return true;
		}

		player.openInventory(player.getEnderChest());
		return true;
	}
}