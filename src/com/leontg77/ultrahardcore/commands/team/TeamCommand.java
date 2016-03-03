package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Team command class.
 * 
 * @author LeonTG77
 */
public class TeamCommand extends UHCCommand {
	public static Map<String, List<String>> invites = new HashMap<String, List<String>>();
	
	private int teamsize = 0;
	
	public TeamCommand() {
		super("team", "");
	}
	
	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		final BoardManager board = BoardManager.getInstance();
		final TeamManager teams = TeamManager.getInstance();
		
		if (args.length == 0) {
			// the method returns true but hey, one less line :D
			return helpMenu(sender);
		}
		
		if (args.length > 1) {
			Player target = Bukkit.getPlayer(args[1]);
			
			if (args[0].equalsIgnoreCase("info")) {
				if (!sender.hasPermission("uhc.team.admin")) {
					return helpMenu(sender);
				}
				
				if (target == null) {
					throw new CommandException("'" + args[1] + "' is not online.");
				}

				Team team = teams.getTeam(target);

				sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + "'s §7team info:");
				sender.sendMessage("§8» §7Team: §c" + (team == null ? "None" : team.getPrefix() + team.getName()));
				sender.sendMessage("§8» §7Kills: §a" + board.getActualScore(target.getName()));
				
				if (team == null) {
					return true;
				}
				
				if (!teams.getSavedTeams().containsKey(team.getName())) {
					Set<String> players = new HashSet<String>(team.getEntries());
					teams.getSavedTeams().put(team.getName(), players);
				}
				
				StringBuilder list = new StringBuilder("");
				int i = 1;
				
				Set<String> savedTeam = teams.getSavedTeams().get(team.getName());
				
				int teamkills = 0;
				
				for (String entry : savedTeam) {
					if (list.length() > 0) {
						if (i == savedTeam.size()) {
							list.append(" §7and §f");
						} else {
							list.append("§7, §f");
						}
					}
					
					teamkills += board.getActualScore(entry);
					
					OfflinePlayer teammates = PlayerUtils.getOfflinePlayer(entry);
					
					if (teammates.isOnline()) {
						list.append(ChatColor.GREEN + teammates.getName());
					} else {
						list.append(ChatColor.RED + teammates.getName());
					}
					i++;
				}

				sender.sendMessage("§8» §7Team Kills: §a" + teamkills);
				sender.sendMessage("§8» ---------------------------");
				sender.sendMessage("§8» §7Teammates: §o(Names in red means they are offline)");
				sender.sendMessage("§8» §f" + list.toString().trim());
				return true;
			}
		
			if (args[0].equalsIgnoreCase("invite")) {
				if (!(sender instanceof Player)) {
					throw new CommandException("Only players can create and manage teams.");
				}
				
				Player player = (Player) sender;
				
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}
				
				Team team = teams.getTeam(player);
				
				if (team == null) {
					throw new CommandException("You are not on a team, create one using /team create.");
				}
				
				if (target == null) {
					throw new CommandException("'" + args[1] + "' is not online.");
				}
				
				if (team.getSize() >= teamsize) {
					throw new CommandException("Your team is currently full.");
				}

				Team team1 = teams.getTeam(target);
				
				if (team1 != null) {
					throw new CommandException("'" + target.getName() + "' is already on a team.");
				}
				
				teams.sendMessage(team, Main.PREFIX + ChatColor.GREEN + target.getName() + " §7has been invited to your team.");

				if (!invites.containsKey(sender)) {
					invites.put(player.getName(), new ArrayList<String>());
				}
				
				invites.get(sender.getName()).add(target.getName());
				target.sendMessage(Main.PREFIX + "You have been invited to §a" + sender.getName() + "'s §7team.");
				
				ComponentBuilder builder = new ComponentBuilder("");
				builder.append(Main.PREFIX + "§b§n§oClick here to accept the request.");
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + sender.getName()));
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to join " + sender.getName() + "'s team.") }));
				target.spigot().sendMessage(builder.create());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("kick")) {
				if (!(sender instanceof Player)) {
					throw new CommandException("Only players can create and manage teams.");
				}
				
				Player player = (Player) sender;
				
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}

				Team team = teams.getTeam(player);
				
				if (team == null) {
					throw new CommandException("You are not on a team.");
				}
				
				if (target == null) {
					throw new CommandException("'" + args[1] + "' is not online.");
				}
				
				if (!team.getEntries().contains(target.getName())) {
					throw new CommandException("'" + target.getName() + "' is not on your team.");
				}
				
				teams.leaveTeam(target, true);
				target.sendMessage(Main.PREFIX + "You got kicked out of your team.");
				teams.sendMessage(team, Main.PREFIX + ChatColor.GREEN + target.getName() + " §7was kicked from your team.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("accept")) {
				if (!(sender instanceof Player)) {
					throw new CommandException("Only players can create and manage teams.");
				}
				
				Player player = (Player) sender;
				
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}
				
				if (target == null) {
					throw new CommandException("'" + args[1] + "' is not online.");
				}
				
				if (teams.getTeam(player) != null) {
					throw new CommandException("You are already on a team.");
				}
				
				if (!invites.containsKey(target.getName()) || !invites.get(target.getName()).contains(sender.getName())) {
					sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7hasn't sent you any requests.");
					return true;
				}
				
				Team team = teams.getTeam(target);
				
				if (team == null) {
					throw new CommandException("'" + target.getName() + "' is not on a team");
				}
				
				if (team.getSize() >= teamsize) {
					throw new CommandException("That team is currently full.");
				}
				
				teams.sendMessage(team, Main.PREFIX + ChatColor.GREEN + sender.getName() + " §7joined your team.");
				sender.sendMessage(Main.PREFIX + "Request accepted, you joined their team.");
				teams.joinTeam(team, player);
				
				invites.get(target.getName()).remove(sender.getName());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("deny")) {
				if (!(sender instanceof Player)) {
					throw new CommandException("Only players can create and manage teams.");
				}
				
				Player player = (Player) sender;
				
				if (!game.teamManagement()) {
					sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
					return true;
				}
				
				if (target == null) {
					throw new CommandException("'" + args[1] + "' is not online.");
				}
				
				if (!invites.containsKey(target.getName()) || !invites.get(target.getName()).contains(player.getName())) {
					sender.sendMessage(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7hasn't sent you any requests.");
					return true;
				}
				
				target.sendMessage(Main.PREFIX + ChatColor.GREEN + player.getName() + " §7denied your request.");
				sender.sendMessage(Main.PREFIX + "Request denied.");
				invites.get(target.getName()).remove(player.getName());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("remove")) {
				if (!sender.hasPermission("uhc.team.admin")) {
					return helpMenu(sender);
				}
				
				OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);
				Team team = teams.getTeam(offline);
				
				if (team == null) {
					throw new CommandException("'" + offline.getName() + "' is not on a team.");
				}
				
				sender.sendMessage(Main.PREFIX + ChatColor.GREEN + offline.getName() + " §7was removed from his team.");
				teams.leaveTeam(offline, true);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("delete")) {
				if (!sender.hasPermission("uhc.team.admin")) {
					return helpMenu(sender);
				}
				
				Team team = teams.getTeam(args[1]);
				
				if (team == null) {
					throw new CommandException("'" + args[1] + "' is not a vaild team.");
				}
				
				for (OfflinePlayer player : teams.getPlayers(team)) {
					teams.leaveTeam(player, true);
				}
				
				sender.sendMessage(Main.PREFIX + "Team §a" + team.getName() + " §7has been deleted.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("friendlyfire")) {
				if (!sender.hasPermission("uhc.team.admin")) {
					return helpMenu(sender);
				}
				
				boolean enable = parseBoolean(args[1], "FriendlyFire");
				
				for (Team team : teams.getTeams()) {
					team.setAllowFriendlyFire(enable);
				}
				
				PlayerUtils.broadcast(Main.PREFIX + "FriendlyFire is now " + booleanToString(enable) + ".");
				return true;
			}
		}
	
		if (args.length > 2) {
			if (args[0].equalsIgnoreCase("add")) {
				if (!sender.hasPermission("uhc.team.admin")) {
					return helpMenu(sender);
				}
				
				Team team = teams.getTeam(args[1]);
				
				if (team == null) {
					throw new CommandException("'" + args[1] + "' is not a vaild team.");
				}
				
				OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[2]);
				teams.joinTeam(team, offline);
				
				sender.sendMessage(Main.PREFIX + ChatColor.GREEN + offline.getName() + "§7 was added to team " + team.getName() + ".");
				return true;
			} 
		}
		
		if (args[0].equalsIgnoreCase("create")) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can create and manage teams.");
			}
			
			Player player = (Player) sender;
			
			if (!game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
				return true;
			}
		
			if (teams.getTeam(player) != null) {
				throw new CommandException("You are already on a team.");
			}
			
			Team team = teams.findAvailableTeam();
			
			if (team == null) {
				throw new CommandException("There are no more available teams.");
			}
			
			teams.joinTeam(team, player);
			
			sender.sendMessage(Main.PREFIX + "Team created! Use §a/team invite <player>§7 to invite a player.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("leave")) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can create and manage teams.");
			}
			
			Player player = (Player) sender;
			
			if (!game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is currently disabled.");
				return true;
			}
			
			Team team = teams.getTeam(player);
			
			if (team == null) {
				throw new CommandException("You are not on a team.");
			}

			sender.sendMessage(Main.PREFIX + "You left your team.");
			teams.leaveTeam(player, true);
			
			teams.sendMessage(team, Main.PREFIX + "§a" + player.getName() + "§7 left your team.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("info")) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can create and manage teams.");
			}
			
			Player player = (Player) sender;
			
			Team team = teams.getTeam(player);
			
			if (team == null || SpecManager.getInstance().isSpectating(player)) {
				throw new CommandException("You are not on a team.");
			}
			
			if (!teams.getSavedTeams().containsKey(team.getName())) {
				Set<String> players = new HashSet<String>(team.getEntries());
				teams.getSavedTeams().put(team.getName(), players);
			}
			
			StringBuilder list = new StringBuilder("");
			int i = 1;
			
			Set<String> savedTeam = teams.getSavedTeams().get(team.getName());

			int teamkills = 0;
			
			for (String entry : savedTeam) {
				if (list.length() > 0) {
					if (i == savedTeam.size()) {
						list.append(" §7and §f");
					} else {
						list.append("§7, §f");
					}
				}
				
				teamkills += board.getActualScore(entry);
				
				OfflinePlayer teammates = PlayerUtils.getOfflinePlayer(entry);
				
				if (teammates.isOnline()) {
					list.append(ChatColor.GREEN + teammates.getName());
				} else {
					list.append(ChatColor.RED + teammates.getName());
				}
				i++;
			}

			sender.sendMessage(Main.PREFIX + "Your team info:");
			sender.sendMessage("§8» §7Team: §c" + (team == null ? "None" : team.getPrefix() + team.getName()));

			sender.sendMessage("§8» §7Kills: §a" + board.getActualScore(player.getName()));
			sender.sendMessage("§8» §7Team Kills: §a" + teamkills);
			
			sender.sendMessage("§8» ---------------------------");
			sender.sendMessage("§8» §7Teammates: §o(Names in red means they are offline)");
			sender.sendMessage("§8» §f" + list.toString().trim());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("clear")) {
			if (!sender.hasPermission("uhc.team.admin")) {
				return helpMenu(sender);
			}
			
			for (Team team : board.getBoard().getTeams()) {
				for (OfflinePlayer player : teams.getPlayers(team)) {
					teams.leaveTeam(player, true);
				}
			}
			
			teams.getSavedTeams().clear();
			PlayerUtils.broadcast(Main.PREFIX + "All teams has been cleared.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("color")) {
			if (!sender.hasPermission("uhc.team.admin")) {
				return helpMenu(sender);
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All teams has been re-colored.");
			// it doesn't do anything other than change the color if the team exist.
			teams.setup();
			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			if (teams.getTeamsWithPlayers().size() == 0) {
				sender.sendMessage(Main.PREFIX + "There are no teams with players in it.");
				return true;
			}
			
			sender.sendMessage(Main.PREFIX + "List of all teams:");
			
			for (Team team : teams.getTeamsWithPlayers()) {
				StringBuilder list = new StringBuilder("");
				int i = 1;
				
				Set<String> savedTeam = teams.getSavedTeams().get(team.getName());
				
				if (savedTeam == null) {
					continue;
				}
				
				for (String entry : savedTeam) {
					if (list.length() > 0) {
						if (i == savedTeam.size()) {
							list.append(" and ");
						} else {
							list.append(", ");
						}
					}
					
					list.append(entry);
					i++;
				}
				
				sender.sendMessage(team.getPrefix() + team.getName() + ": §f" + list.toString().trim() + ".");
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("enable")) {
			if (!sender.hasPermission("uhc.team.admin") || args.length == 1) {
				return helpMenu(sender);
			}
			
			if (game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is already enabled.");
				return true;
			}
			
			teamsize = parseInt(args[1], "teamsize");
			
			PlayerUtils.broadcast(Main.PREFIX + "You can now create your own teams! §8(§7Max team size: §6" + teamsize + "§8)");
			PlayerUtils.broadcast(Main.PREFIX + "Use §a/team create §7and §a/team invite§7 to create the team.");

			if (game.pregameBoard()) {
				board.setScore("§e ", 14);
				board.setScore("§8» §cTeam:", 13);
				board.setScore("§8» §7/team", 12);
			}
			
			game.setTeamManagement(true);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("disable")) {
			if (!sender.hasPermission("uhc.team.admin")) {
				return helpMenu(sender);
			}
			
			if (!game.teamManagement()) {
				sender.sendMessage(Main.PREFIX + "Team management is not enabled.");
				return true;
			}

			if (game.pregameBoard()) {
				board.resetScore("§e ");
				board.resetScore("§8» §cTeam:");
				board.resetScore("§8» §7/team");
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "You can no longer create your own teams.");
			game.setTeamManagement(false);
			return true;
		}

		return helpMenu(sender);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			toReturn.add("info");
			toReturn.add("list");
			
			if (Game.getInstance().teamManagement()) {
				toReturn.add("create");
				toReturn.add("leave");
				toReturn.add("invite");
				toReturn.add("kick");
				toReturn.add("accept");
				toReturn.add("deny");
			}
			
			if (sender.hasPermission("uhc.team.admin")) {
				toReturn.add("enable");
				toReturn.add("disable");
				toReturn.add("add");
				toReturn.add("remove");
				toReturn.add("delete");
				toReturn.add("friendlyfire");
				toReturn.add("clear");
			}
		}
		
		if (args.length == 2) {
			switch (args[0].toLowerCase()) {
			case "invite":
			case "kick":
			case "remove":
			case "accept":
			case "deny":
			case "info":
				return null;
			case "add":
			case "delete":
				for (Team team : TeamManager.getInstance().getTeams()) {
					toReturn.add(team.getName());
				}
				break;
			case "friendlyfire":
				toReturn.add("true");
				toReturn.add("false");
				break;
			}
		}
		
		if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
			return null;
		}
		
		return toReturn;
	}
	
	/**
	 * Sends the help list to a player.
	 * 
	 * @param sender the player.
	 */
	public boolean helpMenu(CommandSender sender) {
		sender.sendMessage(Main.PREFIX + "Team help:");
		sender.sendMessage("§8» §f/pm <message> §7- §f§oTalk in team chat.");
		sender.sendMessage("§8» §f/tl §7- §f§oTell your coords to your teammates.");
		sender.sendMessage("§8» §f/team info §7- §f§oDisplay your team info.");
		sender.sendMessage("§8» §f/team list §7- §f§oList all teams.");
		
		if (game.teamManagement()) {
			sender.sendMessage("§8» §f/team create §7- §f§oCreate a team.");
			sender.sendMessage("§8» §f/team leave §7- §f§oLeave your team.");
			sender.sendMessage("§8» §f/team invite <player> §7- §f§oInvite a player to your team.");
			sender.sendMessage("§8» §f/team kick <player> §7- §f§oKick a player to your team.");
			sender.sendMessage("§8» §f/team accept <player> §7- §f§oAccept the players request.");
			sender.sendMessage("§8» §f/team deny <player> §7- §f§oDeny the players request.");
		}
		
		if (sender.hasPermission("uhc.team.admin")) {
			sender.sendMessage(Main.PREFIX + "Team admin help:");
			sender.sendMessage("§8» §f/team info <player> §7- §f§oDisplay the targets team info.");
			sender.sendMessage("§8» §f/team enable <teamsize> §7- §f§oEnable team management.");
			sender.sendMessage("§8» §f/team disable §7- §f§oDisable team management.");
			sender.sendMessage("§8» §f/team add <team> <player> §7- §f§oAdd a player to a team.");
			sender.sendMessage("§8» §f/team remove <player> §7- §f§oRemove a player from his team.");
			sender.sendMessage("§8» §f/team delete <team> §7- §f§oEmpty a specific team.");
			sender.sendMessage("§8» §f/team friendlyfire <true|false> §7- §f§oToggle FriendlyFire.");
			sender.sendMessage("§8» §f/team clear §7- §f§oClear all teams.");
		}
		return true;
	}
}