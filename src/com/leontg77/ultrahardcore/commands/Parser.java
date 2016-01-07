package com.leontg77.ultrahardcore.commands;

import com.leontg77.ultrahardcore.utils.NameUtils;

/**
 * Class for parsing things.
 * 
 * @author LeonTG77
 */
public class Parser {

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
	 * Parse an float from the given string.
	 * 
	 * @param parse The string to parse.
	 * @return The float parsed.
	 * 
	 * @throws CommandException If the string typed isn't an float, this will send the sender a message.
	 */
	public float parseFloat(String parse) throws CommandException {
		return parseFloat(parse, "number");
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
	 * Parse an float from the given string.
	 * 
	 * @param parse The string to parse.
	 * @param criteria What the long is used for.
	 * @return The float parsed.
	 * 
	 * @throws CommandException If the string typed isn't an float, this will send the sender a message.
	 */
	public float parseFloat(String parse, String criteria) throws CommandException {
		try {
			return Float.parseFloat(parse);
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
}