/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api.util;

import java.util.Iterator;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author BergStudio
 */
public class Numeric {
    
    public static boolean insideArea(Location target, Location min, Location max){
        return target.getBlockX() >= min.getBlockX() && target.getBlockX() <= max.getBlockX() && 
                target.getBlockY() >= min.getBlockY() && target.getBlockY() <= max.getBlockY() && 
                target.getBlockZ() >= min.getBlockZ() && target.getBlockZ() <= max.getBlockZ();
    }
    
    public static Entity getCursorTarget(Player p, double range) {
        Block block;
        Entity target;
        Iterator<Entity> entities;
        Location loc = p.getEyeLocation();
        Vector vec = loc.getDirection().multiply(0.15);
        while ((range -= 0.1) > 0 && ((block = loc.getWorld().getBlockAt(loc)).isLiquid() || block.isEmpty())) {
            entities = loc.getWorld().getNearbyEntities(loc.add(vec), 0.001, 0.001, 0.001).iterator();
            while (entities.hasNext()) {
                if ((target = entities.next()) != p) {
                    return target;
                }
            }
        }
        return null;
    }
    
    public static ConfigurationSection locationToSection(Location location){
        ConfigurationSection section = new YamlConfiguration();
        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
        return section;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    
    public static int toChunkLoc(int value){
        return value%16==0 || value<=16 || value>=-16 ? (value/16)+1 : (value/16);
    }
    
    public static boolean compareLocation(Location loc0, Location loc1){
        return loc0.getBlockX() == loc1.getBlockX() && loc0.getBlockY() == loc1.getBlockY() && loc0.getBlockZ() == loc1.getBlockZ();
    }
}
