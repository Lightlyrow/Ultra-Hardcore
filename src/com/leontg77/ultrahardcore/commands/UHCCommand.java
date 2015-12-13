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
	
	/**
	 * Get the name of the command used after the /
	 * 
	 * @return The command name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the usage of the command
	 * <p>
	 * Usage can be /nameofcommand [argurments...]
	 * 
	 * @return The command usage.
	 */
	public String getUsage() {
		return "/" + name + " " + usage;
	}
	
	/**
	 * Return the permission of the command
	 * <p>
	 * The permission will be uhc.[nameofcommand]
	 * 
	 * @return The command permission.
	 */
	public String getPermission() {
		return "uhc." + name;
	}
	
	/**
	 * Execute the command.
	 * 
	 * @param sender The sender of the command.
	 * @param args The argurments typed after the command.
	 * @return True if successful, false otherwise. Returning false will send usage to the sender.
	 * 
	 * @throws CommandException If anything was wrongly typed this is thrown sending the sender a warning.
	 */
	public abstract boolean execute(CommandSender sender, String[] args) throws CommandException;
	
	/**
	 * Tab complete the command.
	 * 
	 * @param sender The sender of the command.
	 * @param args The argurments typed after the command
	 * @return A list of tab completable argurments.
	 */
	public abstract List<String> tabComplete(CommandSender sender, String[] args);
	
	/**
	 * Parse an int from the given string.
	 * 
	 * @param parse The string to parse.
	 * @return The int parsed.
	 * 
	 * @throws CommandException If the string typed isn't an int, this will send the sender a message.
	 */
	public int parseInt(String parse) throws CommandException {
		return parseInt(parse, "number");
	}
	
	/**
	 * Parse an double from the given string.
	 * 
	 * @param parse The string to parse.
	 * @return The double parsed.
	 * 
	 * @throws CommandException If the string typed isn't an double, this will send the sender a message.
	 */
	public double parseDouble(String parse) throws CommandException {
		return parseDouble(parse, "number");
	}
	
	/**
	 * Parse an long from the given string.
	 * 
	 * @param parse The string to parse.
	 * @return The long parsed.
	 * 
	 * @throws CommandException If the string typed isn't an long, this will send the sender a message.
	 */
	public long parseLong(String parse) throws CommandException {
		return parseLong(parse, "number");
	}
	
	/**
	 * Parse an int from the given string.
	 * 
	 * @param parse The string to parse.
	 * @param criteria What the int is used for.
	 * @return The int parsed.
	 * 
	 * @throws CommandException If the string typed isn't an int, this will send the sender a message.
	 */
	public int parseInt(String parse, String criteria) throws CommandException {
		try {
			return Integer.parseInt(parse);
		} catch (Exception e) {
			throw new CommandException("'" + parse + "' is not a vaild " + criteria + ".");
		}
	}
	
	/**
	 * Parse an double from the given string.
	 * 
	 * @param parse The string to parse.
	 * @param criteria What the double is used for.
	 * @return The double parsed.
	 * 
	 * @throws CommandException If the string typed isn't an double, this will send the sender a message.
	 */
	public double parseDouble(String parse, String criteria) throws CommandException {
		try {
			return Double.parseDouble(parse);
		} catch (Exception e) {
			throw new CommandException("'" + parse + "' is not a vaild " + criteria + ".");
		}
	}
	
	/**
	 * Parse an long from the given string.
	 * 
	 * @param parse The string to parse.
	 * @param criteria What the long is used for.
	 * @return The long parsed.
	 * 
	 * @throws CommandException If the string typed isn't an long, this will send the sender a message.
	 */
	public long parseLong(String parse, String criteria) throws CommandException {
		try {
			return Long.parseLong(parse);
		} catch (Exception e) {
			throw new CommandException("'" + parse + "' is not a vaild " + criteria + ".");
		}
	}
	
	/**
	 * Parse an boolean from the given string.
	 * 
	 * @param parse The string to parse.
	 * @param criteria What the boolean is used for.
	 * @return The boolean parsed.
	 * 
	 * @throws CommandException If the string typed isn't an boolean, this will send the sender a message.
	 */
	public boolean parseBoolean(String parse, String criteria) throws CommandException {
		if (parse.equalsIgnoreCase("true") || parse.equalsIgnoreCase("on")) {
			return true;
		} 

		if (parse.equalsIgnoreCase("false") || parse.equalsIgnoreCase("off")) {
			return false;
		}

		throw new CommandException(NameUtils.capitalizeString(criteria, false) + " can only be 'true' or 'false', not '" + parse + "'.");
	}
	
	/**
	 * Turn a the given boolean into "Enabled" or "Disabled".
	 * 
	 * @param converting The boolean converting.
	 * @return The converted boolean.
	 */
	public String booleanToString(boolean converting) {
		return converting ? "enabled" : "disabled";
	}
}