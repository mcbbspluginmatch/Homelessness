/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public abstract interface View extends Cloneable {

    public Map<Integer, ItemStack> getItems();

    public ItemStack getItem(int paramInteger);

    public void setItem(int paramInteger, ItemStack paramItemStack);

    public void addItem(ItemStack paramItemStack);
    
    public int getPageNumber();
    
    public void setPageNumber(int paramInteger);

    public UUID getUniqueId();

    public void setUniqueId(UUID paramUniqueId);

    public View clone() throws CloneNotSupportedException;

    public static abstract interface Viewable {

        public View getView();

        public void updateView(Player paramPlayer);

    }

    public static class ViewMap {

        private Map<Integer, Map<String, View>> views = new HashMap();

        public void add(String paramString, View paramView) {
            if (contains(paramString)) return;
            for (Map<String, View> map : views.values()) {
                if (map.size() < 9) {
                    map.put(paramString, paramView);
                    return;
                }
            }
            if(views.size() < 6){
                int max = 0;
                for(int i = 1; i <= 6; i++){
                    if(!views.containsKey(i)) max = i;
                }
                Map<String, View> map = new HashMap();
                map.put(paramString, paramView);
                views.put(max, map);
            }
        }
        
        public void replace(String paramString, View paramView){
            
        }
        
        public boolean contains(View paramView) {
            return views.values().stream().anyMatch((map) -> (map.containsValue(paramView)));
        }

        public boolean contains(String paramString) {
            return views.values().stream().anyMatch((map) -> (map.containsKey(paramString)));
        }
        
        public void remove(String paramString){
            views.values().forEach((paramMap) -> {
                paramMap.remove(paramString);
            });
        }
        
        public Map<String, View> all(){
            Map<String, View> map = new HashMap();
            views.values().forEach((paramMap) -> {
                map.putAll(paramMap);
            });
            return map;
        }
        
        public Set<String> keys(){
            Set<String> keySet = new HashSet();
            views.values().forEach((paramMap) -> { keySet.addAll(paramMap.keySet()); });
            return keySet;
        }
        
        public Collection<View> values(){
            Collection<View> collection = new LinkedList();
            views.values().forEach((paramMap) -> { collection.addAll(paramMap.values()); });
            return collection;
        }

    }

}
