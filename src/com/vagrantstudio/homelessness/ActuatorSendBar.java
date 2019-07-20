/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Actuator;
import com.vagrantstudio.homelessness.api.Mission;
import java.util.Objects;

/**
 *
 * @author BergStudio
 */
public class ActuatorSendBar extends Actuator{
    
    private String message;
    private Mission mission;
    
    public ActuatorSendBar(String[] array, Mission mission){
        message = array[1].replace("&", "§");
        this.mission = mission;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorSendBar && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.message);
        hash = 97 * hash + Objects.hashCode(this.mission);
        return hash;
    }

    @Override
    public void execute() {
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            Homelessness.core.sendBar(paramPlayer, message);
        });
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
