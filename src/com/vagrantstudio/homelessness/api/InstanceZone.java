/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public abstract interface InstanceZone extends Nameable, Unique, Visual, Mission, Storable{
    
    /**
     *
     * @return
     */
    public abstract Area getArea();
    
    /**
     * 
     * @param paramArena
     */
    public abstract void setArea(Area paramArena);
    
    public abstract int getMaxPlayer();
    
    public abstract void setMaxPlayer(int paramInteger);
    
    public abstract void join(Player paramPlayer);
    
    public abstract void join(Party paramParty);
    
    public abstract void leave(Player paramPlayer);
    
    public abstract void leaveAll();
    
    public abstract void start();
    
    public abstract void end();
    
    public abstract EnterMode getEnterMode();
    
    public abstract void setEnterMode(EnterMode paramEnterMode);
    
    public abstract long getMaxTime();
    
    public abstract void setMaxTime(long paramLong);
    
    public abstract boolean isMultiworld();   
    
    public abstract void setMultiworld(boolean paramBoolean);
    
    public abstract void addBreakedBlock(Block block);
    
    public abstract void addPlacedBlock(Block block);

    public abstract void reset();
    
    /**
     * 代表了这个关卡的模式的枚举
     */
    public static enum EnterMode{

        /**
         * 代表这个关卡的最大人数为 1
         * 是一个单人关卡
         */
        SINGLE_PLAYER,

        /**
         * 代表这个关卡可以多人进入
         * 是一个组队关卡
         */
        MULTI_PLAYER,

        /**
         * 代表这个关卡对人数没有限制
         * 通常用于刷怪点
         */
        UNLIMITED;
    }
    
    public static enum ZoneState{
        PLOT,
        THREAT,
        NORMAL,
        
    }
    
    
}
