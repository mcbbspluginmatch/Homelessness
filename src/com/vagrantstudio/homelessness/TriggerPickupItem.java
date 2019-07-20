/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author BergStudio
 */
public class TriggerPickupItem extends Trigger {

    private Mission mission;
    private Material material;
    private byte data;
    private String name;
    private int taskId;
    private Set<Entity> glowingEntities = new HashSet();

    public TriggerPickupItem(String[] array, Mission mission) throws IOException {
        material = Material.valueOf(array[1]);
        data = Byte.valueOf(array[2]);
        name = array[3].equalsIgnoreCase("_ignore") ? null : array[3];
        this.mission = mission;
    }

    public TriggerPickupItem(Material material, byte data, String name, Mission mission) {
        this.mission = mission;

    }

    public TriggerPickupItem(ItemStack item, Mission mission) {
        this(item.getType(), (byte) item.getDurability(), (item.hasItemMeta() ? item.getItemMeta().getDisplayName() : null), mission);
    }

    @Override
    public void call(Event event) {
        EntityPickupItemEvent e = (EntityPickupItemEvent) event;
        ItemStack item = e.getItem().getItemStack();
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity();
        if (!mission.getPlayers().contains(player)) {
            return;
        }
        if (item.getType() == material && item.getDurability() == data && (name == null || (item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(name)))) {
            mission.trigger(this);
            glowingEntities.stream().forEach((paramEntity) -> {
                paramEntity.setGlowing(false);
            });
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(EntityPickupItemEvent.class, this);
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerPickupItem ? obj.hashCode() == hashCode() : false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.material);
        hash = 83 * hash + this.data;
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        return "PickupItem," + material.toString() + "," + data + "," + name;
    }

    @Override
    public void tips() {
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            Homelessness.core.sendTitle(paramPlayer, "§a新的目标已更新", "§7捡起§f§l被标记§7的物品", 5, 40, 5);
        });
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Homelessness.core, () -> {
            mission.getPlayers().stream().forEach((paramPlayer) -> {
                paramPlayer.getNearbyEntities(15, 15, 15).stream().forEach((paramEntity) -> {
                    if (glowingEntities.contains(paramEntity)) {
                        return;
                    }
                    if (paramEntity instanceof Item) {
                        ItemStack item = ((Item) paramEntity).getItemStack();
                        if (item.getType() == material && item.getDurability() == data && (name == null || (item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(name)))) {
                            paramEntity.setGlowing(true);
                            glowingEntities.add(paramEntity);
                        }
                    }
                });
            });
        }, 0, 20);
        taskId = task.getTaskId();
    }

}
