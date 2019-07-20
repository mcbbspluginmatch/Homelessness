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
public class ActuatorEndZone extends Actuator{
    private Mission mission;
    
    public ActuatorEndZone(String[] array, Mission mission){
        this.mission = mission;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorEndZone && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.mission);
        return hash;
    }

    @Override
    public void execute() {
        if(mission instanceof InstanceZone) ((InstanceZone)mission).end();
    }

    @Override
    public String toString() {
        return "EndZone";
    }
    
}
