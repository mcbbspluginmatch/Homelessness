/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api;

import java.util.Set;
import java.util.UUID;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author BergStudio
 */
public abstract interface Risker extends Visual, View.Viewable, Storable {

    public Combat getCombat();

    public void setCombat(Combat paramCombat);

    public Experience experience();

    public Bank bank();

    public OfflinePlayer getPlayer();

    public UUID getUniqueId();

    public boolean isFriend(OfflinePlayer paramOfflinePlayer);
    
    public Friends friends();

    public boolean withdraw(double paramDouble);

    public void deposit(double paramDouble);
    
    public Set<UUID> getOwnedArea();

    public static enum Combat {

        FIGHTING("§c战斗中", 14),
        BUILDING("§a建造中", 5),
        FREE("§f空闲", 0),
        SOCIAL("§b社交", 3),
        EXPLORING("§6探索中", 4),
        STANDARD("§a§l在线", 5);

        private final String describe;
        private final short data;

        private Combat(String paramDescribe, int paramInteger) {
            describe = paramDescribe;
            data = (short) paramInteger;
        }

        public String getDescribe() {
            return describe;
        }

        public short getData() {
            return data;
        }

        public static Combat getCombat(String string) {
            return Combat.valueOf(string.toUpperCase());
        }
    }

}
