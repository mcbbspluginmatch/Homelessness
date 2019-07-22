/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.google.common.collect.Maps;
import com.vagrantstudio.homelessness.api.View;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelView implements View {

    protected static final Map<Player, Map<String, View>> localMenuMap = new HashMap();

    protected Map<Integer, ItemStack> localItemStackMap = new HashMap();
    protected UUID localUniqueId = null;
    protected int page = -1;

    protected static Map<String, View> addView(Player paramPlayer, String paramString, View paramView) {
        Map<String, View> map = localMenuMap.get(paramPlayer);
        if(map.size() >= 9) return map;
        map.put(paramString, paramView);
        localMenuMap.replace(paramPlayer, map);
        return map;
    }

    protected static Map<String, View> removeView(Player paramPlayer, String... paramStringArray) {
        Map<String, View> map = localMenuMap.get(paramPlayer);
        for (String paramString : paramStringArray) {
            map.remove(paramString);
        }
        localMenuMap.replace(paramPlayer, map);
        return map;
    }
    
    private PixelView(UUID paramUniqueId, Map map, int paramInteger){
        localUniqueId = paramUniqueId;
        localItemStackMap = Maps.newHashMap(map);
        page = paramInteger;
    }

    protected PixelView() {
    }

    protected PixelView(UUID paramUniqueId) {
        localUniqueId = paramUniqueId;
    }

    @Override
    public Map<Integer, ItemStack> getItems() {
        return localItemStackMap;
    }

    @Override
    public void setItem(int paramInteger, ItemStack paramItemStack) {
        localItemStackMap.put(paramInteger, paramItemStack);
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId;
    }

    @Override
    public void setUniqueId(UUID paramUniqueId) {
        localUniqueId = paramUniqueId;
    }

    @Override
    public void addItem(ItemStack paramItemStack) {
        if (localItemStackMap.isEmpty()) {
            localItemStackMap.put(0, paramItemStack);
            return;
        }
        Iterator<Integer> it = localItemStackMap.keySet().iterator();
        while(it.hasNext()){
            int i = it.next();
            if (!localItemStackMap.containsKey(i + 1) && (i + 1) < 36) {
                localItemStackMap.put(i + 1, paramItemStack);
            }
        }
    }

    @Override
    public ItemStack getItem(int paramInteger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public View clone() throws CloneNotSupportedException {
        return new PixelView(localUniqueId, localItemStackMap, page);
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public void setPageNumber(int paramInteger) {
        page = paramInteger;
    }

}
