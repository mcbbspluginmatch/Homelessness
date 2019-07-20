/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Area;
import com.vagrantstudio.homelessness.api.Game;
import com.vagrantstudio.homelessness.api.Guild;
import com.vagrantstudio.homelessness.api.util.CraftRegion;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public class GameConguest extends Game{
    private Guild offensive;
    private Guild defender;
    private Area place;
    private CraftRegion region;
    private int respawnTime = 0;
    
    public GameConguest(Guild g0, Guild g1, Area where){
        offensive = g0;
        defender = g1;
        place = where;
    }
    
    public GameConguest(Guild g0, Guild g1, Area where, int time){
        this(g0, g1, where);
        setMaxTime(time);
        region = new CraftRegion(where.getLoc1(), where.getLoc2());
    }
    
    @Override
    public void start(){
        
    }
    
    @Override
    public void stop(){
        
    }
    
    @Override
    public void pause(){
        
    }
    
    @Override
    public void resume(){
        
    }
    
    public void join(Player player){
        
    }
    
    public Guild getOffensive(){ return offensive; }
    
    public Guild getDefender(){ return defender; }
    
    public Area getArea(){ return place; }
    
    public CraftRegion getRegion(){ return region; }
    
    public int getRespawnTime(){ return respawnTime; }
    
    public void setArea(Area area){
        if(isPlaying()) stop();
        place = area;
        region = new CraftRegion(area.getLoc1(), area.getLoc2());
    }
}
