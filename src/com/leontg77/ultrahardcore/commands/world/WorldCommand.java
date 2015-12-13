package com.leontg77.ultrahardcore.commands.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.worlds.WorldManager;

/**
 * World command class.
 * 
 * @author LeonTG77
 */
public class WorldCommand extends UHCCommand {

	public WorldCommand() {
		super("world", "");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		WorldManager manager = WorldManager.getInstance();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length < 5) {
					sender.sendMessage(Main.PREFIX + "Usage: /world create <host> <mapradius> <nether> <end> [seed]");
					return true;
				}
				
				String worldname = args[1];
				int radius = parseInt(args[2], "radius");
				
				boolean nether = parseBoolean(args[3], "nether");
				boolean end = parseBoolean(args[4], "The End");
				
				long seed = new Random().nextLong();
				
				if (args.length > 5) {
					try {
						seed = parseLong(args[5]);
		            } catch (Exception e) {
		                seed = (long) args[5].hashCode();
		            }
				}
				
				sender.sendMessage(Main.PREFIX + "Starting creation of world '§a" + worldname + "§7'...");
				sender.sendMessage("§8» §7Radius: §6" + radius);
				sender.sendMessage("§8» §7Seed: §6" + seed);
				sender.sendMessage("§8» §7Type: §6" + WorldType.NORMAL);
				sender.sendMessage("§8» §7Nether: §6" + nether);
				sender.sendMessage("§8» §7End: §6" + end);
				
				manager.createWorld(worldname, radius, seed, Environment.NORMAL, WorldType.NORMAL);
				
				if (nether) {
					manager.createWorld(worldname + "_nether", radius, seed, Environment.NETHER, WorldType.NORMAL);
				}
				
				if (end) {
					manager.createWorld(worldname + "_end", radius, seed, Environment.THE_END, WorldType.NORMAL);
				}

				sender.sendMessage(Main.PREFIX + "World creation finished.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("delete")) {
				if (args.length == 1) {
					sender.sendMessage(Main.PREFIX + "Usage: /world delete <world>");
					return true;
				}
				
				World world = Bukkit.getWorld(args[1]);
				
				if (world == null) {
					throw new CommandException("The world '" + args[1] + "'  does not exist.");
				}
				
				if (world.getName().equals("lobby")) {
					throw new CommandException("You cannot delete the lobby world.");
				}

				if (!manager.deleteWorld(world)) {
					throw new CommandException("Could not delete world '" + world.getName() + "'.");
				}
				
				sender.sendMessage(Main.PREFIX + "World '§a" + world.getName() + "§7' has been deleted.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("tp")) {
				if (!(sender instanceof Player)) {
					throw new CommandException("Only players can teleport to worlds.");
				}
				
				Player player = (Player) sender;
				
				if (args.length == 1) {
					player.sendMessage(Main.PREFIX + "Usage: /world tp <world>");
					return true;
				}
				
				World world = Bukkit.getWorld(args[1]);
				
				if (world == null) {
					throw new CommandException("The world '" + args[1] + "'  does not exist.");
				}
				
				player.sendMessage(Main.PREFIX + "Teleported to world '§a" + world.getName() + "§7'.");
				player.teleport(world.getSpawnLocation());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("list")) {
				sender.sendMessage(Main.PREFIX + "List of all worlds: §8(§6" + Bukkit.getWorlds().size() + "§8)");
				
				for (World world : Bukkit.getWorlds()) {
					ChatColor color;
					
					switch (world.getEnvironment()) {
					case NETHER:
						color = ChatColor.RED;
						break;
					case NORMAL:
						color = ChatColor.GREEN;
						break;
					case THE_END:
						color = ChatColor.AQUA;
						break;
					default:
						return true;
					}
					
					sender.sendMessage("§8» §7" + world.getName() + " §8- " + color + world.getEnvironment().name());
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("load")) {
				if (args.length == 1) {
					sender.sendMessage(Main.PREFIX + "Usage: /world unload <world>");
					return true;
				}
				
				World world = Bukkit.getWorld(args[1]);
				
				if (world != null) {
					throw new CommandException("The world '" + args[1] + "'  is already loaded.");
				}
				
				manager.loadWorld(args[1]);
				sender.sendMessage(Main.PREFIX + "World '§a" + args[1] + "§7' has been loaded.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("unload")) {
				if (args.length == 1) {
					sender.sendMessage(Main.PREFIX + "Usage: /world unload <world>");
					return true;
				}
				
				World world = Bukkit.getWorld(args[1]);
				
				if (world == null) {
					throw new CommandException("The world '" + args[1] + "'  does not exist.");
				}
				
				if (world == Bukkit.getWorlds().get(0)) {
					throw new CommandException("You cannot unload the lobby world.");
				}
				
				sender.sendMessage(Main.PREFIX + "World '§a" + world.getName() + "§7' has been unloaded.");
				manager.unloadWorld(world);
				return true;
			}
		}
		
		sender.sendMessage(Main.PREFIX + "World management help:");
		sender.sendMessage("§8» §a/world create §8- §7§oCreate a world.");
		sender.sendMessage("§8» §a/world delete §8- §7§oDelete a world.");
		sender.sendMessage("§8» §a/world load §8- §7§oLoad a world.");
		sender.sendMessage("§8» §a/world unload §8- §7§oUnload a world.");
		sender.sendMessage("§8» §a/world list §8- §7§oList all worlds.");
		sender.sendMessage("§8» §a/world tp §8- §7§oTeleport to a world.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			toReturn.add("create");
			toReturn.add("delete");
			toReturn.add("list");
			toReturn.add("tp");
			toReturn.add("load");
			toReturn.add("unload");
		}
		
		if (args.length == 2) {
			switch (args[0].toLowerCase()) {
			case "create":
				for (String name : GameUtils.HOSTS_BY_NAME.keySet()) {
					toReturn.add(name.toLowerCase());
				}
				break;
			case "load":
			case "unload":
			case "tp":
			case "delete":
				for (World world : Bukkit.getWorlds()) {
					toReturn.add(world.getName());
				}
				break;
			}
		}
		
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("create")) {
				toReturn.add("1500");
				toReturn.add("1000");
			}
		}
		
		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("create")) {
				toReturn.add("true");
				toReturn.add("false");
			}
		}
		
		if (args.length == 5) {
			if (args[0].equalsIgnoreCase("create")) {
				toReturn.add("true");
				toReturn.add("false");
			}
		}
		
		if (args.length == 6) {
			if (args[0].equalsIgnoreCase("create")) {
				toReturn.add("" + new Random().nextLong());
			}
		}
		
		return toReturn;
	}
}