/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public interface ChatChannel extends Visual, Unique, View.Viewable{
    
    public abstract List<Player> listAll();
    
    public abstract String getName();
    
    public abstract void setName(String paramString);
    
    public abstract void add(Player paramPlayer);
    
    public abstract void remove(Player paramPlayer);
    
    public abstract boolean contains(Player paramPlayer);
    
    public abstract void check();
    
    public abstract void clearMessageBar();
    
    public abstract void chat(Player paramPlayer, String paramString);
    
    public abstract void setPassword(String paramString);
    
    public abstract String getPassword();
    
    public abstract boolean usePassword();
    
    public abstract void setAccessLevel(Access paramAccessLevel);
    
    public abstract Access getAccessLevel();
    
    public abstract void setOwner(Player paramPlayer);
    
    public abstract Player getOwner();
    
    public abstract void addAll(List<Player> paramPlayerList);
    
    public abstract void removeAll(List<Player> paramPlayerList);
}
