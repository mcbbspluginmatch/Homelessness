/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public interface Task extends Nameable, Visual, Storable{
    
    public abstract void start(Player paramPlayer);
    
    public abstract void start(Party paramParty);
    
    public abstract List<String> getRequirement();
    
    public abstract List<String> getRecommendation();
    
    public abstract Type getType();
    
    public abstract ConfigurationSection getTaskSection();
    
    public static enum Type{
        MAIN_TASK("主线任务"),
        SIDE_TASK("支线任务"),
        NORMAL("普通任务");
        
        private final String s;
        
        private Type(String string){
            s = string;
        }
        
        public String getName(){
            return s;
        }
    }
    
}
