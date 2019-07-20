/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.BridgePlayerPoints.PointsBank;
import com.vagrantstudio.homelessness.BridgeVault.VaultBank;
import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelBank implements Bank {

    protected static final Map<UUID, Bank> localMap = new HashMap();
    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Bank").replace("&", "§");
    protected static Sync sync = Sync.LOCAL;

    protected static ItemStack DEPOSIT = new CraftItemStack(Material.WOOL, (short) 5, "§a存款").create();
    protected static ItemStack WITHDRAW = new CraftItemStack(Material.WOOL, (short) 14, "§a取款").create();
    protected static ItemStack DONATE = new CraftItemStack(Material.WOOL, (short) 5, "§a捐献").create();

    private UUID localUniqueId;
    private double localDouble = 0.0D;

    static {
        String syncStr = PixelConfiguration.option.getString("Bank.Sync").toUpperCase();
        sync = Sync.valueOf(syncStr);
        switch (syncStr) {
            default:
            case "LOCAL":
                break;
            case "VAULT":
                for (OfflinePlayer paramOfflinePlayer : Bukkit.getOfflinePlayers()) {
                    localMap.put(paramOfflinePlayer.getUniqueId(), new VaultBank(paramOfflinePlayer));
                }   break;
            case "PLAYERPOINTS":
                for (OfflinePlayer paramOfflinePlayer : Bukkit.getOfflinePlayers()) {
                    localMap.put(paramOfflinePlayer.getUniqueId(), new PointsBank(paramOfflinePlayer.getUniqueId()));
                }   break;
        }
    }

    protected PixelBank(UUID paramUUID) {
        localUniqueId = paramUUID;
    }

    protected PixelBank(UUID paramUUID, double paramDouble) {
        localUniqueId = paramUUID;
        localDouble = paramDouble;
    }

    @Override
    public double getBalance() {
        return localDouble;
    }

    @Override
    public void setBalance(double paramDouble) {
        localDouble = paramDouble;
    }

    @Override
    public void deposit(double paramDouble) {
        localDouble = localDouble + paramDouble;
    }

    @Override
    public boolean withdraw(double paramDouble) {
        if (localDouble >= paramDouble) {
            localDouble = localDouble - paramDouble;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        localDouble = 0.0D;
    }

    @Override
    public ItemStack icon() {
        return Homelessness.core.getReflection().set(new CraftItemStack(Material.EMERALD_BLOCK, "§a银行", new String[]{"§7余额: " + localDouble}).create(), "uid", localUniqueId.toString());
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId;
    }

    public static enum Sync {

        LOCAL,
        VAULT,
        PLAYER_POINTS;
    }

}
