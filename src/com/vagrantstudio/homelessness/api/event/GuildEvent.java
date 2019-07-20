/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.event;

import com.vagrantstudio.homelessness.api.Guild;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author BergStudio
 */
public abstract class GuildEvent extends Event{
    
    private HandlerList handlers = new HandlerList();
    private Guild guild;
    
    public GuildEvent(Guild paramGuild){
        guild = paramGuild;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
