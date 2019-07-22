/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author BergStudio
 */
public class TriggerEnterVehicle extends Trigger {

    private Mission mission;
    private EntityType type;
    private String name;
    private int taskId;
    private Set<Entity> glowingEntities = new HashSet();

    public TriggerEnterVehicle(String[] array, Mission mission) {
        this.mission = mission;
        type = EntityType.valueOf(array[1]);
        name = "_ignore".equals(array[2]) ? null : array[2];
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerEnterVehicle && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.mission);
        hash = 79 * hash + Objects.hashCode(this.type);
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public void call(Event event) {
        VehicleEnterEvent e = (VehicleEnterEvent) event;
        if (e.getEntered() instanceof Player && mission.getPlayers().contains((Player) e.getEntered()) && e.getVehicle().getType() == type
                && (name == null || name.equals(e.getVehicle().getCustomName()))) {
            mission.trigger(this);
            Bukkit.getScheduler().cancelTask(taskId);
            glowingEntities.stream().forEach((paramEntity) -> {
                paramEntity.setGlowing(false);
            });
            unregister();
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(VehicleEnterEvent.class, this);
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger(this);
    }

    @Override
    public void tips() {
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            Homelessness.core.sendTitle(paramPlayer, "§a新的目标已更新", "§7捡起§f§l被标记§7的物品", 5, 40, 5);
        });
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Homelessness.core, () -> {
            mission.getPlayers().stream().forEach((paramPlayer) -> {
                paramPlayer.getNearbyEntities(15, 15, 15).stream().forEach((paramEntity) -> {
                    if (!glowingEntities.contains(paramEntity)) {
                        if (paramEntity instanceof Vehicle && paramEntity.getType() == type && (name == null || name.equals(paramEntity.getCustomName()))) {
                            paramEntity.setGlowing(true);
                            glowingEntities.add(paramEntity);
                        }
                    }
                });
            });
        }, 0, 20);
        taskId = task.getTaskId();
    }

    @Override
    public String toString() {
        return "EnterVehicle," + type.toString() + "," + (name == null ? "_ignore" : name);
    }

}
