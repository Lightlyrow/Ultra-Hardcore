package com.leontg77.ultrahardcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.commands.arena.ArenaCommand;
import com.leontg77.ultrahardcore.commands.arena.HotbarCommand;
import com.leontg77.ultrahardcore.commands.banning.BanCommand;
import com.leontg77.ultrahardcore.commands.banning.BanIPCommand;
import com.leontg77.ultrahardcore.commands.banning.DQCommand;
import com.leontg77.ultrahardcore.commands.banning.KickCommand;
import com.leontg77.ultrahardcore.commands.banning.MuteCommand;
import com.leontg77.ultrahardcore.commands.banning.TempbanCommand;
import com.leontg77.ultrahardcore.commands.banning.UnbanCommand;
import com.leontg77.ultrahardcore.commands.banning.UnbanIPCommand;
import com.leontg77.ultrahardcore.commands.basic.BroadcastCommand;
import com.leontg77.ultrahardcore.commands.basic.ButcherCommand;
import com.leontg77.ultrahardcore.commands.basic.EditCommand;
import com.leontg77.ultrahardcore.commands.basic.FireCommand;
import com.leontg77.ultrahardcore.commands.basic.IgnoreCommand;
import com.leontg77.ultrahardcore.commands.basic.ListCommand;
import com.leontg77.ultrahardcore.commands.basic.ParkourCommand;
import com.leontg77.ultrahardcore.commands.basic.SetspawnCommand;
import com.leontg77.ultrahardcore.commands.basic.SkullCommand;
import com.leontg77.ultrahardcore.commands.basic.StaffChatCommand;
import com.leontg77.ultrahardcore.commands.basic.TextCommand;
import com.leontg77.ultrahardcore.commands.game.BoardCommand;
import com.leontg77.ultrahardcore.commands.game.ChatCommand;
import com.leontg77.ultrahardcore.commands.game.ConfigCommand;
import com.leontg77.ultrahardcore.commands.game.EndCommand;
import com.leontg77.ultrahardcore.commands.game.HelpopCommand;
import com.leontg77.ultrahardcore.commands.game.MatchpostCommand;
import com.leontg77.ultrahardcore.commands.game.ScenarioCommand;
import com.leontg77.ultrahardcore.commands.game.SpreadCommand;
import com.leontg77.ultrahardcore.commands.game.StartCommand;
import com.leontg77.ultrahardcore.commands.game.TimeLeftCommand;
import com.leontg77.ultrahardcore.commands.game.TimerCommand;
import com.leontg77.ultrahardcore.commands.game.VoteCommand;
import com.leontg77.ultrahardcore.commands.game.WhitelistCommand;
import com.leontg77.ultrahardcore.commands.give.GiveCommand;
import com.leontg77.ultrahardcore.commands.give.GiveallCommand;
import com.leontg77.ultrahardcore.commands.inventory.GameInfoCommand;
import com.leontg77.ultrahardcore.commands.inventory.HOFCommand;
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
import com.leontg77.ultrahardcore.commands.spectate.BackCommand;
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
import com.leontg77.ultrahardcore.commands.user.InfoIPCommand;
import com.leontg77.ultrahardcore.commands.user.RankCommand;
import com.leontg77.ultrahardcore.commands.user.StatsCommand;
import com.leontg77.ultrahardcore.commands.user.TopCommand;
import com.leontg77.ultrahardcore.commands.world.BorderCommand;
import com.leontg77.ultrahardcore.commands.world.PregenCommand;
import com.leontg77.ultrahardcore.commands.world.PvPCommand;
import com.leontg77.ultrahardcore.commands.world.WorldCommand;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.FireworkManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.world.WorldManager;

/**
 * Command handler class.
 * 
 * @author LeonTG77
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
	private final Main plugin;
	
	public CommandHandler(Main plugin) {
		this.plugin = plugin;
	}
	
	private List<UHCCommand> cmds = new ArrayList<UHCCommand>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		UHCCommand command = getCommand(cmd.getName());
		
		if (command == null) {
			// this shouldn't happen, it only uses registered commands.
			return true;
		}
		
		// if they don't have permission, tell them so and stop
		if (!sender.hasPermission(command.getPermission())) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
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
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (sender instanceof Player && !((Player) sender).canSee(online)) {
				continue;
			}
			
			list.add(online.getName());
		}
		
		return list;
	}
	
	/**
	 * Register all the commands.
	 */
	public void registerCommands(Game game, Arena arena, Parkour parkour, Settings settings, GUIManager gui, BoardManager board, SpecManager spec, FeatureManager feat, ScenarioManager scen, WorldManager manager, Timer timer, TeamManager teams, FireworkManager firework) {
		// arena
		cmds.add(new ArenaCommand(arena, game, board));
		cmds.add(new HotbarCommand());
		
		// banning
		cmds.add(new BanCommand(board));
		cmds.add(new BanIPCommand(board));
		cmds.add(new DQCommand(board));
		cmds.add(new KickCommand());
		cmds.add(new MuteCommand());
		cmds.add(new TempbanCommand(board));
		cmds.add(new UnbanCommand());
		cmds.add(new UnbanIPCommand());
		
		// basic
		cmds.add(new BroadcastCommand());
		cmds.add(new ButcherCommand(game));
		cmds.add(new EditCommand());
		cmds.add(new FireCommand(firework));
		cmds.add(new IgnoreCommand());
		cmds.add(new ListCommand(game));
		cmds.add(new ParkourCommand(plugin, settings, parkour));
		cmds.add(new SetspawnCommand(settings));
		cmds.add(new SkullCommand());
		cmds.add(new StaffChatCommand());
		cmds.add(new TextCommand());
		
		// game
		cmds.add(new BoardCommand(arena, game, board));
		cmds.add(new ChatCommand(game));
		cmds.add(new ConfigCommand(game, gui, feat, scen));
		cmds.add(new EndCommand(plugin, timer, settings, game, scen, board, teams, spec, gui, firework, manager));
		cmds.add(new HelpopCommand());
		cmds.add(new MatchpostCommand());
		cmds.add(new ScenarioCommand());
		cmds.add(new SpreadCommand());
		cmds.add(new StartCommand());
		cmds.add(new TimeLeftCommand());
		cmds.add(new TimerCommand());
		cmds.add(new VoteCommand());
		cmds.add(new WhitelistCommand());
		
		// give
		cmds.add(new GiveallCommand());
		cmds.add(new GiveCommand());
		
		// inventory
		cmds.add(new HOFCommand(game, settings, gui));
		cmds.add(new GameInfoCommand(gui));
		
		// lag
		cmds.add(new MsCommand());
		cmds.add(new TpsCommand(plugin));
		
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
		cmds.add(new BackCommand());
		cmds.add(new InvseeCommand());
		cmds.add(new NearCommand());
		cmds.add(new SpectateCommand(spec));
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
		cmds.add(new InfoIPCommand());
		cmds.add(new RankCommand());
		cmds.add(new StatsCommand());
		cmds.add(new TopCommand());
		
		// world
		cmds.add(new BorderCommand());
		cmds.add(new PregenCommand());
		cmds.add(new PvPCommand());
		cmds.add(new WorldCommand(game, settings, gui, manager));
		
		for (UHCCommand cmd : cmds) {
			PluginCommand pCmd = plugin.getCommand(cmd.getName());
			
			// if its null, broadcast the command name so I know which one it is (so I can fix it).
			if (pCmd == null) {
				PlayerUtils.broadcast(cmd.getName());
				continue;
			}
			
			pCmd.setExecutor(this);
			pCmd.setTabCompleter(this);
		}
	}
}