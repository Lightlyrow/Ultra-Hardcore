package com.leontg77.ultrahardcore.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.utils.NameUtils;

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
	
	public int parseInt(String parse) throws CommandException {
		return parseInt(parse, "number");
	}
	
	public double parseDouble(String parse) throws CommandException {
		return parseDouble(parse, "number");
	}
	
	public long parseLong(String parse) throws CommandException {
		return parseLong(parse, "number");
	}
	
	public int parseInt(String parse, String criteria) throws CommandException {
		try {
			return Integer.parseInt(parse);
		} catch (Exception e) {
			throw new CommandException("'" + parse + "' is not a vaild " + criteria + ".");
		}
	}
	
	public double parseDouble(String parse, String criteria) throws CommandException {
		try {
			return Double.parseDouble(parse);
		} catch (Exception e) {
			throw new CommandException("'" + parse + "' is not a vaild " + criteria + ".");
		}
	}
	
	public long parseLong(String parse, String criteria) throws CommandException {
		try {
			return Long.parseLong(parse);
		} catch (Exception e) {
			throw new CommandException("'" + parse + "' is not a vaild " + criteria + ".");
		}
	}
	
	public boolean parseBoolean(String parse, String criteria) throws CommandException {
		if (parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("on")) {
			return true;
		} 

		if (parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("off")) {
			return false;
		}

		throw new CommandException(NameUtils.fixString(criteria, false) + " can only be 'true' or 'false', not '" + parse + "'.");
	}
}