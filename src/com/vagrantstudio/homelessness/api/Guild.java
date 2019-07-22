/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public abstract interface Guild extends Nameable, Visual, View.Viewable, Unique, Storable{
    
    public abstract void apply(Player paramPlayer);
    
    public abstract void accpet(UUID paramUniqueId);
    
    public abstract void deny(UUID paramUniqueId);
    
    public abstract void add(Player paramPlayer);
    
    public abstract void remove(Player paramPlayer);
    
    public abstract void kick(UUID paramUniqueId);
    
    public abstract boolean contains(OfflinePlayer paramOfflinePlayer);
    
    public abstract ChatChannel getChatChannel();
    
    public abstract void setOwner(OfflinePlayer paramOfflinePlayer);
    
    public abstract OfflinePlayer getOwner();
    
    public abstract Bank bank();
    
    public abstract Map<UUID, Grade> getPlayers();
    
    public abstract Grade getGrade(OfflinePlayer paramOfflinePlayer);
    
    public abstract void setGrade(OfflinePlayer paramOfflinePlayer, Grade paramGrade);
    
    public abstract void broadcast(String paramString);
    
    public abstract List<UUID> getApplications();
    
    public abstract View getOptionInterface();
    
    public abstract List<String> getManifesto();
    
    public abstract boolean isFull();
    
    public abstract boolean upgrade();
    
    public WareCollection ware();
    
    public Set<UUID> getOwnedArea();
    
    public static enum Grade{
        CAPTAIN,
        VICE_CAPTAIN,
        MEMBER;
    }
    
}
