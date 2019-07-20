/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Actuator;
import com.vagrantstudio.homelessness.api.Mission;
import java.util.Objects;
import org.bukkit.Location;

/**
 *
 * @author BergStudio
 */
public class ActuatorSpawnMob extends Actuator{
    
    private String name;
    private Location location;
    private int amount = 1;
    
    public ActuatorSpawnMob(String[] array, Mission mission){
        location = new Location(mission.getWorld(), Double.valueOf(array[1]), Double.valueOf(array[2]), Double.valueOf(array[3]));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorSpawnMob ? obj.hashCode() == hashCode() : false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.location);
        hash = 89 * hash + this.amount;
        return hash;
    }

    @Override
    public void execute() {
        for(int loop = 0; loop < amount; loop++){
            Homelessness.hookMythicMobs.spawnMob(name, location);
        }
    }

    @Override
    public String toString() {
        return "SpawnMob," + location.getX() + "," + location.getY() + "," + location.getZ();
    }
    
}
