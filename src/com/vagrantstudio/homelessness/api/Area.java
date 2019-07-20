/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author BergStudio
 */
public interface Area extends Nameable, Visual, View.Viewable, Storable{
    
    public Location getLoc1();
    
    public Location getLoc2();
    
    public void setPos1(Location paramLocation);
    
    public void setPos2(Location paramLocation);
    
    public Location getTeleportLocation();
    
    public void setTeleportLocation(Location paramLocation);
    
    public Feudal getFeudal();
    
    public void setFeudal(Feudal paramFeudalLord);
    
    public boolean inArea(Location paramLocation);
    
    public boolean inArea(int x, int y, int z);
    
    public OwnerType getOwnerType();
    
    public UUID getOwnerId();
    
    public void setOwnerId(UUID paramUniqueId);
    
    public void setOwnerType(OwnerType paramOwnerType);
    
    public boolean isPlayerOwned();
    
    public boolean isGuildOwned();

    public Set<Location> corners();
    
    public UUID getUniqueId();
    
    public int getLengthX();
    
    public int getLengthY();
    
    public int getLengthZ();
    
    public World getWorld();
    
    public static enum OwnerType{
        OWNED("§a已被占领"),
        SERVER_LAND("§a不可占领"),
        NOT_OWNED("§a无主之地");
        
        private final String name;
        
        private OwnerType(String s){ name = s; }
        
        public String getName() { return name; }
        
    }
    
}
