/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @since v0.0.2
 * @author BergStudio
 */
public final class CraftItemStack {
    
    private Material material;
    private int amount = 1;
    private short data = 0;
    private String name = "";
    private String[] lore = new String[]{};
    private Map<Enchantment, Integer> enchantment = new HashMap();
    private List<ItemFlag> itemFlag = new ArrayList();
    
    public CraftItemStack(Material m){
        material = m;
    }
    
    public CraftItemStack(Material m, short s){
        this(m);
        data = s;
    }
    
    public CraftItemStack(Material m, String str){
        this(m);
        name = str;
    }
    
    public CraftItemStack(Material m, short s, String str){
        this(m, s);
        name = str;
    }
    
    public CraftItemStack(Material m, String str, String[] stra){
        this(m, str);
        lore = stra;
    }
    
    public CraftItemStack(Material m, String str, List<String> strl){
        this(m, str, strl.toArray(new String[]{}));
    }
    
    public CraftItemStack(Material m, short s, String str, String[] stra){
        this(m, s, str);
        lore = stra;
    }
    
    public CraftItemStack(Material m, String str, Enchantment[] ench, int[] l){
        this(m, str);
        putEnchant(ench, l);
    }
    
    public CraftItemStack(Material m, String str, String[] stra, Enchantment[] ench, int[] l){
        this(m, str, stra);
        putEnchant(ench, l);
    }
    
    public CraftItemStack(Material m, String str, Enchantment[] ench, int[] l, ItemFlag[] f){
        this(m, str, ench, l);
        putFlag(f);
    }
    
    public CraftItemStack(Material m, short s, String str, String[] stra, Enchantment[] ench, int[] l){
        this(m, s, str, stra);
        putEnchant(ench, l);
    }
    
    public CraftItemStack(Material m, String str, String[] stra, Enchantment[] ench, int[] l, ItemFlag[] f){
        this(m, str, stra, ench, l);
        putFlag(f);
    }
    
    public CraftItemStack(Material m, short s, String str, ItemFlag[] flag){
        this(m, s, str);
        itemFlag.addAll(Arrays.asList(flag));
    }
    
    public void putFlag(ItemFlag... flag){
        itemFlag.addAll(Arrays.asList(flag));
    }
    
    public void putEnchant(Enchantment ench, int l){
        enchantment.put(ench, l);
    }
    
    public void putEnchant(Enchantment[] ench, int[] l){
        for(int i = 0; i < ench.length && i < l.length; i++){
            enchantment.put(ench[i], l[i]);
        }
    }
    
    public boolean isSimilar(ItemStack item){
        return item.getType() == material && item.getDurability() == data;
    }
    
    public ItemStack create(){
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(material);
        meta.setDisplayName(name);
        enchantment.forEach((paramEnchantment, paramInteger) -> {
            meta.addEnchant(paramEnchantment, paramInteger, true);
        });
        meta.addItemFlags(itemFlag.toArray(new ItemFlag[]{}));
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    public boolean isLegal(){
        return true;
    }
}
