/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class TriggerBreakItem extends Trigger{
    
    private Mission mission;
    private Material material;
    private byte data;
    private String name;
    
    public TriggerBreakItem(String[] array, Mission mission){
        this.mission = mission;
        material = Material.valueOf(array[1]);
        data = Byte.valueOf(array[2]);
        name = "_ignore".equals(array[3]) ? null : array[3];
   }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerBreakItem && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.mission);
        hash = 37 * hash + Objects.hashCode(this.material);
        hash = 37 * hash + this.data;
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public void call(Event event) {
        PlayerItemBreakEvent e = (PlayerItemBreakEvent) event;
        ItemStack brokenItem = e.getBrokenItem();
        if(mission.getPlayers().contains(e.getPlayer()) && brokenItem.getType() == material && brokenItem.getData().getData() == data 
                && (name == null || (brokenItem.hasItemMeta() && name.equals(brokenItem.getItemMeta().getDisplayName())))){
            mission.trigger(this);
            unregister();
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(PlayerItemBreakEvent.class, this);
        mission.updateTip(name == null ? "§a用坏一个 §7" + material.toString().toLowerCase() + " §a物品" : "§a用坏一个名为 " + name + " §a的 " + material.toString().toLowerCase());
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger( this);
    }

    @Override
    public void tips() {
        String tip = name == null ? "§a用坏一个 §7" + material.toString().toLowerCase() + " §a物品" : "§a用坏一个名为 " + name + " §a的 " + material.toString().toLowerCase();
        mission.getPlayers().stream().forEach((paramPlayer) -> {
            Homelessness.core.sendTitle(paramPlayer, "§a新的目标已更新", tip);
        });
    }

    @Override
    public String toString() {
        return "BreakItem," + material.toString() + "," + data + "," + (name == null ? "_ignore" : name);
    }
    
}
