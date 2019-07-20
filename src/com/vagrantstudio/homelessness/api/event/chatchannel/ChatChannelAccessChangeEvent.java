/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api.event.chatchannel;

import com.vagrantstudio.homelessness.api.Access;
import com.vagrantstudio.homelessness.api.ChatChannel;
import com.vagrantstudio.homelessness.api.event.ChatChannelEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 *
 * @author BergStudio
 */
public class ChatChannelAccessChangeEvent extends ChatChannelEvent
        implements Cancellable {

    public static HandlerList handlers = new HandlerList();
    private Access localAccess0;
    private Access localAccess1;
    private boolean cancel = false;

    public ChatChannelAccessChangeEvent(ChatChannel paramChatChannel, Access from, Access result) {
        super(paramChatChannel);
        localAccess0 = from;
        localAccess1 = result;
    }

    public Access getFrom() {
        return localAccess0;
    }

    public Access getResult() {
        return localAccess1;
    }

    public void setResult(Access paramAccess) {
        localAccess1 = paramAccess;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean bln) {
        cancel = bln;
    }

}
