/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

/**
 *
 * @author BergStudio
 */
public interface Freezable {
    
    public abstract void freeze();
    
    public abstract boolean isFreeze();
    
    public abstract void unfreeze();
    
    public abstract void setFreeze(boolean freeze);
    
}
