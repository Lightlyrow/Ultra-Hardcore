package com.leontg77.ultrahardcore.protocol;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Fake Health protocol class.
 * 
 * @author D4mnX
 */
public class FakeHealth extends PacketAdapter {
	private final ProtocolManager manager;

	/**
	 * Fake health class constructor.
	 * 
	 * @param plugin The main class.
	 */
    public FakeHealth(Plugin plugin) {
        super(plugin, PacketType.Play.Server.UPDATE_HEALTH);
		
		manager = ProtocolLibrary.getProtocolManager();
    }
    
    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket().deepClone();
        packet.getFloat().write(0, 20f);
        event.setPacket(packet);
    }
    
    /**
     * Enable the fake hearts.
     */
    public void enable() {
	    manager.addPacketListener(this);
    }    
    
    /**
     * Disable the fake hearts.
     */
    public void disable() {
	    manager.removePacketListener(this);
    }
}