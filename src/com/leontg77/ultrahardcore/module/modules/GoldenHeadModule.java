package com.leontg77.ultrahardcore.module.modules;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.module.Module;
import com.leontg77.ultrahardcore.utils.BlockUtils;

public class GoldenHeadModule extends Module implements Listener {

	@EventHandler
	public void on(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		
		// incase an explotion, wait a tick.
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				Block block = player.getLocation().getBlock();
				
				block.setType(Material.NETHER_FENCE);
				block = block.getRelative(BlockFace.UP);
				block.setType(Material.SKULL);
			    
				Skull skull;
				
				try {
			        skull = (Skull) block.getState();
					
				    skull.setSkullType(SkullType.PLAYER);
				    skull.setRotation(BlockUtils.getBlockDirection(player.getLocation()));
				    skull.setOwner(player.getName());
				    skull.update();
				    
				    block.setData((byte) 0x1, true);
				} catch (Exception e) {
					// the skull wasn't placed (outside of the world probs), tell the console so.
					Bukkit.getLogger().warning("Could not place player skull.");
				}
			}
		}.runTaskLater(Main.plugin, 1);
	}
}