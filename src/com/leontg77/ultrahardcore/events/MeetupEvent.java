package com.leontg77.ultrahardcore.events;

import org.bukkit.event.HandlerList;

/**
 * Meetup event.
 * <p>
 * Called when meetup occurs.
 * 
 * @author LeonTG77
 */
public class MeetupEvent extends UHCEvent {
   
    public MeetupEvent() {}
   
    private static final HandlerList handlers = new HandlerList();
     
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}