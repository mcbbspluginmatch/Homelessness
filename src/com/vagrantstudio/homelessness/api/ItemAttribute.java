/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author BergStudio
 */
public class ItemAttribute {

    public static class Sort {

        private String localString;
        private EquipmentSlot[] localSlotArray;

        private static final Map<String, Sort> localMap = new HashMap();

        public static final Sort ARMOUR = new Sort("护甲", new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS});
        public static final Sort SWORD = new Sort("剑", new EquipmentSlot[]{EquipmentSlot.HAND});
        public static final Sort TOOL = new Sort("工具", new EquipmentSlot[]{EquipmentSlot.HAND});
        public static final Sort SHIELD = new Sort("盾牌", new EquipmentSlot[]{EquipmentSlot.OFF_HAND, EquipmentSlot.HAND});
        public static final Sort BOW = new Sort("弓箭", new EquipmentSlot[]{EquipmentSlot.OFF_HAND, EquipmentSlot.HAND});
        public static final Sort POTION = new Sort("药水", new EquipmentSlot[]{EquipmentSlot.OFF_HAND, EquipmentSlot.HAND});
        public static final Sort TOME = new Sort("法典", new EquipmentSlot[]{EquipmentSlot.OFF_HAND});

        static {
            localMap.put("ARMOUR", ARMOUR);
            localMap.put("SWORD", SWORD);
            localMap.put("TOOL", TOOL);
            localMap.put("SHIELD", SHIELD);
            localMap.put("BOW", BOW);
            localMap.put("POTION", POTION);
            localMap.put("TOME", TOME);
        }

        public Sort(String paramString, EquipmentSlot[] paramSlotArray) {
            localString = paramString;
            localSlotArray = paramSlotArray;
        }

        public EquipmentSlot[] getSlots() {
            return localSlotArray;
        }

        public String getName() {
            return localString;
        }

        public static Sort getSort(String paramString) {
            return localMap.get(paramString.toUpperCase());
        }

        public static void addSort(String paramString, Sort paramSort) {
            localMap.put(paramString, paramSort);
        }
    }

    public static enum Quality {

        NORMAL("§7普通"),
        REFINEMENT("§a精致"),
        DISTINCTION("§9优秀"),
        COLLECTION("§b§l典藏"),
        UNCOMMON("§6稀有"),
        EXCELLENCE("§c卓越"),
        EPIC("§5史诗"),
        PERFECT("§4完美"),
        LEGENDARY("§e传说"),
        GOD("§d§l天赐");

        private final String name;

        private Quality(String s) {
            this.name = s;
        }

        public String getName() {
            return name;
        }
    }
}
