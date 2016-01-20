package com.leontg77.ultrahardcore.protocol;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.leontg77.ultrahardcore.Main;

/**
 * Old Enchants class.
 * <p> 
 * This disables the enchantment preview.
 *
 * @author LeonTG77
 */
public class OldEnchants extends PacketAdapter {
	private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();
	private static final OldEnchants INSTANCE = new OldEnchants(Main.plugin);

	/**
	 * Constructor for OldEnchants.
	 * 
	 * @param plugin The main class of the plugin.
	 */
	public OldEnchants(Plugin plugin) {
		super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CRAFT_PROGRESS_BAR);
	}

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(Play.Server.CRAFT_PROGRESS_BAR)) {
        	return;
        }
        
        StructureModifier<Integer> integers = event.getPacket().getIntegers();
        int property = integers.read(1);
        
        if (property >= 4) {
        	integers.write(2, -1);
        }
    }
    
    /**
     * Enable the old enchants.
     */
    public static void enable() {
	    PROTOCOL_MANAGER.addPacketListener(INSTANCE);
    }    
    
    /**
     * Disable the old enchants.
     */
    public static void disable() {
	    PROTOCOL_MANAGER.removePacketListener(INSTANCE);
    }
}