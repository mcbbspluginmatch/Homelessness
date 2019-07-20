/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.util;

import java.util.UUID;
import org.bukkit.Chunk;

/**
 *
 * @author BergStudio
 */
public class CraftChunkSnapshot {
    private int x;
    private int z;
    private UUID uid;
    
    public static CraftChunkSnapshot getByChunk(Chunk chunk){
        return new CraftChunkSnapshot(chunk.getWorld().getUID(), Numeric.toChunkLoc(chunk.getX()), Numeric.toChunkLoc(chunk.getZ()));
    }
    
    public CraftChunkSnapshot(UUID uid, int x, int z){
        this.uid = uid;
        this.x = x;
        this.z = z;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof CraftChunkSnapshot){
            CraftChunkSnapshot compare = (CraftChunkSnapshot)obj;
            return compare.x == x && compare.z == z && compare.uid == uid;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.x;
        hash = 83 * hash + this.z;
        return hash;
    }
    
    @Override
    public String toString(){
        return "IChunkSnapshot{world=" + uid.toString() + ",x=" + x + ",z=" + z + "}";
    }
}
