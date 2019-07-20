/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.reflect;

import java.io.File;
import java.util.Map;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public interface Reflection {
    
    public abstract Object asNMSCopy(ItemStack itemStack);
    
    public abstract ItemStack asBukkitCopy(Object object);
    
    public abstract String getTagString(Object object, String key);
    
    public abstract boolean getTagBoolean(Object object, String key);
    
    public abstract int getTagInteger(Object object, String key);
    
    public abstract double getTagDouble(Object object, String key);
    
    public abstract Set<String> getTagKeys(Object object);
    
    public abstract Map<String, Object> getTags(Object object);
    
    public abstract String getTagString(ItemStack itemStack, String key);
    
    public abstract boolean getTagBoolean(ItemStack itemStack, String key);
    
    public abstract int getTagInteger(ItemStack itemStack, String key);
    
    public abstract double getTagDouble(ItemStack itemStack, String key);
    
    public abstract Set<String> getTagKeys(ItemStack itemStack);
    
    public abstract Map<String, Object> getTags(ItemStack itemStack);
    
    public abstract ItemStack set(Object object, String key, Object value);
    
    public abstract ItemStack set(ItemStack itemStack, String key, Object value);
    
    public abstract ItemStack setTags(ItemStack itemStack, Map<String, Object> map);
    
    public abstract ItemStack setTags(Object object, Map<String, Object> map);
    
    public abstract ItemStack replaceTag(ItemStack itemStack, Map<String, Object> map);
    
    public abstract ItemStack replaceTag(Object object, Map<String, Object> map);
    
}
