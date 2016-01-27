package com.leontg77.ultrahardcore.events.uhc;

import org.bukkit.event.HandlerList;

import com.leontg77.ultrahardcore.events.UHCEvent;

/**
 * PvP enable event.
 * <p>
 * Called when pvp enables.
 * 
 * @author LeonTG77
 */
public class PvPEnableEvent extends UHCEvent {
   
    public PvPEnableEvent() {}
   
    private static final HandlerList handlers = new HandlerList();
     
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}