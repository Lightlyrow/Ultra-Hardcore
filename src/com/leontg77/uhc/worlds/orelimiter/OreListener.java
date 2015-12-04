package com.leontg77.uhc.worlds.orelimiter;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class OreListener implements Listener {
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldInit(final WorldInitEvent event) {
        Bukkit.getLogger().log(Level.INFO, "World init detected! OreLimiter");
        event.getWorld().getPopulators().add(new OreLimiterPopulator());
    }
}