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
public class ActuatorSendTitle extends Actuator{
    
    private Mission mission;
    private String title = "";
    private String subTitle = "";
    private int fadeIn = 5;
    private int stay = 20;
    private int fadeOut = 5;
    
    public ActuatorSendTitle(String[] array, Mission mission){
        this.title = array[1];
        this.subTitle = array[2];
        this.fadeIn = Integer.valueOf(array[3]);
        this.stay = Integer.valueOf(array[4]);
        this.fadeOut = Integer.valueOf(array[5]);
        this.mission = mission;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorSendTitle ? obj.hashCode() == hashCode() : false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.title);
        hash = 11 * hash + Objects.hashCode(this.subTitle);
        hash = 11 * hash + this.fadeIn;
        hash = 11 * hash + this.stay;
        hash = 11 * hash + this.fadeOut;
        return hash;
    }

    @Override
    public void execute() {
        mission.getPlayers().stream().forEach((paramPlayer) -> { Homelessness.core.sendTitle(paramPlayer, title, subTitle, fadeIn, stay, fadeOut); });
    }

    @Override
    public String toString() {
        return "SendTitle," + title + "," + subTitle + "," + fadeIn + "," + stay + "," + fadeOut;
    }
    
}
