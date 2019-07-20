/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.event.player;

import com.vagrantstudio.homelessness.api.Experience;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author BergStudio
 */
public class PlayerExpChangeEvent extends PlayerEvent implements Cancellable{
    private int localInteger = 0;
    private boolean localBoolean = true;
    private Experience localExperience;
    private static HandlerList handlers = new HandlerList();

    public PlayerExpChangeEvent(Player paramPlayer, int paramInteger, boolean paramBoolean, Experience paramExperience) {
        super(paramPlayer);
    }

    @Override
    public HandlerList getHandlers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCancelled(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean increase(){
        return localBoolean;
    }
    
    public Experience experience(){
        return localExperience;
    }
    
}
