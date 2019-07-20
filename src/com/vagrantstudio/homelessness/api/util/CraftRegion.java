/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;

/**
 *
 * @author BergStudio
 */
public class CraftRegion {
    
    private Map<Location, Block> placedBlock = new HashMap(); //代表被放置的方块集合
    private Map<Location, ItemStack[]> chestItems = new HashMap();
    private Map<Location, CraftData<ItemStack>> furnaceItems = new HashMap();
    private Map<Location, Material> breakedBlockMaterial = new HashMap();
    private Map<Location, BlockFace> breakedBlockFace = new HashMap();
    private Map<Location, Byte> breakedBlockData = new HashMap();
    private Map<Location, Biome> breakedBlockBiome = new HashMap();
    private Map<Location, Boolean> breakedBlockPower = new HashMap();
    private List<Location> breakedBlock = new ArrayList(); //代表被破坏的方块集合
    private List<Entity> removingEntities = new ArrayList();
    private Location min;
    private Location max;
    private World world;
    
    public CraftRegion(Location loc0, Location loc1) {
        if (loc0.getWorld() != loc1.getWorld()) {
            return;
        }
        min = new Location(world, Math.min(loc0.getX(), loc1.getX()), Math.min(loc0.getY(), loc1.getY()), Math.min(loc0.getZ(), loc1.getZ()));
        max = new Location(world, Math.max(loc0.getX(), loc1.getX()), Math.max(loc0.getY(), loc1.getY()), Math.max(loc0.getZ(), loc1.getZ()));
    }
    
    public void loadChunks() {
        int minX = (int) Math.floor(this.min.getX());
        int maxX = (int) Math.ceil(this.max.getX());
        int minZ = (int) Math.floor(this.min.getZ());
        int maxZ = (int) Math.ceil(this.max.getZ());
        for (int x = minX; x <= maxX; x += 16) {
            for (int z = minZ; z <= maxZ; z += 16) {
                Chunk chunk = this.world.getChunkAt(x, z);
                if (!chunk.isLoaded()) {
                    chunk.load();
                }
            }
        }
    }
    
    public void init() {
        Thread blockIterator = new Thread() {
            @Override
            public void run() {
                for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                    for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                            Block blockInstance = world.getBlockAt(x, y, z);
                            if (blockInstance.getType() == Material.CHEST) {
                                Chest chest = (Chest) blockInstance.getState();
                                chestItems.put(blockInstance.getLocation(), chest.getBlockInventory().getContents());
                            }
                            if (blockInstance.getType() == Material.FURNACE){
                                Furnace furnace = (Furnace) blockInstance.getState();
                                FurnaceInventory fInv = furnace.getInventory();
                                furnaceItems.put(blockInstance.getLocation(), new CraftData(3, fInv.getFuel(), fInv.getResult(), fInv.getSmelting()));
                            }
                        }
                    }
                }
            }
        };
        blockIterator.start();
    }

    /**
     * 加载区块 移除所有放置的方块 放置所有被破坏的方块并还原数据 移除所有被放置的实体 还原所有箱子物品
     */
    public void reset() {
        loadChunks();
        
        for (Block block : placedBlock.values()) {
            block.setType(Material.AIR);
        }
        placedBlock.clear();
        
        for (Location location : breakedBlock) {
            Block block = world.getBlockAt(location);
            block.setType(breakedBlockMaterial.get(location));
            block.setBiome(breakedBlockBiome.get(location));
            block.setData(breakedBlockData.get(location));
            MaterialData mdata = block.getState().getData();
            if (mdata instanceof Directional) {
                ((Directional) mdata).setFacingDirection(breakedBlockFace.get(location));
            }
            if (mdata instanceof Lever) {
                ((Lever) mdata).setPowered(breakedBlockPower.get(location));
            }
            block.getState().setData(mdata);
        }
        breakedBlockMaterial.clear();
        
        removingEntities.stream().forEach((paramEntity) -> {
            paramEntity.remove();
        });
        
        chestItems.forEach((paramLocation, paramItemArray) -> {
            Block block = world.getBlockAt(paramLocation);
            if(block.getState() instanceof Chest){
                Inventory inv = ((Chest)block.getState()).getBlockInventory();
                inv.clear();
                inv.addItem(paramItemArray);
            }
        });
        
        furnaceItems.forEach((paramLocation, paramData) -> {
            Block block = world.getBlockAt(paramLocation);
            if(block.getState() instanceof Furnace){
                FurnaceInventory furnace = ((Furnace)block.getState()).getInventory();
                furnace.clear();
                furnace.setFuel(paramData.get(0));
                furnace.setResult(paramData.get(1));
                furnace.setSmelting(paramData.get(2));
            }
        });
    }
    
    public void addBreakedBlock(Block block) {
        if (placedBlock.containsKey(block.getLocation())) {
            placedBlock.remove(block.getLocation());
            return;
        }
        Location loc = block.getLocation();
        breakedBlock.add(loc);
        breakedBlockFace.put(loc, block.getFace(block));
        breakedBlockData.put(loc, block.getData());
        breakedBlockBiome.put(loc, block.getBiome());
        breakedBlockPower.put(loc, block.isBlockPowered());
        breakedBlockMaterial.put(loc, block.getType());
    }
    
    public void addPlacedBlock(Block block) {
        placedBlock.put(block.getLocation(), block);
    }
    
    public void addEntity(Entity entity){
        removingEntities.add(entity);
    }
}
