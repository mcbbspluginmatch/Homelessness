/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author BergStudio
 */
public interface WareCollection extends Visual, View.Viewable, Unique{
    
    public void set(Integer paramInteger, Warehouse paramWarehouse);
    
    public void add(Warehouse paramWarehouse);
    
    public boolean contains(Integer paramInteger);
    
    public boolean contains(Warehouse paramWarehouse);
    
    public Warehouse get(Integer paramInteger);
    
    public void remove(Integer paramInteger);
    
    public Map<Integer, Warehouse> all();
    
    public void reset();
    
    public abstract ConfigurationSection toConfigurationSection();
    
}
