/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public abstract interface Friends extends Visual{
    
    public abstract void add(OfflinePlayer paramOfflinePlayer);
    
    public abstract void add(UUID paramUniqueId);
    
    public abstract void apply(OfflinePlayer paramOfflinePlayer);
    
    public abstract void accept(OfflinePlayer paramOfflinePlayer);
    
    public abstract void deny(OfflinePlayer paramOfflinePlayer);
    
    public abstract List<UUID> all();
    
    public abstract void clear();
    
    public abstract void remove(OfflinePlayer paramOfflinePlayer);
    
    public abstract void remove(UUID paramUniqueId);
    
    public abstract View page(int paramInteger);
    
    public abstract List<String> toList();
    
    public abstract Set<UUID> applications();
    
    public abstract List<UUID> getOnlines();
}
