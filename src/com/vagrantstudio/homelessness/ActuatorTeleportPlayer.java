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
public class ActuatorTeleportPlayer extends Actuator{
    
    private Location location;
    private Mission mission;
    
    public ActuatorTeleportPlayer(String[] array, Mission mission){
        this.location = new Location(mission.getWorld(), Double.valueOf(array[1]), Double.valueOf(array[2]), Double.valueOf(array[3]));
        this.mission = mission;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorTeleportPlayer ? obj.hashCode() == hashCode() : false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.location);
        hash = 19 * hash + Objects.hashCode(this.mission);
        return hash;
    }

    @Override
    public void execute() {
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            paramPlayer.teleport(location);
        });
    }

    @Override
    public String toString() {
        return "TeleportPlayer," + location.getX() + "," + location.getY() + "," + location.getZ();
    }
    
}
