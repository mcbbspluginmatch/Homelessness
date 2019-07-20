/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 *
 * @author BergStudio
 */
public class TriggerScheduler implements Listener {

    private Map<Class, Set<Trigger>> map = new HashMap();

    public void registerTrigger(Class clazz, Trigger trigger) {
        Set<Trigger> triggerSet = map.containsKey(clazz) ? map.get(clazz) : new HashSet();
        triggerSet.add(trigger);
        map.put(clazz, triggerSet);
    }

    public void unregisterTrigger(Trigger trigger) {
        map.values().stream().forEach((paramTriggerSet) -> {
            paramTriggerSet.remove(trigger);
        });
    }

    public void callEvent(Event event) {
        Class clazz = event.getClass();
        if (map.containsKey(clazz)) {
            for(Trigger trigger : map.get(clazz)){
                trigger.call(event);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onEnter(VehicleEnterEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onExit(VehicleExitEvent event) {
        callEvent(event);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        callEvent(event);
    }
}
