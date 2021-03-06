package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Agar.io scenario class.
 * 
 * Scenario by: D4mnX
 * 
 * @author D4mnX
 */
public class AgarIO extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    private BukkitRunnable task;

    /**
     * Agar.io class constructor.
     * 
     * @param plugin The main class.
     * @param game The game class.
     */
    public AgarIO(Main plugin, Game game) {
        super("Agar.io", "The lower you are on health, the faster you run.");
        
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void onDisable() {
    	if (task != null) {
    		task.cancel();
    	}
    	
        updateSpeed(game.getPlayers());
        task = null;
    }

    @Override
    public void onEnable() {
    	if (!State.isState(State.INGAME)) {
    		return;
    	}
    
    	on(new GameStartEvent());
    }
    
    @EventHandler
    public void on(GameStartEvent event) {
    	if (task != null) {
    		return;
    	}
    	
    	task = new BukkitRunnable() {
            public void run() {
                updateSpeed(game.getPlayers());
            }
        };
        
        task.runTaskTimer(plugin, 1, 1);
    }

    @EventHandler
    public void onEric(PlayerDeathEvent event) {
    	if (!State.isState(State.INGAME)) {
    		return;
    	}
    	
    	Player player = event.getEntity();

    	player.setWalkSpeed(0.1f);
    	player.setFlySpeed(0.2f);
    }
    
    /**
     * Update the speed for the given player.
     * 
     * @param player The player to update for.
     */
    public void updateSpeed(Player player) {
        if (!isEnabled()) {
            setSpeedMultiplier(player, 1);
            return;
        }
        
        float healthPercentage = (float) (player.getHealth() / player.getMaxHealth());
        float speedMultiplier = 2 - healthPercentage;
        
        setSpeedMultiplier(player, speedMultiplier);
    }
    
    /**
     * Update the speed for the given list of players.
     * 
     * @param player The players to update for.
     */
    public void updateSpeed(List<Player> players) {
        for (Player player : players) {
            updateSpeed(player);
        }
    }

    /**
     * Set the speed for the given player.
     * 
     * @param player The player to set it for.
     * @param speedMultiplier The speed multiplier to set.
     */
    private void setSpeedMultiplier(Player player, float speedMultiplier) {
        float defaultSpeed = player.isFlying() ? 0.1f : 0.2f;
        
        if (player.isFlying()) {
            player.setFlySpeed(defaultSpeed * speedMultiplier);
        } else {
            player.setWalkSpeed(defaultSpeed * speedMultiplier);
        }
    }
}