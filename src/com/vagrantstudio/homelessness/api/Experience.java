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
public interface Experience extends Visual{
    
    public void setExp(int paramInteger);
    
    public void addExp(int paramInteger);
    
    public int getExp();
    
    public int getLevel();
    
    public void reset();
    
    public void upgrade();
    
    public void upgrade(int paramInteger);
    
}
