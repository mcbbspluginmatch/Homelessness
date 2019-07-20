/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author BergStudio
 */
public class PixelConfiguration {

    protected static final File localFile0 = new File("plugins/Homelessness");
    
    protected static YamlConfiguration option = YamlConfiguration.loadConfiguration(new File(localFile0, "config.yml"));
    protected static YamlConfiguration lang = YamlConfiguration.loadConfiguration(new File(localFile0, "lang.yml"));

    protected PixelConfiguration() {
        localFile0.mkdirs();
        Homelessness.core.saveResource("config.yml", false);
        Homelessness.core.saveResource("lang.yml", false);
        option = YamlConfiguration.loadConfiguration(new File(localFile0, "config.yml"));
        lang = YamlConfiguration.loadConfiguration(new File(localFile0, "lang.yml"));
        File horseSpawner = new File(localFile0, "HorseSpawner.yml");
        if(horseSpawner.exists()){
            Homelessness.core.setFunction("HorseSpawner", new FunctionHorseSpawner(YamlConfiguration.loadConfiguration(horseSpawner)));
        }
    }
    
    protected static String getLang(String path){
        return lang.getString(path).replace("&", "§");
    }

}
