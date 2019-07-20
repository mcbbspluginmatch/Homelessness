/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 *
 * @author BergStudio
 */
public abstract class Trigger implements Listener {

    @Override
    public abstract boolean equals(Object obj);
    
    public abstract void call(Event event);

    public abstract void register();

    public abstract void unregister();
    
    public abstract void tips();
    
    @Override
    public abstract String toString();
}
