package com.leontg77.ultrahardcore.events;

import org.bukkit.event.HandlerList;

/**
 * Game start event.
 * <p>
 * Called when the game starts.
 * 
 * @author LeonTG77
 */
public class GameStartEvent extends UHCEvent {
   
    public GameStartEvent() {}
   
    private static final HandlerList handlers = new HandlerList();
     
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}