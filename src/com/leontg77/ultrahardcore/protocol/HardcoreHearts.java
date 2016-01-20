package com.leontg77.ultrahardcore.protocol;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.leontg77.ultrahardcore.Main;

/**
 * Hardcore hearts class.
 * <p> 
 * This class manages the hardcore hearts feature.
 *
 * @author ghowden
 */
public class HardcoreHearts extends PacketAdapter {
	private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();
	private static final HardcoreHearts INSTANCE = new HardcoreHearts(Main.plugin);

	/**
	 * Constructor for HardcoreHearts.
	 * 
	 * @param plugin The main class of the plugin.
	 */
	public HardcoreHearts(Plugin plugin) {
		super(plugin, ListenerPriority.NORMAL, Play.Server.LOGIN);
	}

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(Play.Server.LOGIN)) {
        	return;
        }
        
        event.getPacket().getBooleans().write(0, true);
    }
    
    /**
     * Enable the hardcore hearts.
     */
    public static void enable() {
	    PROTOCOL_MANAGER.addPacketListener(INSTANCE);
    }    
    
    /**
     * Disable the hardcore hearts.
     */
    public static void disable() {
	    PROTOCOL_MANAGER.removePacketListener(INSTANCE);
    }
}	
