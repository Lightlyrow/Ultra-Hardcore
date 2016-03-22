package com.leontg77.ultrahardcore.commands.banning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Ban command class
 * 
 * @author LeonTG77
 */
public class BanCommand extends UHCCommand {	
	private final BoardManager board;

	public BanCommand(BoardManager board) {
		super("ban", "<player> <reason>");
		
		this.board = board;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

    	BanList banList = Bukkit.getBanList(Type.NAME);
    	Player target = Bukkit.getPlayer(args[0]);

    	if (target == null) {
			PlayerUtils.broadcast(Main.PREFIX + "§6" + args[0] + " §7has been banned for §a" + message + "§7.");
			
    		banList.addBan(args[0], message, null, sender.getName());
			board.resetScore(args[0]);
            return true;
		}
    	
    	if (target.hasPermission("uhc.staff") && !sender.hasPermission("uhc.ban.bypass")) {
	    	throw new CommandException("You can't ban this player.");
    	}

    	PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been banned for §a" + message + "§7.");
    	
    	banList.addBan(target.getName(), message, null, sender.getName());
    	
    	PlayerDeathEvent deathEvent = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
    	Bukkit.getPluginManager().callEvent(deathEvent);
    	
    	Block block = target.getLocation().getBlock();
		
		block.setType(Material.AIR);
		block = block.getRelative(BlockFace.UP);
		block.setType(Material.AIR);
		
    	target.kickPlayer(
    	"§8» §7You have been §4banned §7from §6Arctic UHC §8«" +
    	"\n" + 
    	"\n§cReason §8» §7" + message +
    	"\n§cBanned by §8» §7" + sender.getName() +
    	"\n" +
   		"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
    	);
    	
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				toReturn.add(online.getName());
			}
		}
		
		if (args.length > 1) {
			toReturn.add("Hacked Client");
			toReturn.add("Xray");
		}
		
		return toReturn;
	}
}