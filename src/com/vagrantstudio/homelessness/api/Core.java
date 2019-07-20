/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import com.vagrantstudio.homelessness.reflect.Reflection;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author BergStudio
 */
public abstract class Core extends JavaPlugin implements CommandExecutor, Listener{
    
    public abstract TriggerScheduler getEventScheduler();
    
    public abstract Guild getGuildByPlayer(OfflinePlayer offline);
    
    public abstract Guild getGuildByName(String name);
    
    public abstract Guild getGuildByUniqueId(UUID uid);
    
    public abstract Area getAreaByLocation(Location location);
    
    public abstract Area getAreaByUniqueId(UUID uid);
    
    public abstract Risker asRisker(OfflinePlayer offline);
    
    public abstract ChatChannel getChatChannelByPlayer(Player player);
    
    public abstract ChatChannel getChatChannelByUniqueId(UUID uid);
    
    public abstract Area createArea(Location location0, Location location1);
    
    public abstract Area createArea(Location location0, Location location1, String name);
    
    public abstract Area createArea(Location location0, Location location1, String name, Player player);
    
    public abstract Area createArea(Location location0, Location location1, String name, Guild guild);
    
    public abstract Bank getBankByPlayer(OfflinePlayer offline);
    
    public abstract Bank getBankByUniqueId(UUID uid);
    
    public abstract Experience getExperience(OfflinePlayer offline);
    
    public abstract Reflection getReflection();
    
    public abstract void deleteGuild(UUID uid);
    
    public abstract void deleteGuild(String name);
    
    public abstract void getStructure(String name);
    
    public abstract void upgrade(OfflinePlayer offline);
    
    public abstract void setEconomy(OfflinePlayer offline, double amount);
    
    public abstract void completeChallenge(OfflinePlayer offline, String name);
    
    public abstract void completeChallenge(OfflinePlayer offline, Challenge challenge);
    
    public abstract void sendBar(Player player, String message);
    
    public abstract void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut);
    
    public abstract void sendTitle(Player player, String title, String subTitle);
    
    public abstract void openView(Player player, String viewName);
    
    public abstract World copyWorld(String from, String to);
    
    public abstract World copyWorld(World world, String newName);
    
    public abstract void spawnParticle(Location location, int amount, Particle... particle);
    
    public abstract void spawnParticle(Location location, int amount, double offsetX, double offsetY, double offsetZ, Particle... particle);
    
    public abstract void spawnParticle(Location location, Particle... particle);
    
    public abstract void setFunction(String name, Function function);
}
