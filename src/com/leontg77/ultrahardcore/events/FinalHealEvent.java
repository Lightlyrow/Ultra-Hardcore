package com.leontg77.ultrahardcore.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Final heal event.
 * <p>
 * Called when final heal occurs.
 * 
 * @author LeonTG77
 */
public class FinalHealEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
     
    public HandlerList getHandlers() {
        return HANDLERS;
    }
     
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}