package com.leontg77.uhc.ubl;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Login listener class.
 * <p> 
 * Contains all eventhandlers for login releated events.
 * 
 * @author LeonTG77
 */
public class UBLListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		UBL ubl = UBL.getInstance();
		
		if (ubl.isBanned(event.getUniqueId())) {
            UBL.BanEntry banEntry = ubl.banlistByUUID.get(event.getUniqueId());
        	PlayerUtils.broadcast(Main.PREFIX + "§c" + event.getName() + " §7tried to join while being UBL'ed for:§c " + banEntry.getData("Reason"), "uhc.staff");
        	
        	UUID uuid = event.getUniqueId();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ubl.getBanMessage(uuid));
            return;
        }
    }
}