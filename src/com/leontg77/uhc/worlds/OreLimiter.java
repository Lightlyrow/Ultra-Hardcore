package com.leontg77.uhc.worlds;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Maps;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.worlds.orelimiter.OreListener;

public class OreLimiter {

    public static final Material[] SUPPORTED_ORES = {
        Material.GOLD_ORE,
        Material.DIAMOND_ORE,
        Material.IRON_ORE,
        Material.LAPIS_ORE,
        Material.COAL_ORE,
        Material.REDSTONE_ORE,
        Material.QUARTZ_ORE
    };

    private final Map<Material, Material> replacements;
    private final Map<Material, Integer> oreRates;
    
    public OreLimiter() {
        this.oreRates = Maps.newHashMap();
        this.replacements = Maps.newHashMap();
    }

    public void setup() {
        Bukkit.getPluginManager().registerEvents(new OreListener(), Main.plugin);

        oreRates.put(Material.GOLD_ORE, configuration.getInt("goldOre.rate"));
        oreRates.put(Material.DIAMOND_ORE, configuration.getInt("diamondOre.rate"));
        oreRates.put(Material.IRON_ORE, configuration.getInt("ironOre.rate"));
        oreRates.put(Material.LAPIS_ORE, configuration.getInt("lapisOre.rate"));
        oreRates.put(Material.COAL_ORE, configuration.getInt("coalOre.rate"));
        oreRates.put(Material.REDSTONE_ORE, configuration.getInt("redstoneOre.rate"));
        oreRates.put(Material.QUARTZ_ORE, configuration.getInt("quartzOre.rate"));

        for (Material material : oreRates.keySet()) {
            if (oreRates.get(material) < 0) {
                oreRates.put(material, 0);
            } else if (oreRates.get(material) > 100) {
                oreRates.put(material, 100);
            }
        }

        replacements.put(Material.GOLD_ORE, Material.matchMaterial(configuration.getString("quartzOre.replacement")));
        replacements.put(Material.DIAMOND_ORE, Material.matchMaterial(configuration.getString("quartzOre.replacement")));
        replacements.put(Material.IRON_ORE, Material.matchMaterial(configuration.getString("quartzOre.replacement")));
        replacements.put(Material.LAPIS_ORE, Material.matchMaterial(configuration.getString("quartzOre.replacement")));
        replacements.put(Material.COAL_ORE, Material.matchMaterial(configuration.getString("quartzOre.replacement")));
        replacements.put(Material.REDSTONE_ORE, Material.matchMaterial(configuration.getString("quartzOre.replacement")));
        replacements.put(Material.QUARTZ_ORE, Material.matchMaterial(configuration.getString("quartzOre.replacement")));

        for (Material material : replacements.keySet()) {
            if (replacements.get(material) == null) {
                replacements.put(material, Material.STONE);
            }
        }
    }

    public Map<Material, Integer> getOreRates() {
        return oreRates;
    }

    public Map<Material, Material> getReplacements() {
        return replacements;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public void setRate(Material material, Integer rate) {
        oreRates.put(material, rate);
    }
    
    public void setReplacement(Material material, Material replacement) {
        replacements.put(material, replacement);
    }
}