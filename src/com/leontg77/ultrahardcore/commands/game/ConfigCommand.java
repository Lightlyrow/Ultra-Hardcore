package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Main.BorderShrink;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Config command class.
 * 
 * @author LeonTG77
 */
public class ConfigCommand extends UHCCommand {

	/**
	 * ConfigValue class
	 * <p>
	 * Class used for the config command to 
	 * get all the possible config options.
	 * 
	 * @author LeonTG77
	 */
	public enum ConfigValue {
		APPLERATES, BORDERSHRINK, FLINTRATES, HEADSHEAL, HOST, MATCHPOST, MAXPLAYERS, MEETUP, PEARLDAMAGE, PVP, RRNAME, SCENARIOS, SHEARRATES, STATE, TEAMSIZE, WORLD;
	}

	public ConfigCommand() {
		super("config", "<option> <value>");
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				return false;
			}
			
			Player player = (Player) sender;

			InvGUI inv = InvGUI.getInstance();
			inv.openConfigOptions(player);
			return true;
		}
		
		if (args.length == 1) {
			return false;
		}
		
		ConfigValue type;
		
		try {
			type = ConfigValue.valueOf(args[0].toUpperCase());
		} catch (Exception e) {
			StringBuilder types = new StringBuilder();
			int i = 1;
			
			for (ConfigValue value : ConfigValue.values()) {
				if (types.length() > 0) {
					if (i == ConfigValue.values().length) {
						types.append(" §7and§a ");
					} else {
						types.append("§7, §a");
					}
				}
				
				types.append(value.name().toLowerCase());
				i++;
			}
			
			sender.sendMessage(Main.PREFIX + "Available config types: §a" + types.toString().trim() + "§7.");
			return true;
		}
		
		switch (type) {
		case APPLERATES:
			double appleRate = parseDouble(args[1], "apple rate");
			
			if (appleRate < 0) {
				throw new CommandException("Apple rates cannot be lower than 0%");
			}
			
			if (appleRate > 100) {
				throw new CommandException("Apple rates cannot be higher than 100%");
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Apple rates has been changed to §a" + NumberUtils.formatDouble(appleRate) + "%");
			game.setAppleRates(appleRate);
			break;
		case BORDERSHRINK:
			BorderShrink border;
			
			try {
				border = BorderShrink.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				throw new CommandException("'" + args[1] + "' is not a vaild border shink.");
			}
			
			if (border == BorderShrink.NEVER) {
				PlayerUtils.broadcast(Main.PREFIX + "Border will no longer shrink.");
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Border will now shrink " + border.getPreText() + border.name().toLowerCase());
			}
			
			game.setBorderShrink(border);
			break;
		case STATE:
			State state;
			
			try {
				state = State.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				throw new CommandException("'" + args[1] + "' is not a vaild state.");
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "The server is now in §a" + state.name().toLowerCase() + "§7 mode.");
			State.setState(state);
			break;
		case FLINTRATES:
			double flintRate = parseDouble(args[1], "flint rate");
			
			if (flintRate < 0) {
				throw new CommandException("Flint rates cannot be lower than 0%");
			}
			
			if (flintRate > 100) {
				throw new CommandException("Flint rates cannot be higher than 100%");
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Flint rates has been changed to §a" + NumberUtils.formatDouble(flintRate) + "%");
			game.setFlintRates(flintRate);
			break;
		case HEADSHEAL:
			double headheals = parseDouble(args[1], "heal amount");
			
			PlayerUtils.broadcast(Main.PREFIX + "Golden heads now heal §a" + NumberUtils.formatDouble(headheals) + "§7 hearts.");
			game.setGoldenHeadsHeal(headheals);
			break;
		case HOST:
			PlayerUtils.broadcast(Main.PREFIX + "The host has been changed to §a" + args[1] + "§7.");
			game.setHost(args[1]);
			break;
		case MATCHPOST:
			PlayerUtils.broadcast(Main.PREFIX + "The matchpost has been changed to §a" + args[1] + "§7.");
			game.setMatchPost(args[1]);
			break;
		case MAXPLAYERS:
			int maxplayers = parseInt(args[1], "maxplayers time");
			
			if (maxplayers > Bukkit.getMaxPlayers()) {
				sender.sendMessage(ChatColor.RED + "You cannot set the slots higher than the servers max.");
				return true;
			}
			
			if (maxplayers < 1) {
				sender.sendMessage(ChatColor.RED + "You cannot set the slots lower than 1.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "The max player limit is now §a" + maxplayers + "§7.");
			game.setMaxPlayers(maxplayers);
			break;
		case MEETUP:
			int meetup = parseInt(args[1], "meetup time");

			PlayerUtils.broadcast(Main.PREFIX + "Meetup is now §a" + meetup + " §7minutes in.");
			game.setMeetup(meetup);
			break;
		case PVP:
			int pvp = parseInt(args[1], "pvp time");
			
			PlayerUtils.broadcast(Main.PREFIX + "PvP will now be enabled §a" + pvp + " §7minutes in.");
			game.setPvP(pvp);
			break;
		case PEARLDAMAGE:
			double damage = parseDouble(args[1], "damage amount");

			if (damage == 0.0) {
				PlayerUtils.broadcast(Main.PREFIX + "Ender pearls will no longer deal damage.");
			} else {
				PlayerUtils.broadcast(Main.PREFIX + "Ender pearls will now deal §a" + NumberUtils.formatDouble(damage) + "§7 hearts.");
			}
			
			game.setPearlDamage(damage);
			break;
		case RRNAME:
			String rrname = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
			
			game.setRRName(rrname);
			PlayerUtils.broadcast(Main.PREFIX + "The recorded round is now called §a" + game.getRRName() + "§7.");
			break;
		case SCENARIOS:
			String scens = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
			
			game.setScenarios(scens);
			PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + GameUtils.getTeamSize(true, true) + game.getScenarios() + "§7.");
			break;
		case SHEARRATES:
			double shearRate = parseDouble(args[1], "shear rate");
			
			if (shearRate < 0) {
				throw new CommandException("Shear rates cannot be lower than 0%");
			}
			
			if (shearRate > 100) {
				throw new CommandException("Shear rates cannot be higher than 100%");
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Shear rates has been changed to §a" + NumberUtils.formatDouble(shearRate) + "%");
			game.setShearRates(shearRate);
			break;
		case TEAMSIZE:
			game.setTeamSize(args[1]);
			PlayerUtils.broadcast(Main.PREFIX + "The gamemode is now §a" + GameUtils.getTeamSize(true, true) + game.getScenarios() + "§7.");
			break;
		case WORLD:
			PlayerUtils.broadcast(Main.PREFIX + "The game will now be played in '§a" + args[1] + "§7'.");
			game.setWorld(args[1]);
			break;
		default:
			return true;
		}
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
    		for (ConfigValue type : ConfigValue.values()) {
    			toReturn.add(type.name().toLowerCase());
    		}
        }
    	
		if (args.length == 2) {
			ConfigValue configType;
			
			try {
				configType = ConfigValue.valueOf(args[0].toUpperCase());
			} catch (Exception e) {
				return toReturn;
			}
			
    		switch (configType) {
			case BORDERSHRINK:
	    		for (BorderShrink type : BorderShrink.values()) {
	    			toReturn.add(type.name().toLowerCase());
	    		}
				break;
			case HOST:
	    		for (Player online : PlayerUtils.getPlayers()) {
	    			Rank rank = User.get(online).getRank();
	    			
	    			if (rank.getLevel() > 4) {
	    				toReturn.add(online.getName());
	    			}
	    		}
				break;
			case MATCHPOST:
				toReturn.add("https://redd.it/######");
				break;
			case SCENARIOS:
	    		for (Scenario type : ScenarioManager.getInstance().getScenarios()) {
	    			toReturn.add(type.getName());
	    		}
				break;
			case STATE:
	    		for (State type : State.values()) {
	    			toReturn.add(type.name().toLowerCase());
	    		}
				break;
			case TEAMSIZE:
				toReturn.add("FFA");
				toReturn.add("cTo");
				toReturn.add("rTo");
				toReturn.add("pTo");
				toReturn.add("mTo");
				toReturn.add("CapTo");
				toReturn.add("Auction");
				toReturn.add("No");
				toReturn.add("Open");
				break;
			case WORLD:
				for (World world : Bukkit.getWorlds()) {
					toReturn.add(world.getName());
				}
				break;
			default:
				break;
    		}
        }
		
    	return toReturn;
	}
}