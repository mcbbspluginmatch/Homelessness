/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Actuator;
import com.vagrantstudio.homelessness.api.InstanceZone;
import com.vagrantstudio.homelessness.api.Mission;
import java.util.Objects;

/**
 *
 * @author BergStudio
 */
public class ActuatorStartZone extends Actuator{
    
    private Mission mission;
    private String zoneName;
    
    public ActuatorStartZone(String array[], Mission mission){
        this.mission = mission;
        zoneName = array[1];
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorStartZone && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.mission);
        hash = 37 * hash + Objects.hashCode(this.zoneName);
        return hash;
    }

    @Override
    public void execute() {
        InstanceZone zone = PixelInstanceZone.startNewZone(zoneName);
        if(zone == null){
            return;
        }
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            zone.join(paramPlayer);
        });
        zone.start();
    }

    @Override
    public String toString() {
        return "StartZone," + zoneName;
    }
    
}
