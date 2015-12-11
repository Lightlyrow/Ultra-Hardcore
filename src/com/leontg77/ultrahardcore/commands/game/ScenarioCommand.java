package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Scenario command class.
 * 
 * @author LeonTG77
 */
public class ScenarioCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		ScenarioManager manager = ScenarioManager.getInstance();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("enable")) {
				if (sender.hasPermission("uhc.scenario")) {
					if (args.length == 1) {
						sender.sendMessage(Main.PREFIX + "Usage: /scen enable <scenario>");
						return true;
					}
					
					Scenario scen = manager.getScenario(args[1]);
					
					if (scen == null) {
						sender.sendMessage(Main.PREFIX + args[1] + " is not a scenario.");
						return true;
					}
					
					if (scen.isEnabled()) {
						sender.sendMessage(Main.PREFIX + "§a" + scen.getName() + " §7is already enabled.");
						return true;
					}

					PlayerUtils.broadcast(Main.PREFIX + "§a" + scen.getName() + " §7has been enabled.");
					scen.setEnabled(true);
					return true;
				}
			} 

			if (args[0].equalsIgnoreCase("disable")) {
				if (sender.hasPermission("uhc.scenario")) {
					if (args.length == 1) {
						sender.sendMessage(Main.PREFIX + "Usage: /scen disable <scenario>");
						return true;
					}
					
					Scenario scen = manager.getScenario(args[1]);
					
					if (scen == null) {
						sender.sendMessage(Main.PREFIX + args[1] + " is not a scenario.");
						return true;
					}
					
					if (!scen.isEnabled()) {
						sender.sendMessage(Main.PREFIX + "§a" + scen.getName() + " §7is not enabled.");
						return true;
					}

					PlayerUtils.broadcast(Main.PREFIX + "§a" + scen.getName() + " §7has been disabled.");
					scen.setEnabled(false);
					return true;
				}
			} 

			if (args[0].equalsIgnoreCase("list")) {
				StringBuilder list = new StringBuilder("");
				int i = 1;
				
				for (Scenario scen : manager.getScenarios()) {
					if (list.length() > 0) {
						if (i == manager.getScenarios().size()) {
							list.append("§8 and §7");
						} else {
							list.append("§8, §7");
						}
					}
					
					list.append((scen.isEnabled() ? "§a" : "§c") + scen.getName());
					i++;
				}
				
				sender.sendMessage(Main.PREFIX + "List of all scenarios: §8(§6" + manager.getScenarios().size() + "§8)");
				sender.sendMessage("§8» §7" + list.toString().trim());
				return true;
			}

			if (args[0].equalsIgnoreCase("info")) {
				if (args.length == 1) {
					sender.sendMessage(Main.PREFIX + "Usage: /scen info <scenario>");
					return true;
				}

				Scenario scen = manager.getScenario(args[1]);
				
				if (scen == null) {
					sender.sendMessage(Main.PREFIX + args[1] + " is not a scenario.");
					return true;
				}
				
				sender.sendMessage(Main.PREFIX + "Information about §6" + scen.getName() + "§8:");
				sender.sendMessage("§8» §f§o" + scen.getDescription());
				return true;
			}
		}
		
		int size = manager.getEnabledScenarios().size();
		
       	sender.sendMessage(Main.PREFIX + "Currently enabled scenarios: §8(§6" + (size == 0 ? 1 : size) + "§8)");
		
		if (size == 0) {
       		sender.sendMessage("§8» §7Vanilla+ §8- §f§oA normal UHC with a few changes.");
			return true;
		}
       	
       	for (Scenario scen : manager.getEnabledScenarios()) {
       		sender.sendMessage("§8» §7" + scen.getName() + " §8- §f§o" + scen.getDescription());
       	}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ScenarioManager manager = ScenarioManager.getInstance();
    	ArrayList<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	ArrayList<String> types = new ArrayList<String>();
        	types.add("list");
        	types.add("info");
        	
        	if (sender.hasPermission("uhc.scenario")) {
	        	types.add("enable");
	        	types.add("disable");
        	}
        	
        	if (args[0].equals("")) {
        		for (String type : types) {
        			toReturn.add(type);
        		}
        	} else {
        		for (String type : types) {
        			if (type.startsWith(args[0].toLowerCase())) {
        				toReturn.add(type);
        			}
        		}
        	}
        }
		
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("info")) {
	        	if (args[1].equals("")) {
	        		for (Scenario scen : manager.getScenarios()) {
        				toReturn.add(scen.getName());
	        		}
	        	} else {
	        		for (Scenario scen : manager.getScenarios()) {
	        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(scen.getName());
	        			}
	        		}
	        	}
        	}
			
        	if (!sender.hasPermission("uhc.scenario")) {
	        	return null;
        	}
        	
        	if (args[0].equalsIgnoreCase("enable")) {
        		if (args[1].equals("")) {
	        		for (Scenario scen : manager.getDisabledScenarios()) {
        				toReturn.add(scen.getName());
	        		}
	        	} else {
	        		for (Scenario scen : manager.getDisabledScenarios()) {
	        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(scen.getName());
	        			}
	        		}
	        	}
        	} else if (args[0].equalsIgnoreCase("disable")) {
	        	if (args[1].equals("")) {
	        		for (Scenario scen : manager.getEnabledScenarios()) {
        				toReturn.add(scen.getName());
	        		}
	        	}
	        	else {
	        		for (Scenario scen : manager.getEnabledScenarios()) {
	        			if (scen.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
	        				toReturn.add(scen.getName());
	        			}
	        		}
	        	}
        	}
		}
    	return toReturn;
	}
}