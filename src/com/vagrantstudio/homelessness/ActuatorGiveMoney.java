/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Actuator;
import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.Mission;
import java.util.Objects;

/**
 *
 * @author BergStudio
 */
public class ActuatorGiveMoney extends Actuator{
    
    private int value;
    private Mission mission;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActuatorGiveMoney && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.value;
        hash = 73 * hash + Objects.hashCode(this.mission);
        return hash;
    }

    @Override
    public void execute() {
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            Bank bank = PixelBank.localMap.get(paramPlayer.getUniqueId());
            bank.setBalance(value + bank.getBalance());
        });
    }

    @Override
    public String toString() {
        return "GiveMoney," + value;
    }
    
}
