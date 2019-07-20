/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Function;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 *
 * @author BergStudio
 */
public class FunctionHorseSpawner implements Function {

    private Map<Location, Entity> spawners = new ConcurrentHashMap();
    private long respawnTime = 0;

    public FunctionHorseSpawner(ConfigurationSection paramConfigurationSection) {
        respawnTime = paramConfigurationSection.getLong("RespawnTime");
        if(paramConfigurationSection.contains("Spawner")) paramConfigurationSection.getStringList("Spawner").stream().forEach((paramString) -> {
            String[] array = paramString.split(",");
            World world = Bukkit.getWorld(array[0]);
            Location location = new Location(world, Double.valueOf(array[1]), Double.valueOf(array[2]), Double.valueOf(array[3]));
            Entity horse = world.spawnEntity(location, EntityType.HORSE);
            spawners.put(location, horse);
            Bukkit.getScheduler().runTaskTimerAsynchronously(Homelessness.core, () -> {
                if (!horse.isDead() && !world.getNearbyEntities(location, 10, 10, 10).contains(horse)) {
                    spawners.replace(location, world.spawnEntity(location, EntityType.HORSE));
                }
            }, 0, 6000);
        });
    }

}
