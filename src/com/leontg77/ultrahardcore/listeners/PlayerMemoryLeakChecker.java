package com.leontg77.ultrahardcore.listeners;

import java.lang.ref.WeakReference;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Function;

/**
 * A class D4 made to fix my memory leaks...
 * 
 * @author D4mnX
 */
public class PlayerMemoryLeakChecker implements Listener {
    protected final Function<String, Void> leakCallback;
	protected final Plugin plugin;

    /**
     * Create a new leak checker.
     * 
     * @param plugin The owning plugin
     * @param leakCallback The callback if a player leak is detected.
     */
    public PlayerMemoryLeakChecker(Plugin plugin, Function<String, Void> leakCallback) {
        this.leakCallback = leakCallback;
        this.plugin = plugin;
    }

    @EventHandler
    protected void on(PlayerQuitEvent event) {
        Player player = event.getPlayer(); // DO NOT REFERENCE IN RUNNABLE
        
        final String playerName = player.getName();
        final WeakReference<Player> reference = new WeakReference<Player>(player);

        new BukkitRunnable() {
            private int secondsLeftUntilError = 30;

            public void run() {
                if (reference.get() == null) {
                    // Player object was garbage collected
                    this.cancel();
                    return;
                }

                secondsLeftUntilError--;
                
                if (secondsLeftUntilError <= 0) {
                    leakCallback.apply(playerName);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}