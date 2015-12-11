package com.leontg77.ultrahardcore.events;

import org.bukkit.event.HandlerList;

/**
 * Game end event.
 * <p>
 * Called when the game ends.
 * 
 * @author LeonTG77
 */
public class GameEndEvent extends UHCEvent {
   
    public GameEndEvent() {}
   
    private static final HandlerList handlers = new HandlerList();
     
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}