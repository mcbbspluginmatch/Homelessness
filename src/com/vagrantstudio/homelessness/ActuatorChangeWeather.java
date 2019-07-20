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
public class ActuatorChangeWeather extends Actuator{
    
    private Weather weather;
    private Mission mission;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorChangeWeather && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.weather);
        hash = 31 * hash + Objects.hashCode(this.mission);
        return hash;
    }

    @Override
    public void execute() {
        switch(weather.toString()){
            case "NORMAL":
                mission.getWorld().setThundering(false);
                mission.getWorld().setStorm(false);
                break;
            case "RAIN_SNOW":
                mission.getWorld().setStorm(true);
                break;
            case "LIGHTNING":
                mission.getWorld().setThundering(true);
                break;
        }
    }

    @Override
    public String toString() {
        return "ChangeWeather," + weather.toString();
    }
    
    public static enum Weather{
        NORMAL,
        RAIN_SNOW,
        LIGHTNING;
    }
}
