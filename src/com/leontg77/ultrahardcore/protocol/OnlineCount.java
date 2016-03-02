package com.leontg77.ultrahardcore.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.leontg77.ultrahardcore.Main;

/**
 * Online count class.
 * <p> 
 * Makes the server list not count spectators.
 *
 * @author LeonTG77
 */
public class OnlineCount extends PacketAdapter {
	private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	private final Main plugin;

	/**
	 * Constructor for Online count.
	 * 
	 * @param plugin The main class of the plugin.
	 */
	public OnlineCount(Main plugin) {
		super(plugin, ListenerPriority.NORMAL, PacketType.Status.Server.OUT_SERVER_INFO);
		
		this.plugin = plugin;
	}

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(PacketType.Status.Server.OUT_SERVER_INFO)) {
        	return;
        }

        WrappedServerPing ping = event.getPacket().getServerPings().read(0);
        ping.setPlayersOnline(plugin.getOnlineCount());
        
        event.getPacket().getServerPings().write(0, ping);
    }
    
    /**
     * Enable the online counter.
     */
    public void enable() {
	    manager.addPacketListener(this);
    }    
}