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
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author BergStudio
 */
public class TriggerAttackEntity extends Trigger {

    private Mission mission;
    private Set<Entity> glowingEntities = new HashSet();
    private EntityType type;
    private int times = 1, now = 0, taskId = -1;
    private String name;
    
    public TriggerAttackEntity(String[] array, Mission mission){
        this.mission = mission;
        type = EntityType.valueOf(array[1]);
        times = Integer.valueOf(array[2]);
        name = "_ignore".equals(array[3]) ? null : array[3];
    }

    public TriggerAttackEntity(EntityType type, int amount, Mission mission) {
        this.mission = mission;
        this.type = type;
        this.times = amount;
    }

    public TriggerAttackEntity(EntityType type, int amount, String name, Mission mission) {
        this(type, amount, mission);
        this.name = name;
    }

    @Override
    public void call(Event event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        Entity damager = e.getDamager();
        Entity victim = e.getEntity();
        if (!(damager instanceof Player)) {
            return;
        }
        Player player = (Player) damager;
        if (!mission.getPlayers().contains(player)) {
            return;
        }
        if (victim.getType() == type && (name == null ? true : victim.getCustomName().equals(name))) {
            now++;
        }
        if (now == times) {
            mission.trigger(this);
            unregister();
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(EntityDamageByEntityEvent.class, this);
        mission.updateTip(name == null ? "§a攻击 §7" + times + " §a次 " + type.toString().toLowerCase() : "§a攻击名为 §7" + name + " §a的 " + type.toString().toLowerCase() + " " + times + " §a次");
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerAttackEntity ? obj.hashCode() == hashCode() : false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.mission);
        hash = 17 * hash + Objects.hashCode(this.type);
        hash = 17 * hash + this.times;
        hash = 17 * hash + Objects.hashCode(this.name);
        return hash;
    }


    @Override
    public String toString() {
        return "AttackEntity," + type.toString() + "," + times + "," + name;
    }

    @Override
    public void tips() {
        mission.getPlayers().stream().forEach((paramPlayer) -> { Homelessness.core.sendTitle(paramPlayer, "§a新的目标已更新", "§7攻击§f§l被标记§7的目标", 5, 40, 5); });
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Homelessness.core, () -> {
            mission.getPlayers().stream().forEach((paramPlayer) -> {
                paramPlayer.getNearbyEntities(15, 15, 15).stream().forEach((paramEntity) -> {
                    if(paramEntity.getType() == type && (name != null && name.equals(paramEntity.getCustomName())) && !glowingEntities.contains(paramEntity)) {
                        glowingEntities.add(paramEntity);
                        paramEntity.setGlowing(true);
                    }
                });
            });
        }, 0, 40);
        taskId = task.getTaskId();
    }
}
