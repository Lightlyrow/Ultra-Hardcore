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

public class PushUpwardsInSpawnListener implements Listener {
    public static double SPAWN_RADIUS = 70.0d;

    private final Main plugin;
    private final Parkour parkour;

    public PushUpwardsInSpawnListener(Main plugin, Parkour parkour) {
        this.plugin = plugin;
        this.parkour = parkour;
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Location to = event.getTo();
        World world = to.getWorld();
        Player player = event.getPlayer();
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

        if (velocityY > 2) {
            return;
        }

        // No matter what, increase velocity by 2
        velocity.setY(velocityY + 2);

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
