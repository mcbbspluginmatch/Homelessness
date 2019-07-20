/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.comphenix.protocol.utility.StreamSerializer;
import com.vagrantstudio.homelessness.api.Item;
import com.vagrantstudio.homelessness.api.ItemAttribute;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author BergStudio
 */
public class PixelItem implements Item {
    
    private static StreamSerializer localStreamSerializer = new StreamSerializer();
    protected static final File localFile = new File("plugins/Vagrant/item");
    
    private String localString = "default";
    private ItemAttribute.Sort localSort;
    private ItemAttribute.Quality localQuality;
    private Map<String, Object> localMap = new HashMap();
    private ItemStack localItemStack;
    private Map<Enchantment, Integer> enchants = new HashMap();
    private List<ItemFlag> flags = new ArrayList();
    private short durability = 0;
    
    static{
        localFile.mkdirs();
    }

    protected PixelItem(File paramFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(paramFile);
        localSort = ItemAttribute.Sort.getSort(yaml.getString("Sort"));
        localQuality = ItemAttribute.Quality.valueOf(yaml.getString("Quality").toUpperCase());
        ConfigurationSection cs = yaml.getConfigurationSection("Tag");
        cs.getKeys(false).stream().forEach((paramString) -> {
            localMap.put("vagrant:item:" + paramString.toLowerCase(), cs.get(paramString));
        });
        localItemStack = new ItemStack(Material.getMaterial(yaml.getString("Material")),
                1,
                (short) yaml.getInt("Data"));
        ItemMeta meta = localItemStack.getItemMeta();
        if (yaml.contains("Enchant")) {
            for (String paramString : yaml.getStringList("Enchant")) {
                String[] array = paramString.split("=");
                meta.addEnchant(Enchantment.getByName(array[0].toUpperCase()), Integer.parseInt(array[1]), true);
            }
        }
        if (yaml.contains("Flag")) {
            for (String paramString : yaml.getStringList("Flag")) {
                meta.addItemFlags(ItemFlag.valueOf(paramString.toUpperCase()));
            }
        }
        if (yaml.getBoolean("Unbreakable")) {
            meta.spigot().setUnbreakable(true);
        }
        localItemStack.setItemMeta(meta);
        localString = yaml.contains("Model") ? yaml.getString("Model") : localString;
    }

    protected PixelItem() {
        localSort = ItemAttribute.Sort.SWORD;
    }

    protected PixelItem(ItemStack paramItemStack) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        localItemStack = paramItemStack;
        localMap = Homelessness.core.getReflection().getTags(paramItemStack);
        localSort = ItemAttribute.Sort.getSort((String) localMap.get("vagrant:item:sort"));
    }

    private PixelItem(ItemAttribute.Sort paramSort, Map<String, Object> paramMap) {
        localSort = paramSort;
        localMap = paramMap;
    }

    @Override
    public void setSort(ItemAttribute.Sort paramSort) {
        localSort = paramSort;
    }

    @Override
    public ItemAttribute.Sort getSort() {
        return localSort;
    }

    @Override
    public Map<String, Object> elements() {
        return localMap;
    }

    @Override
    public void setValue(String paramString, Object paramObject) {
        localMap.put(paramString, paramObject);
    }

    @Override
    public Object getValue(String paramString) {
        return localMap.get(paramString);
    }

    @Override
    public boolean isElement(String paramString) {
        return localMap.containsKey(paramString);
    }

    protected ItemStack asBukkitItemStack() {
        ItemStack item = localItemStack.clone();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList();
        lore.add("§8§m───────────────§1 ╳ §a简明 §1╳ §8§m───────────────");
        lore.add("§7>> §a品质 §6§l× " + localQuality.getName());
        lore.add("§7>> §a类型 §6§l× §f" + localSort.getName());
        lore.add("§8§m───────────────§1 ╳ §b属性 §1╳ §8§m───────────────");
        localMap.keySet().stream().forEach((paramString) -> {

        });
        meta.setLore(lore);
        item.setItemMeta(meta);
        try {
            item = Homelessness.core.getReflection().setTags(item, localMap);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PixelItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return item;
    }

    protected static final String serialize(ItemStack paramItemStack) {
        try {
            return localStreamSerializer.serializeItemStack(paramItemStack);
        } catch (IOException ex) {
            Logger.getLogger(PixelItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    protected static final ItemStack deserialize(String paramString){
        try {
            return localStreamSerializer.deserializeItemStack(paramString);
        } catch (IOException ex) {
            Logger.getLogger(PixelItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
