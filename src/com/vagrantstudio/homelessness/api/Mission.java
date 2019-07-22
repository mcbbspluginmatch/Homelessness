/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public interface Mission {
    
    public List<Entry<Trigger, Set<Actuator>>> getTask();
    
    public ConfigurationSection getTaskSection();
    
    public void trigger(Trigger trigger);
    
    public Set<Player> getPlayers();
    
    public World getWorld();
    
}
