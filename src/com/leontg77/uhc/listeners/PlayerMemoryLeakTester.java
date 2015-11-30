package com.leontg77.uhc.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.leontg77.uhc.Main;

/**
 * A class D4 made to fix my memory leaks...
 * 
 * @author D4mnX
 */
public class PlayerMemoryLeakTester implements Listener {
	public static final String METADATA_KEY = "MemoryLeakTester";
   
    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        final String name = player.getName();
        
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(Main.plugin, new Object(){
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                Main.plugin.getLogger().info("Player object for " + name + " has been garbage collected!");
            }
        }));
    }
}