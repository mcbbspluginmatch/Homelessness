/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.event;

import com.vagrantstudio.homelessness.api.ChatChannel;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author BergStudio
 */
public abstract class ChatChannelEvent extends Event{
    
    private ChatChannel localChatChannel;
    private HandlerList handlers = new HandlerList();
    
    public ChatChannelEvent(ChatChannel paramChatChannel){
        localChatChannel = paramChatChannel;
    }
    
    public ChatChannel getChatChannel(){
        return localChatChannel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
