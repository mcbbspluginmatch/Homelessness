/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 *
 * @author BergStudio
 */
public class TriggerTouchEntity extends Trigger{
    
    private Mission mission;
    private EntityType type;
    private String name;
    
    public TriggerTouchEntity(String[] array, Mission mission){
        this.mission = mission;
        this.type = EntityType.valueOf(array[1].toUpperCase());
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void call(Event event) {
        PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
        
    }

    @Override
    public void register() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unregister() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tips() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
