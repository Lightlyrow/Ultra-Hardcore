package com.leontg77.ultrahardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.banning.MuteCommand;
import com.leontg77.ultrahardcore.commands.game.BoardCommand;
import com.leontg77.ultrahardcore.commands.game.ChatCommand;
import com.leontg77.ultrahardcore.commands.game.ConfigCommand;
import com.leontg77.ultrahardcore.commands.game.EndCommand;
import com.leontg77.ultrahardcore.commands.game.HelpopCommand;
import com.leontg77.ultrahardcore.commands.game.MatchpostCommand;
import com.leontg77.ultrahardcore.commands.game.ScenarioCommand;
import com.leontg77.ultrahardcore.commands.give.GiveCommand;
import com.leontg77.ultrahardcore.commands.give.GiveallCommand;
import com.leontg77.ultrahardcore.commands.inventory.HOFCommand;
import com.leontg77.ultrahardcore.commands.inventory.UHCCmd;
import com.leontg77.ultrahardcore.commands.lag.MsCommand;
import com.leontg77.ultrahardcore.commands.lag.TpsCommand;
import com.leontg77.ultrahardcore.commands.msg.MsgCommand;
import com.leontg77.ultrahardcore.commands.msg.ReplyCommand;
import com.leontg77.ultrahardcore.commands.player.ClearInvCommand;
import com.leontg77.ultrahardcore.commands.player.ClearXPCommand;
import com.leontg77.ultrahardcore.commands.player.FeedCommand;
import com.leontg77.ultrahardcore.commands.player.FlyCommand;
import com.leontg77.ultrahardcore.commands.player.GamemodeCommand;
import com.leontg77.ultrahardcore.commands.player.HealCommand;
import com.leontg77.ultrahardcore.commands.player.HealthCommand;
import com.leontg77.ultrahardcore.commands.player.SethealthCommand;
import com.leontg77.ultrahardcore.commands.player.SetmaxhealthCommand;
import com.leontg77.ultrahardcore.commands.spectate.InvseeCommand;
import com.leontg77.ultrahardcore.commands.spectate.NearCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpecChatCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpectateCommand;
import com.leontg77.ultrahardcore.commands.spectate.SpeedCommand;
import com.leontg77.ultrahardcore.commands.spectate.TpCommand;
import com.leontg77.ultrahardcore.commands.team.PmCommand;
import com.leontg77.ultrahardcore.commands.team.PmoresCommand;
import com.leontg77.ultrahardcore.commands.team.RandomCommand;
import com.leontg77.ultrahardcore.commands.team.TeamCommand;
import com.leontg77.ultrahardcore.commands.team.TlCommand;
import com.leontg77.ultrahardcore.commands.user.InfoCommand;
import com.leontg77.ultrahardcore.commands.user.RankCommand;
import com.leontg77.ultrahardcore.commands.user.StatsCommand;
import com.leontg77.ultrahardcore.commands.user.TopCommand;
import com.leontg77.ultrahardcore.commands.world.BorderCommand;
import com.leontg77.ultrahardcore.commands.world.PregenCommand;
import com.leontg77.ultrahardcore.commands.world.PvPCommand;
import com.leontg77.ultrahardcore.commands.world.WorldCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Command handler class.
 * 
 * @author LeonTG77
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
	private List<UHCCommand> cmds = new ArrayList<UHCCommand>();
	
	/**
	 * Register all the commands.
	 */
	public void registerCommands() {
		// banning
		cmds.add(new MuteCommand());
		
		// game
		cmds.add(new BoardCommand());
		cmds.add(new ChatCommand());
		cmds.add(new ConfigCommand());
		cmds.add(new EndCommand());
		cmds.add(new HelpopCommand());
		cmds.add(new MatchpostCommand());
		cmds.add(new ScenarioCommand());
		
		// give
		cmds.add(new GiveallCommand());
		cmds.add(new GiveCommand());
		
		// inventory
		cmds.add(new HOFCommand());
		cmds.add(new UHCCmd());
		
		// lag
		cmds.add(new MsCommand());
		cmds.add(new TpsCommand());
		
		// msg
		cmds.add(new MsgCommand());
		cmds.add(new ReplyCommand());
		
		// player
		cmds.add(new ClearInvCommand());
		cmds.add(new ClearXPCommand());
		cmds.add(new FeedCommand());
		cmds.add(new FlyCommand());
		cmds.add(new GamemodeCommand());
		cmds.add(new HealCommand());
		cmds.add(new HealthCommand());
		cmds.add(new SethealthCommand());
		cmds.add(new SetmaxhealthCommand());

		// spectate
		cmds.add(new InvseeCommand());
		cmds.add(new NearCommand());
		cmds.add(new SpectateCommand());
		cmds.add(new SpecChatCommand());
		cmds.add(new SpeedCommand());
		cmds.add(new TpCommand());
		
		// team
		cmds.add(new PmCommand());
		cmds.add(new PmoresCommand());
		cmds.add(new RandomCommand());
		cmds.add(new TeamCommand());
		cmds.add(new TlCommand());

		// user
		cmds.add(new InfoCommand());
		cmds.add(new RankCommand());
		cmds.add(new StatsCommand());
		cmds.add(new TopCommand());
		
		// world
		cmds.add(new BorderCommand());
		cmds.add(new PregenCommand());
		cmds.add(new PvPCommand());
		cmds.add(new WorldCommand());
		
		for (UHCCommand cmd : cmds) {
			PluginCommand pCmd = Main.plugin.getCommand(cmd.getName());
			
			// if its null, broadcast the command name so I know which one it is (so I can fix it).
			if (pCmd == null) {
				PlayerUtils.broadcast(cmd.getName());
				continue;
			}
			
			pCmd.setExecutor(this);
			pCmd.setTabCompleter(this);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		UHCCommand command = getCommand(cmd.getName());
		
		if (command == null) {
			// this shouldn't happen, it only uses registered commands.
			return true;
		}
		
		// if they don't have permission, tell them so and stop
		if (!sender.hasPermission(command.getPermission())) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		try {
			// if the command returned false, send the usage.
			if (!command.execute(sender, args)) {
				sender.sendMessage(Main.PREFIX + "Usage: " + command.getUsage());
			}
		} catch (CommandException ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
		} catch (Exception ex) {
			// send them the error message in red if anything failed.
			sender.sendMessage(ChatColor.RED + ex.getClass().getName() + ": " + ex.getMessage());
			ex.printStackTrace();
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		UHCCommand command = getCommand(cmd.getName());
		
		if (command == null) {
			// this shouldn't happen, it only uses registered commands.
			return null;
		}
		
		// if they don't have permission, stop
		if (!sender.hasPermission(command.getPermission())) {
			return null;
		}
		
		try {
			List<String> list = command.tabComplete(sender, args);
			
			// if the list is null, replace it with everyone online.
			if (list == null) {
				list = getAllPlayerNames(sender);
			}
			
			// I don't want anything done if the list is empty.
			if (list.isEmpty()) {
				return list;
			}
			
			List<String> toReturn = new ArrayList<String>();
			
			if (args[args.length - 1].isEmpty()) {
				for (String type : list) {
					toReturn.add(type);
				}
			} else {
				for (String type : list) {
					if (type.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
						toReturn.add(type);
					}
				}
			}
			
			return toReturn;
		} catch (Exception ex) {
			// send them the error message in red if anything failed.
			sender.sendMessage(ChatColor.RED + ex.getMessage());
		}
		return null;
	}

	/**
     * Get a uhc command.
     *
     * @param name The name of the uhc command
     * @return The UHCCommand if found, null otherwise.
     */
    protected UHCCommand getCommand(String name) {
        for (UHCCommand cmd : cmds) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                return cmd;
            }
        }
        
        return null;
    }
	
    /**
     * Get a list of all players online's names
     * 
     * @param sender The sender, if a player it will not add hidden players to the list, adds everyone if it's the console.
     * @return List of player names.
     */
	private List<String> getAllPlayerNames(CommandSender sender) {
		List<String> list = new ArrayList<String>();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (sender instanceof Player && !((Player) sender).canSee(online)) {
				continue;
			}
			
			list.add(online.getName());
		}
		
		return list;
	}
}