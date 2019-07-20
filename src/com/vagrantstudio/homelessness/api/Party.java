/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public interface Party extends Visual, View.Viewable, Nameable, Unique{
    
    public abstract void join(Player paramPlayer);
    
    public abstract void kick(OfflinePlayer paramOfflinePlayer);
    
    public abstract void invite(Player paramPlayer);
    
    public abstract Map<OfflinePlayer, Grade> getMembers();
    
    public abstract Set<OfflinePlayer> getPlayers();
    
    public abstract void add(OfflinePlayer paramOfflinePlayer);
    
    public abstract void remove(OfflinePlayer paramOfflinePlayer);
    
    public abstract boolean contains(OfflinePlayer paramOfflinePlayer);
    
    public abstract Grade getGrade(OfflinePlayer paramOfflinePlayer);
    
    public abstract void setGrade(OfflinePlayer paramOfflinePlayer, Grade paramGrade);
    
    public abstract void setLeader(OfflinePlayer paramOfflinePlayer);
    
    public abstract OfflinePlayer getLeader();
    
    public abstract List<Player> getOnlines();
    
    public abstract void convene(Player paramSender);
    
    public abstract void convene(Player paramSender, Player paramPlayer);
    
    public abstract void broadcast(String paramString);
    
    public static enum Grade{
        CAPTAIN,
        VICE_CAPTAIN,
        MEMBER;
    }
}
