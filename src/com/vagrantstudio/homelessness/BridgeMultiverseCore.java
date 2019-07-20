/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.World;
import org.bukkit.WorldType;

/**
 *
 * @author BergStudio
 */
public class BridgeMultiverseCore {
    private MVWorldManager worldManager;
    
    public void addWorld(String paramString1, World.Environment paramEnvironment, String paramString2, WorldType paramWorldType, Boolean paramBoolean, String paramString3){
        worldManager.addWorld(paramString3, paramEnvironment, paramString1, paramWorldType, paramBoolean, paramString2);
    }
}
