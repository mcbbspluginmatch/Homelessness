/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

/**
 *
 * @author BergStudio
 */
public class BridgeMythicMobs implements Listener{
    private BukkitAPIHelper api = ((MythicMobs)Bukkit.getPluginManager().getPlugin("MythicMobs")).getAPIHelper();
    
    public MythicMob getMob(String name){
        return api.getMythicMob(name);
    }
    
    public Entity spawnMob(MythicMob mob, Location location){
        try {
            return api.spawnMythicMob(mob, location, 1);
        } catch (InvalidMobTypeException ex) {
            Logger.getLogger(BridgeMythicMobs.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Entity spawnMob(String name, Location location){
        try {
            return api.spawnMythicMob(name, location);
        } catch (InvalidMobTypeException ex) {
            Logger.getLogger(BridgeMythicMobs.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
