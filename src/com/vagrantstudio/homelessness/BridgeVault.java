/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author BergStudio
 */
public class BridgeVault implements Listener {

    protected Economy economy = null;

    protected BridgeVault(Server server) {
        RegisteredServiceProvider<Economy> economyProvider = server.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }

    public boolean bankHas(OfflinePlayer paramOfflnePlayer, double value) {
        return economy.has(paramOfflnePlayer, value);
    }

    public boolean bankWithdraw(OfflinePlayer paramOfflinePlayer, double value) {
        return economy.withdrawPlayer(paramOfflinePlayer, value).transactionSuccess();
    }

    public boolean bankDeposit(OfflinePlayer paramOfflinePlayer, double value) {
        return economy.depositPlayer(paramOfflinePlayer, value).transactionSuccess();
    }

    public double bankBalance(OfflinePlayer paramOfflinePlayer) {
        return economy.getBalance(paramOfflinePlayer);
    }

    public static class VaultBank implements Bank {

        protected OfflinePlayer who = null;
        protected UUID localUniqueId = null;

        protected VaultBank(OfflinePlayer paramOfflinePlayer) {
            who = paramOfflinePlayer;
            localUniqueId = paramOfflinePlayer.getUniqueId();
        }

        @Override
        public double getBalance() {
            return Homelessness.hookVault.bankBalance(who);
        }

        @Override
        public void setBalance(double paramDouble) {
            double balance = getBalance();
            if (balance > paramDouble) {
                withdraw(balance - paramDouble);
            } else {
                deposit(balance - paramDouble);
            }
        }

        @Override
        public void deposit(double paramDouble) {
            Homelessness.hookVault.bankDeposit(who, paramDouble);
        }

        @Override
        public boolean withdraw(double paramDouble) {
            return Homelessness.hookVault.bankWithdraw(who, paramDouble);
        }

        @Override
        public void clear() {
            setBalance(0);
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
