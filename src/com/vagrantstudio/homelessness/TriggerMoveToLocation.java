/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import com.vagrantstudio.homelessness.api.util.Numeric;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author BergStudio
 */
public class TriggerMoveToLocation extends Trigger{
    
    private Mission mission;
    private Map<Player, Location> compass = new HashMap();
    private Location location;
    private int taskId = -1;
    
    public TriggerMoveToLocation(String[] array, Mission mission){
        this.mission = mission;
        location = new Location(Bukkit.getWorld(array[1]), Double.valueOf(array[2]), Double.valueOf(array[3]), Double.valueOf(array[4]));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerMoveToLocation ? obj.hashCode() == hashCode() : false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.mission);
        hash = 43 * hash + Objects.hashCode(this.location);
        return hash;
    }

    @Override
    public void call(Event event) {
        PlayerMoveEvent e = (PlayerMoveEvent)event;
        if(Numeric.compareLocation(location, e.getTo())){
            mission.trigger(this);
            Bukkit.getScheduler().cancelTask(taskId);
            unregister();
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(PlayerMoveEvent.class, this);
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger(this);
    }

    @Override
    public String toString() {
        return "MoveToLocation," + location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    @Override
    public void tips() {
        Location tip = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            compass.put(paramPlayer, paramPlayer.getCompassTarget());
            paramPlayer.setCompassTarget(tip);
        });
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Homelessness.core, () -> {
            Homelessness.core.spawnParticle(tip, 10, 0.5, 0.5, 0.5, Particle.VILLAGER_HAPPY);
        }, 0, 20);
        taskId = task.getTaskId();
    }
    
}
