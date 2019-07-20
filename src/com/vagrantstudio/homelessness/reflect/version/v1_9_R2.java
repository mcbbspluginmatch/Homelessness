/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.reflect.version;

import com.vagrantstudio.homelessness.reflect.Reflection;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.NBTBase;
import net.minecraft.server.v1_9_R2.NBTTagByte;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NBTTagDouble;
import net.minecraft.server.v1_9_R2.NBTTagInt;
import net.minecraft.server.v1_9_R2.NBTTagList;
import net.minecraft.server.v1_9_R2.NBTTagString;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;

/**
 *
 * @author BergStudio
 */
public class v1_9_R2 implements Reflection{

    private Field nbtCompound;

    public v1_9_R2() {
        try {
            nbtCompound = NBTTagCompound.class.getDeclaredField("map");
        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(v1_9_R2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object asNMSCopy(org.bukkit.inventory.ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    @Override
    public org.bukkit.inventory.ItemStack asBukkitCopy(Object object) {
        return object instanceof ItemStack ? CraftItemStack.asBukkitCopy((ItemStack) object) : null;
    }

    @Override
    public String getTagString(Object object, String key) {
        return ((ItemStack) object).getTag().getString(key);
    }

    @Override
    public boolean getTagBoolean(Object object, String key) {
        return ((ItemStack) object).getTag().getBoolean(key);
    }

    @Override
    public int getTagInteger(Object object, String key) {
        return ((ItemStack) object).getTag().getInt(key);
    }

    @Override
    public double getTagDouble(Object object, String key) {
        return ((ItemStack) object).getTag().getDouble(key);
    }

    @Override
    public Map<String, Object> getTags(Object object) {
        try {
            return (Map<String, Object>) nbtCompound.get(((ItemStack) object).getTag());
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(v1_9_R2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getTagString(org.bukkit.inventory.ItemStack itemStack, String key) {
        return ((ItemStack) asNMSCopy(itemStack)).getTag().getString(key);
    }

    @Override
    public boolean getTagBoolean(org.bukkit.inventory.ItemStack itemStack, String key) {
        return ((ItemStack) asNMSCopy(itemStack)).getTag().getBoolean(key);
    }

    @Override
    public int getTagInteger(org.bukkit.inventory.ItemStack itemStack, String key) {
        return ((ItemStack) asNMSCopy(itemStack)).getTag().getInt(key);
    }

    @Override
    public double getTagDouble(org.bukkit.inventory.ItemStack itemStack, String key) {
        return ((ItemStack) asNMSCopy(itemStack)).getTag().getDouble(key);
    }

    @Override
    public Map<String, Object> getTags(org.bukkit.inventory.ItemStack itemStack) {
        return getTags(asNMSCopy(itemStack));
    }

    @Override
    public Set<String> getTagKeys(Object object) {
        return ((ItemStack) object).getTag().c();
    }

    @Override
    public Set<String> getTagKeys(org.bukkit.inventory.ItemStack itemStack) {
        return ((ItemStack) asNMSCopy(itemStack)).getTag().c();
    }

    @Override
    public org.bukkit.inventory.ItemStack set(Object object, String key, Object value) {
        ItemStack nmsItem = ((ItemStack) object);
        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        tag.set(key, (NBTBase) toTagBase(value));
        nmsItem.setTag(tag);
        return asBukkitCopy(nmsItem);
    }

    @Override
    public org.bukkit.inventory.ItemStack set(org.bukkit.inventory.ItemStack itemStack, String key, Object value) {
        return set(asNMSCopy(itemStack), key, value);
    }

    private Object toTagBase(Object object) {
        if (object instanceof Integer) {
            return new NBTTagInt((int) object);
        } else if (object instanceof Double) {
            return new NBTTagDouble((double) object);
        } else if (object instanceof String) {
            return new NBTTagString((String) object);
        } else if (object instanceof Boolean) {
            return new NBTTagByte(((boolean) object ? (byte) 1 : (byte) 0));
        } else if (object instanceof List) {
            NBTTagList tagList = new NBTTagList();
            ((List) object).forEach((paramObject) -> {
                tagList.add((NBTBase) toTagBase(paramObject));
            });
            return tagList;
        }
        return new NBTTagString(object.toString());
    }

    @Override
    public org.bukkit.inventory.ItemStack setTags(org.bukkit.inventory.ItemStack itemStack, Map<String, Object> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public org.bukkit.inventory.ItemStack setTags(Object object, Map<String, Object> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public org.bukkit.inventory.ItemStack replaceTag(org.bukkit.inventory.ItemStack itemStack, Map<String, Object> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public org.bukkit.inventory.ItemStack replaceTag(Object object, Map<String, Object> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
