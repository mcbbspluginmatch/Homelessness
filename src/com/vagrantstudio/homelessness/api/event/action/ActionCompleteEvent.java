/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.event.action;

import com.vagrantstudio.homelessness.api.Trigger;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author BergStudio
 */
public class ActionCompleteEvent extends PlayerEvent{
    private Trigger action;
    
    private static HandlerList handlers = new HandlerList();

    public ActionCompleteEvent(Player who) {
        super(who);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList(){
        return handlers;
    }
    
}
