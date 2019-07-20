/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.WareCollection;
import com.vagrantstudio.homelessness.api.Warehouse;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelWareCollection implements WareCollection{
    
    protected Map<Integer, Warehouse> localWareMap = new HashMap();
    protected UUID localUniqueId;
    
    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Ware").replace("&", "§");
    protected static Map<Player, Map.Entry<Integer, WareCollection>> queue = new HashMap();
    protected static Map<UUID, WareCollection> localWareCollectionMap = new HashMap();
    protected static List<Integer> allowed = Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25});

    protected PixelWareCollection(ConfigurationSection paramConfigurationSection, UUID paramUUID){
        paramConfigurationSection.getKeys(false).stream().forEach((paramString) -> {
            int i = Integer.valueOf(paramString);
            localWareMap.put(i, new PixelWare(paramConfigurationSection.getStringList(paramString), paramUUID, i));
        });
        localUniqueId = paramUUID;
    }
    
    protected PixelWareCollection(UUID paramUUID){
        localUniqueId = paramUUID;
    }

    @Override
    public void set(Integer paramInteger, Warehouse paramWarehouse) {
        if(localWareMap.size() >= 28 || !allowed.contains(paramInteger)) return;
        localWareMap.put(paramInteger, paramWarehouse);
    }
    
    @Override
    public void add(Warehouse paramWarehosue){
        if(localWareMap.size() >= 28) return;
        int largest = 10;
        for(Integer paramInteger : localWareMap.keySet()){
            if(paramInteger > largest) largest = paramInteger;
        }
        localWareMap.put(allowed.get(allowed.indexOf(largest) + 1), paramWarehosue);
    }

    @Override
    public boolean contains(Integer paramInteger) {
        return localWareMap.containsKey(paramInteger);
    }

    @Override
    public Warehouse get(Integer paramInteger) {
        return localWareMap.get(paramInteger);
    }

    @Override
    public void reset() {
        localWareMap.clear();
    }

    @Override
    public boolean contains(Warehouse paramWarehouse) {
        return localWareMap.containsValue(paramWarehouse);
    }

    @Override
    public ItemStack icon() {
        List<String> list = new ArrayList();
        list.add("§7本仓库共有 " + localWareMap.values().size() + " 个子仓库");
        localWareMap.forEach((paramString, paramWare) -> {
            list.add("§7<§a" + paramString + "§7> 已用 " + paramWare.scale() + "%");
        });
        return Homelessness.core.getReflection().set(new CraftItemStack(Material.CHEST, "§a仓库", list).create(), "uid", localUniqueId.toString());
    }
    
    @Override
    public ConfigurationSection toConfigurationSection(){
        ConfigurationSection section = new YamlConfiguration();
        localWareMap.keySet().stream().forEach((paramInteger) -> {
            section.set(paramInteger.toString(), localWareMap.get(paramInteger).toStringList());
        });
        return section;
    }

    @Override
    public Map<Integer, Warehouse> all() {
        return localWareMap;
    }

    @Override
    public void remove(Integer paramInteger) {
        localWareMap.remove(paramInteger);
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId;
    }

    @Override
    public View getView() {
        View view = new PixelView(localUniqueId);
        ItemStack itemStackHolder = new CraftItemStack(Material.STAINED_GLASS_PANE, (short)3, "§a点击创建新的仓库").create();
        allowed.stream().forEach((paramInteger) -> {
            view.setItem(paramInteger, itemStackHolder);
        });
        localWareMap.forEach((paramInteger, paramWare) -> {
            view.setItem(paramInteger, paramWare.icon());
        });
        return view;
    }
    
    @Override
    public void updateView(Player paramPlayer) {
        PixelView.addView(paramPlayer, "§a仓库储集", getView());
    }
    
}
