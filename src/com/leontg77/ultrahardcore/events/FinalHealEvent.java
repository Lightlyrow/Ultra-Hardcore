package com.leontg77.ultrahardcore.events;

import org.bukkit.event.HandlerList;

/**
 * Final heal event.
 * <p>
 * Called when final heal occurs.
 * 
 * @author LeonTG77
 */
public class FinalHealEvent extends UHCEvent {
   
    public FinalHealEvent() {}
   
    private static final HandlerList handlers = new HandlerList();
     
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}