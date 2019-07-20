/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Area;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author BergStudio
 */
public class ListenerOfBlock implements Listener {

    protected static Map<Player, BlockBreakHandler> breakHandlers = new HashMap();
    protected Map<CraftItemStack, Integer> resourceRespawnTimeMap = new HashMap();
    protected Set<Location> locations = new HashSet();
    protected BukkitScheduler scheduler = Bukkit.getScheduler();
    protected boolean noBreak = false;
    protected boolean unlimitedResource = false;

    protected ListenerOfBlock() {
        ConfigurationSection blockSection = PixelConfiguration.option.getConfigurationSection("Block");
        noBreak = blockSection.getBoolean("DisableBreak");
        unlimitedResource = blockSection.getBoolean("UnlimitedResource");
        blockSection.getStringList("ResourceRespawnTime").stream().forEach((paramString) -> {
            String[] arrayString = paramString.split("="), arrayString0 = arrayString[0].split(":");
            resourceRespawnTimeMap.put(new CraftItemStack(Material.valueOf(arrayString0[0]), (short) (arrayString0.length == 2 ? Short.valueOf(arrayString0[1]) : 0)), Integer.valueOf(arrayString[1]) * 20);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent paramBlockBreakEvent) {
        Block block = paramBlockBreakEvent.getBlock();
        Player player = paramBlockBreakEvent.getPlayer();
        Area arena = PixelArea.forLocation(block.getLocation());
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        if (noBreak) {
            paramBlockBreakEvent.setCancelled(true);
        }
        if (unlimitedResource) {
            int time = anySimilar(new ItemStack(block.getType(), 1, block.getData()));
            if (time != -1) {
                Material blockMaterial = block.getType();
                byte blockData = block.getData();
                block.setType(Material.COBBLESTONE);
                block.setData((byte) 0x0);
                block.getWorld().dropItem(block.getLocation(), new ItemStack(blockMaterial, 1, blockData));
                scheduler.runTaskLater(Homelessness.core, () -> {
                    block.setType(blockMaterial);
                    block.setData(blockData);
                }, time);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent paramBlockPlaceEvent) {

    }

    protected int anySimilar(ItemStack paramItemStack) {
        for (CraftItemStack paramIItemStack : resourceRespawnTimeMap.keySet()) {
            if (paramIItemStack.isSimilar(paramItemStack)) {
                return resourceRespawnTimeMap.get(paramIItemStack);
            }
        }
        return -1;
    }

    protected static interface BlockBreakHandler {

        public abstract void handleEvent(BlockBreakEvent paramBlockBreakEvent);

    }

    protected class BlockBreakDefaultHandler implements BlockBreakHandler {

        @Override
        public void handleEvent(BlockBreakEvent paramBlockBreakEvent) {

        }

    }
}
