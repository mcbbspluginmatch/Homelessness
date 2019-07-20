/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.List;
import org.bukkit.inventory.ItemStack;


/**
 *
 * @author BergStudio
 */
public interface Warehouse extends Visual{
    
    /**
     *
     * @param paramItemStack
     * @return
     */
    public boolean add(ItemStack paramItemStack);
    
    public void set(int i, List<ItemStack> paramItemStackList);
    
    /**
     *
     * @param paramItemStack
     * @param deep
     * @return 
     */
    public boolean contains(ItemStack paramItemStack, boolean deep);
    
    /**
     *
     * @param paramItemStack 即代表要删除的物品
     * @param deep 是否忽视数量区别 删除所有该类物品
     */
    public void remove(ItemStack paramItemStack, boolean deep);
    
    public List<ItemStack> get(int i);
    
    public ItemStack get(int page, int i);
    
    public View page(int i);
    
    public void reset();
    
    public boolean isFull();

    public List<String> toStringList();
    
    public void setMaximum(int i);
    
    public int getMaximum();
    
    public double scale();
}
