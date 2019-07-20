/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Experience;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelExperience implements Experience{
    
    private int localInteger = 0;
    private boolean localBoolean = false;
    
    protected PixelExperience(){
        
    }
    
    protected PixelExperience(int paramInteger){
        localInteger = paramInteger;
    }

    @Override
    public void setExp(int paramInteger) {
        if(localBoolean) return;
        localInteger = paramInteger;
    }

    @Override
    public int getExp() {
        return localInteger;
    }

    @Override
    public int getLevel() {
        List<Integer> list = PixelConfiguration.option.getIntegerList("Level");
        int i = 0;
        for (Integer paramInteger : list) {
            if(localInteger >= paramInteger){
                i++;
            } else {
                return i;
            }
        }
        return i;
    }

    @Override
    public void reset() {
        if(localBoolean) return;
        localInteger = 0;
    }

    @Override
    public void upgrade() {
        if(localBoolean) return;
        localInteger = PixelConfiguration.option.getIntegerList("Level").get(getLevel() + 1);
    }

    @Override
    public void upgrade(int paramInteger) {
        if(localBoolean) return;
        localInteger = PixelConfiguration.option.getIntegerList("Level").get(getLevel() + paramInteger);
    }

    @Override
    public ItemStack icon() {
        return new CraftItemStack(Material.EXP_BOTTLE, "§a经验值", new String[]{"§a当前等级 §7>> §e" + getLevel(),
            "§a当前经验值"}).create();
    }

    @Override
    public void freeze() {
        localBoolean = true;
    }

    @Override
    public void unfreeze() {
        localBoolean = false;
    }

    @Override
    public void setFreeze(boolean paramBoolean) {
        localBoolean = paramBoolean;
    }

    @Override
    public boolean isFreeze() {
        return localBoolean;
    }
    
}
