package com.leontg77.ultrahardcore.listeners;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Parkour;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

/**
 * PushToSpawn listener class.
 * 
 * @author D4mnX
 */
public class PushToSpawnListener implements Listener {
    public static final double SPAWN_RADIUS = 55.0d;

    private final Parkour parkour;
    private final Main plugin;

    /**
     * PushToSpawn listener class constructor.
     * 
     * @param plugin The main class.
     * @param parkour The parkour class.
     */
    public PushToSpawnListener(Main plugin, Parkour parkour) {
        this.parkour = parkour;
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        Location to = event.getTo();
        World world = to.getWorld();
        
        Vector velocity = player.getVelocity();
        double velocityY = velocity.getY();

        if (!world.getName().equals("lobby")) {
            return;
        }

        if (to.getY() > 20) {
            return;
        }

        if (parkour.isParkouring(player)) {
            return;
        }

        if (velocityY > 3) {
            return;
        }

        // No matter what, increase velocity by 3
        velocity.setY(velocityY + 3);

        Location spawn = plugin.getSpawn();
        spawn.setY(to.getY());
        
        double distanceToCenter = to.distance(spawn);
        double horizontalPushingSpeed = Math.max(0, distanceToCenter / SPAWN_RADIUS - 1d);
        
        // Push in the opposite direction (If they're at X, they need to be pushed to -X)
        double inverseX = -to.getX();
        double inverseZ = -to.getZ();
        
        Vector horizontalPushingVector = new Vector(inverseX, 0, inverseZ).normalize().multiply(horizontalPushingSpeed);
        velocity.add(horizontalPushingVector);

        player.setVelocity(velocity);
    }
}
