package com.leontg77.ultrahardcore.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

/**
 * Number utilities class.
 * <p>
 * Contains number related methods.
 * 
 * @author LeonTG77
 */
public class NumberUtils {
	public static final int TICKS_PER_SECOND = 20;
	public static final int TICKS_PER_MIN = TICKS_PER_SECOND * 60;
	public static final int TICKS_PER_HOUR = TICKS_PER_MIN * 60;
	public static final int TICKS_PER_DAY = TICKS_PER_HOUR * 24;
	public static final int TICKS_IN_999_DAYS = TICKS_PER_DAY * 999;
	
	/**
	 * Format the given double to a less lengthed one.
	 * 
	 * @param number the double to format.
	 * @return The formated double.
	 */
	public static String formatDouble(double number) {
		NumberFormat formater = new DecimalFormat("##.##");
		return formater.format(number);
	}

	/**
	 * Format the given int to a int with ,'s in it.
	 * 
	 * @param number the int to format.
	 * @return The formated integer.
	 */
	public static String formatInt(int number) {
		NumberFormat formater = NumberFormat.getInstance(Locale.UK);
		return formater.format(number);
	}
	
	/**
	 * Turn the health given into percent.
	 * 
	 * @param health the health of the player.
	 * @return the percent of the health.
	 */
	public static String makePercent(double health) {
		double hearts = health / 2;
		double percent = hearts * 10;
		
		if (percent >= 66) {
			return "§a" + ((int) percent);
		} else if (percent >= 33) {
			return "§e" + ((int) percent);
		} else if (percent == 0) {
			return "§7" + ((int) percent);
		} else {
			return "§c" + ((int) percent);
		}
	}
	
	public static int get999DaysInTicks() {
		return TICKS_IN_999_DAYS;
	}
	
	/**
	 * Get a random integer between two ints.
	 * 
	 * @param min minimum integer value.
	 * @param max maximum integer value.
	 * @return Random integer between two ints.
	 */
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}