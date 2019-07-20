/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.UUID;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class BridgePlayerPoints implements Listener{
    
    private static PlayerPointsAPI api;
    
    protected BridgePlayerPoints(){
        api = ((PlayerPoints)Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
    }
    
    public static class PointsBank implements Bank{
        
        private UUID localUniqueId;
        
        public PointsBank(UUID paramUniqueId){
            localUniqueId = paramUniqueId;
        }

        @Override
        public double getBalance() {
            return api.look(localUniqueId);
        }

        @Override
        public void setBalance(double paramDouble) {
            api.set(localUniqueId, (int) paramDouble);
        }

        @Override
        public void deposit(double paramDouble) {
            api.give(localUniqueId, (int) paramDouble);
        }

        @Override
        public boolean withdraw(double paramDouble) {
            return api.take(localUniqueId, (int) paramDouble);
        }

        @Override
        public void clear() {
            api.reset(localUniqueId);
        }

        @Override
        public ItemStack icon() {
            return Homelessness.core.getReflection().set(new CraftItemStack(Material.EMERALD_BLOCK, "§a银行", new String[]{"§7余额: " + getBalance()}).create(), "uid", localUniqueId.toString());
        }

        @Override
        public UUID getUniqueId() {
            return localUniqueId;
        }
        
    }
}
