/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.Warehouse;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import com.vagrantstudio.homelessness.api.util.ObjectSerializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelWare implements Warehouse {

    protected static ItemStack locked = new CraftItemStack(Material.STAINED_GLASS_PANE, (short) 14, "§c未解锁✘",
            new String[]{"§7这个储存槽锁着", "§7点击以支付 " + PixelConfiguration.option.getInt("Ware.Unlock_Cost") + " 元来解锁"}).create();

    /* 
     *K<Integer> page number
     *V<List<ItemStack>> items in page K
     */
    protected UUID localUniqueId;
    protected int key;
    protected Map<Integer, List<ItemStack>> localItemStackMap = new HashMap();
    protected int localInteger = 27;

    protected PixelWare(List<String> paramStringList, UUID paramUUID, int paramInteger) {
        int pageNumber = 1;
        List<ItemStack> itemStackList = new ArrayList();
        for (String paramString0 : paramStringList) {
            if (itemStackList.size() <= 45) {
                try {
                    Object obj = ObjectSerializer.singleObjectFromString(paramString0, ItemStack.class);
                    itemStackList.add((ItemStack) obj);
                } catch (IOException ex) {
                    Logger.getLogger(PixelWare.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                localItemStackMap.put(pageNumber, itemStackList);
                itemStackList = new ArrayList();
                pageNumber++;
            }
        }
        localItemStackMap.put(localItemStackMap.containsKey(pageNumber) ? ++pageNumber : pageNumber, itemStackList);
        localUniqueId = paramUUID;
        key = paramInteger;
    }

    protected PixelWare(UUID paramUUID, int paramInteger) {
        localUniqueId = paramUUID;
        key = paramInteger;
        localItemStackMap.put(1, new ArrayList());
    }

    @Override
    public boolean add(ItemStack paramItemStack) {
        int sum = 0;
        for (int i : localItemStackMap.keySet()) {
            List<ItemStack> items = localItemStackMap.get(i);
            sum += items.size();
            if (sum >= localInteger) {
                return false;
            }
            if (items.size() < 45) {
                items.add(paramItemStack);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean contains(ItemStack paramItemStack, boolean deep) {
        if (deep) {
            if (localItemStackMap.values().stream().anyMatch((paramItemStackList) -> (paramItemStackList.contains(paramItemStack)))) {
                return true;
            }
        } else {
            if (localItemStackMap.values().stream().anyMatch((paramItemStackList)
                    -> (paramItemStackList.stream().anyMatch((paramItemStack0) -> (paramItemStack0.isSimilar(paramItemStack)))))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void remove(ItemStack paramItemStack, boolean deep) {
        if (deep) {
            localItemStackMap.values().stream().forEach((paramItemStackList) -> {
                paramItemStackList.remove(paramItemStack);
            });
        } else {
            localItemStackMap.values().stream().forEach((paramItemStackList) -> {
                paramItemStackList.stream().forEach((paramItemStack0) -> {
                    if (paramItemStack0.isSimilar(paramItemStack)) {
                        paramItemStackList.remove(paramItemStack0);
                    }
                });
            });
        }
    }

    @Override
    public List<ItemStack> get(int i) {
        return localItemStackMap.get(i);
    }

    @Override
    public View page(int i) {
        View view = new PixelView();
        for (int s = localInteger - 27 * (i - 1); s < 27; s++) {
            view.setItem(s, locked);
        }
        (localItemStackMap.containsKey(i) ? localItemStackMap.get(i) : new ArrayList<ItemStack>()).stream().forEach(view::addItem);
        view.setItem(27, ObjectSet.itemStackLastPage);
        view.setItem(35, ObjectSet.itemStackNextPage);
        view.setItem(31, Homelessness.core.getReflection().set(Homelessness.core.getReflection().set(icon(), "page", i), "location", key));
        view.setUniqueId(localUniqueId);
        return view;
    }

    @Override
    public void reset() {
        localItemStackMap.clear();
    }

    @Override
    public boolean isFull() {
        int sum = 0;
        sum = localItemStackMap.values().stream().map((paramItemStackList) -> paramItemStackList.size()).reduce(sum, Integer::sum);
        return sum >= localInteger;
    }

    @Override
    public void setMaximum(int i) {
        localInteger = i;
    }

    @Override
    public int getMaximum() {
        return localInteger;
    }

    @Override
    public double scale() {
        double sum = 0;
        for (List<ItemStack> paramItemStackList : localItemStackMap.values()) {
            sum += paramItemStackList.size();
        }
        return new BigDecimal((sum / localInteger) * 100.0).setScale(2, BigDecimal.ROUND_UP).doubleValue();
    }

    @Override
    public ItemStack icon() {
        double scale = scale();
        String str = "";
        for (int i = 1; i <= scale / 2; i++) {
            str += "§a|";
        }
        for (int i = 1; i <= (100 - scale) / 2; i++) {
            str += "§7|";
        }
        return new CraftItemStack(Material.WOOL, (short) (scale >= 80 ? 14 : 5),
                "§a仓库", new String[]{"§7本仓库已用空间 " + scale + "%", str}).create();
    }

    @Override
    public void set(int i, List<ItemStack> paramItemStackList) {
        List<ItemStack> itemStacks = new ArrayList();
        paramItemStackList.stream().forEach((paramItemStack) -> {
            if (paramItemStack != null && paramItemStack.getType() != Material.AIR && !"§c未解锁✘".equals(paramItemStack.getItemMeta().getDisplayName())) {
                itemStacks.add(paramItemStack);
            }
        });
        localItemStackMap.put(i, itemStacks);
    }

    @Override
    public ItemStack get(int page, int i) {
        return localItemStackMap.get(page).get(i);
    }

    @Override
    public List<String> toStringList() {
        List<String> listString = new ArrayList();
        localItemStackMap.values().stream().forEach((paramItemStackList) -> {
            paramItemStackList.stream().forEach((paramItemStack) -> {
                try {
                    listString.add(ObjectSerializer.singleObjectToString(paramItemStack));
                } catch (IOException ex) {
                    Logger.getLogger(PixelWare.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        return listString;
    }

}
