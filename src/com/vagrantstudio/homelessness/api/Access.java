/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public enum Access{
    PRIVATE("私有的", new CraftItemStack(Material.WOOL, (short)14, "§a私有的").create()),
    FRIENDLY("仅好友可见", new CraftItemStack(Material.WOOL, (short)1, "§a仅好友可见").create()),
    PUBLIC("公开的", new CraftItemStack(Material.WOOL, (short)5, "§a公开的").create());
    
    private final String localString;
    private final ItemStack localItemStack;
    
    private Access(String paramString, ItemStack paramItemStack){
        localString = paramString;
        localItemStack = paramItemStack;
    }
    
    public String getName(){
        return localString;
    }
    
    public ItemStack getIcon(){
        return localItemStack;
    }
}
