/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 *
 * @author BergStudio
 */
public class TriggerLeaveVehicle extends Trigger{
    
    private Mission mission;
    private EntityType type;
    private String name;
    
    public TriggerLeaveVehicle(String[] array, Mission mission){
        this.mission = mission;
        type = EntityType.valueOf(array[1]);
        name = "_ignore".equals(array[2]) ? null : array[2];
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerLeaveVehicle && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.mission);
        hash = 37 * hash + Objects.hashCode(this.type);
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public void call(Event event) {
        VehicleExitEvent e = (VehicleExitEvent)event;
        if (e.getExited() instanceof Player && mission.getPlayers().contains((Player) e.getExited()) && e.getVehicle().getType() == type && (name == null || name.equals(e.getVehicle().getCustomName()))) {
            mission.trigger(this);
            unregister();
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(VehicleExitEvent.class, this);
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger(this);
    }

    @Override
    public void tips() {
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            Homelessness.core.sendTitle(paramPlayer, "§a新的目标已更新", "§7离开载具");
        });
    }

    @Override
    public String toString() {
        return "LeaveVehicle," + type.toString() + "," + (name == null ? "_ignore" : name);
    }
    
}
