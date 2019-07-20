/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author BergStudio
 */
public class TriggerTouchBlock extends Trigger {
    
    private Mission mission;
    private Location location;
    private Material material;
    private byte data = 0;
    private Map<Player, Location> compass = new HashMap();
    
    public TriggerTouchBlock(String[] array, Mission mission){
        location = new Location(mission.getWorld(), Integer.valueOf(array[1]), Integer.valueOf(array[2]), Integer.valueOf(array[3]));
        material = Material.valueOf(array[5]);
        data = Byte.valueOf(array[6]);
    }

    public TriggerTouchBlock(Location location, Material mtrl, Mission mission) {
        this.location = location;
        this.material = mtrl;
    }
    
    public TriggerTouchBlock(Location location, Material mtrl, byte data, Mission mission) {
        this(location, mtrl, mission);
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerTouchBlock ? obj.hashCode() == hashCode() : false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.mission);
        hash = 67 * hash + Objects.hashCode(this.location);
        hash = 67 * hash + Objects.hashCode(this.material);
        hash = 67 * hash + this.data;
        return hash;
    }

    @Override
    public void call(Event e) {
        PlayerInteractEvent event = (PlayerInteractEvent) e;
        if(!mission.getPlayers().contains(event.getPlayer())){
            return;
        }
        if(!event.hasBlock()){
            return;
        }
        Block block = event.getClickedBlock();
        if(block.getType() == material && block.getData() == data){
            mission.trigger(this);
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(EntityDamageByEntityEvent.class, this);
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger(this);
    }

    @Override
    public String toString() {
        return "TouchBlock," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getWorld().getUID().toString() + "," + material.toString() + "," + data;
    }

    @Override
    public void tips() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
