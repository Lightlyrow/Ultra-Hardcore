package com.leontg77.ultrahardcore.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

/**
 * Super class for commands.
 * 
 * @author LeonTG77
 */
public abstract class UHCCommand {
	private String name, usage;
	
	/**
	 * Constructor for the uhc command super class.
	 * 
	 * @param name The name of the command.
	 * @param usage the command usage (after /command)
	 */
	protected UHCCommand(String name, String usage) {
		this.usage = usage;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUsage() {
		return "/" + name + " " + usage;
	}
	
	public String getPermission() {
		return "uhc." + name;
	}
	
	public abstract boolean execute(CommandSender sender, String[] args) throws CommandException;
	
	public abstract List<String> tabComplete(CommandSender sender, String[] args);
}