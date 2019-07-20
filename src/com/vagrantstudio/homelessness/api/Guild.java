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
import org.bukkit.inventory.Inventory;

/**
 *
 * @author BergStudio
 */
public abstract interface Guild extends Nameable, Visual, View.Viewable, Unique, Storable{
    
    public abstract void add(Player paramPlayer);
    
    public abstract void remove(Player paramPlayer);
    
    public abstract boolean contains(OfflinePlayer paramOfflinePlayer);
    
    public abstract void refresh();
    
    public abstract ChatChannel getChatChannel();
    
    public abstract void reloadChatChannel();
    
    public abstract void setOwner(OfflinePlayer paramOfflinePlayer);
    
    public abstract OfflinePlayer getOwner();
    
    public abstract Bank bank();
    
    public abstract Map<UUID, String> getPlayers();
    
    public abstract Map<String, List<String>> getLevels();
    
    public abstract boolean hasPermission(Player paramPlayer, String paramString);
    
    public abstract void broadcast(String paramString);
    
    public abstract Inventory getOptionInterface();
    
    public abstract void addManifesto(String paramString);
    
    public abstract void setManifesto(List<String> paramStringList);
    
    public abstract void setManifesto(int paramInteger, String paramString);
    
    public abstract void clearManifesto();
    
    public WareCollection ware();
    
    public Set<UUID> getOwnedArea();
    
}
