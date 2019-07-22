/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Party;
import com.vagrantstudio.homelessness.api.Task;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelTask implements Task {

    protected static final File folder = new File("plugins/Homelessness/Task");
    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Task").replace("&", "§");
    protected static Map<String, Task> localTaskMap = new HashMap();
    protected static Map<UUID, TaskSnapshot> taskSnapshotMap = new ConcurrentHashMap();
    protected static Map<OfflinePlayer, List<String>> localCompletedTaskMap = new HashMap();
    protected static ItemStack cancelTask = new CraftItemStack(Material.BARRIER, "§c放弃任务").create();

    private String localString;
    private List<String> localRecommendation;
    private ConfigurationSection localTaskSection;
    private long localRefreshTime = 0;
    private Type localType;
    private List<String> localTaskRequirement;

    static {
        folder.mkdirs();
        for (File file : folder.listFiles()) {
            Task task = new PixelTask(file);
            localTaskMap.put(task.getName(), task);
        }
    }

    protected static void startTask(String paramString, Player paramPlayer) {
        if(PixelInstanceZone.forPlayer(paramPlayer) != null){
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Task.InZone"));
            return;
        }
        if (localTaskMap.containsKey(paramString)) {
            Party party = PixelParty.forPlayer(paramPlayer);
            if(party == null) startTask(localTaskMap.get(paramString), paramPlayer); else startTask(localTaskMap.get(paramString), party);
        }
    }
    
    protected static void stopTask(String paramString, Set<Player> paramPlayerSet){
        paramPlayerSet.stream().forEach((paramPlayer) -> {
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Task.BreakOff"));
            taskSnapshotMap.remove(paramPlayer.getUniqueId());
        });
    }
    
    protected static void endTask(String paramString, Set<Player> paramPlayerSet){
        paramPlayerSet.stream().forEach((paramPlayer) -> {
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Task.Complete"));
            taskSnapshotMap.remove(paramPlayer.getUniqueId());
            localCompletedTaskMap.get(paramPlayer).add(paramString);
        });
    }
    
    protected static void putSnapshot(Player paramPlayer, TaskSnapshot snapshot){
        taskSnapshotMap.put(paramPlayer.getUniqueId(), snapshot);
    }
    
    protected static void startTask(Task paramTask, Party paramParty){
        TaskSnapshot snapshot = new TaskSnapshot(paramTask, paramParty.getOnlines().toArray(new Player[]{}));
        paramParty.getOnlines().stream().forEach((paramPlayer) -> {
            putSnapshot(paramPlayer, snapshot);
        });
    }

    protected static void startTask(Task paramTask, Player paramPlayer) {
        if (PixelTask.taskSnapshotMap.containsKey(paramPlayer.getUniqueId())) {
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Task.AlreadyReciveOtherTask").replace("%name", PixelTask.taskSnapshotMap.get(paramPlayer.getUniqueId()).getName()));
            return;
        }
        putSnapshot(paramPlayer, new TaskSnapshot(paramTask, paramPlayer));
        paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Task.ReciveTask"));
    }
    
    public static void cancelTask(Set<Player> paramPlayerSet){
        paramPlayerSet.stream().forEach((paramPlayer) -> {
            taskSnapshotMap.remove(paramPlayer.getUniqueId());
        });
    }

    private PixelTask(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        localString = yaml.getString("Name");
        localRecommendation = yaml.getStringList("Recommendation");
        localType = Type.valueOf(yaml.getString("Type"));
        localTaskRequirement = yaml.contains("Requirement") ? yaml.getStringList("Requirement") : new ArrayList();
        localTaskSection = yaml.getConfigurationSection("Task");
    }

    @Override
    public void start(Player paramPlayer) {
        taskSnapshotMap.put(paramPlayer.getUniqueId(), new TaskSnapshot(this));
    }

    @Override
    public void start(Party paramParty) {
        TaskSnapshot snapshot = new TaskSnapshot(this, (Player[]) paramParty.getOnlines().toArray());
        paramParty.getOnlines().stream().forEach((paramPlayer) -> {
            taskSnapshotMap.put(paramPlayer.getUniqueId(), snapshot);
        });
    }

    @Override
    public List<String> getRequirement() {
        return localTaskRequirement;
    }

    @Override
    public void setName(String paramString) {
        localString = paramString;
    }

    @Override
    public String getName() {
        return localString;
    }

    @Override
    public List<String> getRecommendation() {
        return localRecommendation;
    }

    @Override
    public ItemStack icon() {
        return Homelessness.core.getReflection().set(new CraftItemStack(Material.BOOK, localString, localRecommendation).create(), "name", localString);
    }

    @Override
    public Type getType() {
        return localType;
    }

    @Override
    public ConfigurationSection getTaskSection() {
        return localTaskSection;
    }

    @Override
    public void save(File file) {
        try {
            file.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Name", localString);
            yaml.set("Recommendation", localRecommendation);
            yaml.set("Task", localTaskSection);
            yaml.set("Type", localType.toString());
        } catch (IOException ex) {

        }
    }

}
