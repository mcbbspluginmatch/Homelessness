/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Trigger;
import java.util.Objects;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class TriggerFish extends Trigger{
    
    private Mission mission;
    private byte fishType;
    private int times = 1, now = 0;
    
    public TriggerFish(String[] array, Mission mission){
        this.mission = mission;
        fishType = Byte.valueOf(array[1]);
        times = Integer.valueOf(array[2]);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerFish && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.mission);
        hash = 31 * hash + this.fishType;
        hash = 31 * hash + this.times;
        return hash;
    }
    
    @Override
    public void call(Event event) {
        PlayerFishEvent e = (PlayerFishEvent) event;
        if(mission.getPlayers().contains(e.getPlayer()) && e.getCaught() instanceof Item){
            ItemStack caught = ((Item)e.getCaught()).getItemStack();
            if(caught.getData().getData() != fishType) return;
            now++;
            if(now == times){
                mission.trigger(this);
                unregister();
            }
        }
    }

    @Override
    public void register() {
        Homelessness.core.getEventScheduler().registerTrigger(PlayerFishEvent.class, this);
        tips();
    }

    @Override
    public void unregister() {
        Homelessness.core.getEventScheduler().unregisterTrigger(this);
    }

    @Override
    public void tips() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "Fish," + fishType + "," + times;
    }
    
}
