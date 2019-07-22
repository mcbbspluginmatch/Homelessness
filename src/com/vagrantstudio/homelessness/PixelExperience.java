/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Experience;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import com.vagrantstudio.homelessness.api.util.Numeric;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelExperience implements Experience {

    protected static String formula = null;

    protected int lvl = 1;
    protected int exp = 0;
    protected int expTo = 1;
    protected float percentage = 0;
    protected OfflinePlayer player;

    protected PixelExperience(OfflinePlayer who) {
        player = who;
        if (formula == null) {
            formula = PixelConfiguration.option.getString("Level");
            expTo = Numeric.calculate(formula.replace("%lvl", String.valueOf(lvl)));
        }
    }

    protected PixelExperience(int exp, int lvl, OfflinePlayer who) {
        this(who);
        this.exp = exp;
        this.lvl = lvl;
    }

    @Override
    public void setExp(int paramInteger) {
        exp = paramInteger;
    }

    @Override
    public void addExp(int paramInteger) {
        exp += paramInteger;
        new Thread() {
            @Override
            public void run() {
                boolean lvlup = false;
                while (exp >= expTo) {
                    lvlup = true;
                    exp -= expTo;
                    lvl++;
                    expTo = Numeric.calculate(formula.replace("%lvl", String.valueOf(lvl)));
                }
                if (player.isOnline()) {
                    Player online = player.getPlayer();
                    float newPercentage = (new BigDecimal(exp * 1.0F / expTo).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                    try {
                        if (lvlup) {
                            for (float loop = percentage; loop <= 1; loop += 0.01F) {
                                online.setExp(loop);
                                Thread.sleep(12);
                            }
                            online.setLevel(lvl);
                            Homelessness.core.sendTitle(online, "§a升级", "§7当前等级 §6§l> §a" + lvl, 2, 60, 5);
                            for(float loop = 1.0F; loop > 0; loop -= 0.04F){
                                online.setExp(loop);
                                Thread.sleep(7);
                            }
                            for (float loop = 0.0F; loop <= newPercentage; loop += 0.01F) {
                                online.setExp(loop);
                                Thread.sleep(12);
                            }
                            percentage = newPercentage;
                        } else {
                            for (float loop = percentage; loop <= newPercentage; loop += 0.01F){
                                online.setExp(loop);
                                Thread.sleep(15);
                            }
                            percentage = newPercentage;
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PixelExperience.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();

    }

    @Override
    public int getExp() {
        return exp;
    }

    @Override
    public int getLevel() {
        return lvl;
    }

    @Override
    public void reset() {
        exp = 0;
        lvl = 1;
        expTo = Numeric.calculate(formula.replace("%lvl", String.valueOf(lvl)));
    }

    @Override
    public void upgrade() {

    }

    @Override
    public void upgrade(int paramInteger) {

    }

    @Override
    public ItemStack icon() {
        return new CraftItemStack(Material.EXP_BOTTLE, "§a经验值", new String[]{"§7当前等级 §6§l> §e" + lvl,
            "§7当前经验值 §6§l> §e" + exp}).create();
    }

}
