package com.leontg77.ultrahardcore.ubl;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

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
    	UUID uuid = event.getUniqueId();
		UBL ubl = UBL.getInstance();
		
		if (!ubl.isBanned(uuid)) {
            return;
        }
		
        BanEntry banEntry = ubl.banlistByUUID.get(uuid);
    	
    	PlayerUtils.broadcast(Main.PREFIX + "§c" + event.getName() + " §7tried to join while being UBL'ed for: §c" + banEntry.getData("Reason"), "uhc.staff");
        event.disallow(Result.KICK_BANNED, ubl.getBanMessage(uuid));
    }
}