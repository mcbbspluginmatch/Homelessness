/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.google.common.collect.Sets;
import com.vagrantstudio.homelessness.api.Actuator;
import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Task;
import com.vagrantstudio.homelessness.api.Trigger;
import com.vagrantstudio.homelessness.api.util.CraftEntry;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author BergStudio
 */
public class TaskSnapshot implements Mission {

    private List<Entry<Trigger, Set<Actuator>>> taskList = new ArrayList();
    private String name;
    private List<String> recommendation;
    private ConfigurationSection section;
    private Set<Player> playerSet = new HashSet();
    private Task.Type type;
    private String tip;

    public TaskSnapshot(Task task, Player... player) {
        type = task.getType();
        playerSet = Sets.newHashSet(player);
        name = task.getName();
        recommendation = task.getRecommendation();
        ConfigurationSection taskSection = task.getTaskSection();
        if (taskSection != null) {
            taskSection.getValues(false).values().stream().filter((paramUnconfirmedObject) -> (paramUnconfirmedObject instanceof ConfigurationSection)).map((paramUnconfirmedObject) -> (ConfigurationSection) paramUnconfirmedObject).forEach((paramSection) -> {
                Trigger triggerInstance = null;
                Set<Actuator> actuatorSet = new HashSet();
                try {
                    if (paramSection.contains("Trigger")) {
                        String[] trigger = paramSection.getString("Trigger").split(",");
                        Class triggerClazz = Class.forName("com.vagrantstudio.homelessness.Trigger" + trigger[0]);
                        triggerInstance = (Trigger) triggerClazz.getConstructor(String[].class, Mission.class).newInstance(trigger, this);
                    }
                    if (paramSection.contains("Actuator")) {
                        for (String paramString : paramSection.getStringList("Actuator")) {
                            String[] actuator = paramString.split(",");
                            Class actuatorClazz = Class.forName("com.vagrantstudio.homelessness.Actuator" + actuator[0]);
                            actuatorSet.add((Actuator) actuatorClazz.getConstructor(String[].class, Mission.class).newInstance(actuator, this));
                        }
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(PixelInstanceZone.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (triggerInstance != null || !actuatorSet.isEmpty()) {
                    taskList.add(new CraftEntry<>(triggerInstance, actuatorSet));
                }
            });
            Entry<Trigger, Set<Actuator>> entry = taskList.get(0);
            if (entry.getKey() != null) {
                entry.getKey().register();
            } else {
                entry.getValue().stream().forEach((paramActuator) -> {
                    paramActuator.execute();
                });
                if(taskList.size() >= 2) taskList.get(1).getKey().register();
            }
        }
    }

    public List<String> getRecommendation() {
        return recommendation;
    }

    public String getName() {
        return name;
    }

    @Override
    public List<Entry<Trigger, Set<Actuator>>> getTask() {
        return taskList;
    }

    @Override
    public ConfigurationSection getTaskSection() {
        return section;
    }

    @Override
    public void trigger(Trigger trigger) {
        for (int loop = 0; loop < taskList.size(); loop++) {
            Map.Entry<Trigger, Set<Actuator>> entry = taskList.get(loop);
            if (entry.getKey().equals(trigger)) {
                entry.getValue().stream().forEach((paramActuator) -> {
                    paramActuator.execute();
                });
                entry.getKey().unregister();
                if (loop + 1 < taskList.size()) {
                    taskList.get(loop + 1).getKey().register();
                } else {
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public Set<Player> getPlayers() {
        return playerSet;
    }

    @Override
    public World getWorld() {
        for (Player player : playerSet) {
            return player.getWorld();
        }
        return null;
    }

    public void finish() {
        new Thread() {
            @Override
            public void run() {
                PixelTask.endTask(name, playerSet);
            }
        }.start();
    }

    public void stop() {

    }

    public Task.Type getType() {
        return type;
    }

    public String getTip() {
        return tip;
    }

    @Override
    public void updateTip(String tip) {
        this.tip = tip;
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("stats", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(PixelConfiguration.getLang("Scoreboard.Task"));
        Score taskName = obj.getScore("§f当前目标 §e§l> §a" + tip);
        Score holder = obj.getScore("   ");
        Score taskType = obj.getScore("§f任务类型 §e§l> §a" + type.getName());
        taskName.setScore(15);
        holder.setScore(14);
        taskType.setScore(13);
        playerSet.stream().forEach((paramPlayer) -> {
            paramPlayer.setScoreboard(board);
        });
    }

}
