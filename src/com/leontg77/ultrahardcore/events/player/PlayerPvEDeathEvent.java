package com.leontg77.ultrahardcore.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerPvEDeathEvent extends PlayerEvent {
   
    public PlayerPvEDeathEvent(Player player) {
		super(player);
	}

	private static final HandlerList handlers = new HandlerList();
     
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
