/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class ListenerOfEntity implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickupItem(EntityPickupItemEvent paramEntityPickupItemEvent) {
        if (paramEntityPickupItemEvent.getEntity() instanceof Player) {
            Entity entity = paramEntityPickupItemEvent.getEntity();
            ItemStack itemStack = paramEntityPickupItemEvent.getItem().getItemStack();
            Player player = (Player) entity;
            if (PixelConfiguration.option.getBoolean("PressShiftToPickup") && !player.isSneaking()) {
                paramEntityPickupItemEvent.setCancelled(true);
                return;
            }
            if (PixelConfiguration.option.getBoolean("ActionTips.Pickup")) {
                Homelessness.core.sendTitle(player, "§b[§7" + (itemStack.hasItemMeta() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().toString()) + "§b]", "§a捡起物品", 0, 20, 5);
            }
        }
        if(!paramEntityPickupItemEvent.isCancelled()) Homelessness.core.getEventScheduler().callEvent(paramEntityPickupItemEvent);
    }
}
