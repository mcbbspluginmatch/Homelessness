/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.boydti.fawe.bukkit.wrapper.AsyncWorld;
import com.vagrantstudio.homelessness.ListenerOfBlock.BlockBreakHandler;
import com.vagrantstudio.homelessness.api.Actuator;
import com.vagrantstudio.homelessness.api.Area;
import com.vagrantstudio.homelessness.api.InstanceZone;
import com.vagrantstudio.homelessness.api.Mission;
import com.vagrantstudio.homelessness.api.Party;
import com.vagrantstudio.homelessness.api.Risker.Combat;
import com.vagrantstudio.homelessness.api.Trigger;
import com.vagrantstudio.homelessness.api.util.CraftEntry;
import com.vagrantstudio.homelessness.api.util.CraftFile;
import com.vagrantstudio.homelessness.api.util.CraftRegion;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelInstanceZone implements InstanceZone {

    protected static Map<Entry<String, UUID>, InstanceZone> zoneMap = new ConcurrentHashMap();
    protected static Map<String, InstanceZone> instanceZoneModelMap = new HashMap();

    protected static final File localFile = new File("plugins/Homelessness/InstanceZone");

    protected String localString;
    protected ConfigurationSection localTaskSection;
    protected Area localArea;
    protected int localInteger = 1; //最大人数
    protected long localLong = 900;
    protected EnterMode localEnterMode = EnterMode.UNLIMITED;
    protected boolean localBoolean = true;

    static {
        localFile.mkdirs();
        for (File file : localFile.listFiles()) {
            instanceZoneModelMap.put(file.getName().replace(".yml", ""), new PixelInstanceZone(file));
        }
    }
    
    protected static InstanceZone forPlayer(Player paramPlayer){
        for(InstanceZone zone : zoneMap.values()){
            if(zone.getPlayers().contains(paramPlayer)) return zone;
        }
        return null;
    }

    protected static boolean createZone(String paramString, Area paramArea, EnterMode paramEnterMode) {
        if (paramArea == null) {
            return false;
        }
        if (instanceZoneModelMap.containsKey(paramString)) {
            return false;
        }
        instanceZoneModelMap.put(paramString, new PixelInstanceZone(paramArea, paramEnterMode, paramString));
        return true;
    }

    protected static void deleteZone(String paramString) {
        instanceZoneModelMap.remove(paramString);
    }

    protected static InstanceZone startNewZone(String paramString) {
        if (!instanceZoneModelMap.containsKey(paramString)) {
            return null;
        }
        InstanceZone zoneModel = instanceZoneModelMap.get(paramString);
        UUID randomUniqueId = UUID.randomUUID();
        InstanceZone zone = zoneModel.isMultiworld() ? new MultiZone(zoneModel, randomUniqueId) : new SingleZone(zoneModel, randomUniqueId);
        zoneMap.put(new CraftEntry<>(paramString, randomUniqueId), zone);
        return zone;
    }

    protected static void endZone(String paramString, UUID paramUniqueId) {
        zoneMap.remove(new CraftEntry<>(paramString, paramUniqueId));
    }

    protected static InstanceZone getZoneByPlayer(Player paramPlayer) {
        for (InstanceZone zone : zoneMap.values()) {
            if (zone.getPlayers().contains(paramPlayer)) {
                return zone;
            }
        }
        return null;
    }

    protected PixelInstanceZone(File paramFile) {
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(paramFile);
        localString = fileConfig.getString("Name");
        localArea = PixelArea.localAreaMap.get(UUID.fromString(fileConfig.getString("Area")));
        localInteger = fileConfig.getInt("MaxPlayer");
        localEnterMode = EnterMode.valueOf(fileConfig.getString("EnterMode"));
        localTaskSection = fileConfig.getConfigurationSection("Task");
    }

    protected PixelInstanceZone(Area paramArea, EnterMode paramEnterMode, String paramString) {
        localString = paramString;
        localArea = paramArea;
        localEnterMode = paramEnterMode;
    }

    protected PixelInstanceZone(Area paramArea, EnterMode paramEnterMode, String paramString, int paramInteger) {
        this(paramArea, paramEnterMode, paramString);
        localInteger = paramInteger >= 1 ? paramInteger : 1;
    }

    @Override
    public Area getArea() {
        return localArea;
    }

    @Override
    public void setArea(Area paramArena) {
        localArea = paramArena;
    }

    @Override
    public int getMaxPlayer() {
        return localInteger;
    }

    @Override
    public void setMaxPlayer(int paramInteger) {
        localInteger = paramInteger;
    }

    @Override
    public EnterMode getEnterMode() {
        return localEnterMode;
    }

    @Override
    public void setEnterMode(EnterMode paramEnterMode) {
        localEnterMode = paramEnterMode;
    }

    @Override
    public long getMaxTime() {
        return localLong;
    }

    @Override
    public void setMaxTime(long paramLong) {
        localLong = paramLong;
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
    public ItemStack icon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMultiworld() {
        return localBoolean;
    }

    @Override
    public void setMultiworld(boolean paramBoolean) {
        localBoolean = paramBoolean;
    }

    @Override
    public ConfigurationSection getTaskSection() {
        return localTaskSection;
    }

    @Override
    public void save(File file) {
        Bukkit.getConsoleSender().sendMessage(localArea == null ? "null" : localArea.toString());
        Bukkit.getConsoleSender().sendMessage(PixelArea.localAreaMap.toString());
        try {
            file.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Name", localString);
            yaml.set("Area", localArea.getUniqueId().toString());
            yaml.set("Task", localTaskSection);
            yaml.set("MaxPlayer", localInteger);
            yaml.set("EnterMode", localEnterMode.toString());
            yaml.set("Multiworld", localBoolean);
            yaml.save(file);
        } catch (IOException ex) {
            Logger.getLogger(PixelInstanceZone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void join(Player paramPlayer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void join(Party paramParty) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void leave(Player paramPlayer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void leaveAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void end() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addBreakedBlock(Block block) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addPlacedBlock(Block block) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UUID getUniqueId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Entry<Trigger, Set<Actuator>>> getTask() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void trigger(Trigger trigger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Player> getPlayers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public World getWorld() {
        return localArea.getWorld();
    }

    protected static class MultiZone extends PixelInstanceZone {

        protected List<Map.Entry<Trigger, Set<Actuator>>> localTask = new ArrayList();
        protected Map<Player, Location> from = new HashMap();
        protected int executedTask = 0;
        protected int taskId = -1, tempTaskId = -1;
        protected boolean started = false;
        protected Set<Player> players = new HashSet();
        protected Location tp;
        protected Location loc0;
        protected Location loc1;
        protected UUID uniqueId;
        protected Location copied;
        protected World world = null;

        /**
         *
         * @param zone 从这个副本中拷贝所有数据
         * @param paramUniqueId 代表了这个副本所存在的临时世界的UUID
         */
        public MultiZone(InstanceZone zone, UUID paramUniqueId) {
            super(zone.getArea(), zone.getEnterMode(), zone.getName());
            getArea().getWorld().save();
            copied = zone.getArea().getTeleportLocation();
            ConfigurationSection taskSection = zone.getTaskSection();
            if (taskSection != null) {
                taskSection.getValues(false).values().stream().filter((paramUnconfirmedObject) -> (paramUnconfirmedObject instanceof ConfigurationSection)).map((paramUnconfirmedObject) -> (ConfigurationSection) paramUnconfirmedObject).forEach((section) -> {
                    Trigger triggerInstance = null;
                    Set<Actuator> actuatorSet = new HashSet();
                    try {
                        String[] trigger = section.getString("Trigger").split(",");
                        Class triggerClazz = Class.forName("com.vagrantstudio.homelessness.Trigger" + trigger[0]);
                        triggerInstance = (Trigger) triggerClazz.getConstructor(String[].class, Mission.class).newInstance(trigger, this);
                        for (String paramString : section.getStringList("Actuator")) {
                            String[] actuator = paramString.split(",");
                            Class actuatorClazz = Class.forName("com.vagrantstudio.homelessness.Actuator" + actuator[0]);
                            actuatorSet.add((Actuator) actuatorClazz.getConstructor(String[].class, Mission.class).newInstance(actuator, this));
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(PixelInstanceZone.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (triggerInstance != null && !actuatorSet.isEmpty()) {
                        localTask.add(new CraftEntry<>(triggerInstance, actuatorSet));
                    }
                });
            }
            uniqueId = paramUniqueId;
            Location areaLoc0 = super.localArea.getLoc1();
            Location areaLoc1 = super.localArea.getLoc2();
            loc0 = new Location(world, areaLoc0.getX(), areaLoc0.getY(), areaLoc1.getZ());
            loc1 = new Location(world, areaLoc1.getX(), areaLoc1.getY(), areaLoc1.getZ());
        }

        @Override
        public UUID getUniqueId() {
            return uniqueId;
        }

        @Override
        public Set<Player> getPlayers() {
            return players;
        }

        @Override
        public void join(Player paramPlayer) {
            if (started) {
                return;
            }
            from.put(paramPlayer, paramPlayer.getLocation());
            players.add(paramPlayer);
            PixelRisker.get(paramPlayer).setCombat(Combat.FIGHTING);
        }

        @Override
        public void join(Party paramParty) {
            if (started) {
                return;
            }
            paramParty.getOnlines().stream().forEach((paramPlayer) -> {
                join(paramPlayer);
            });
        }

        @Override
        public void leave(Player paramPlayer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void leaveAll() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void start() {
            players.stream().forEach((paramPlayer) -> {
                Homelessness.core.sendTitle(paramPlayer, "§a请稍等", "§a副本加载中...", 5, 80, 5);
            });
            new Thread() {
                @Override
                public void run() {
                    File worldDir = copied.getWorld().getWorldFolder();
                    File to = new File(worldDir.getParent(), uniqueId.toString());
                    CraftFile.copyFolder(worldDir, to);
                    new File(to.getAbsolutePath(), "uid.dat").delete();
                    world = AsyncWorld.create(new WorldCreator(uniqueId.toString())).getBukkitWorld();
                    tp = new Location(world, copied.getX(), copied.getY(), copied.getZ());
                }
            }.start();
            if (localLong > 0) {
                taskId = Bukkit.getScheduler().runTaskLater(Homelessness.core, this::end, localLong * 20).getTaskId();
            }
            tempTaskId = Bukkit.getScheduler().runTaskTimer(Homelessness.core, () -> {
                if (world != null) {
                    players.stream().forEach((paramPlayer) -> {
                        Homelessness.core.sendTitle(paramPlayer, "", "§a副本加载完成", 5, 20, 5);
                        paramPlayer.teleport(tp);
                    });
                    if (!localTask.isEmpty()) {
                        Entry<Trigger, Set<Actuator>> entry = localTask.get(0);
                        if (entry.getKey() != null) {
                            entry.getKey().register();
                        } else {
                            entry.getValue().stream().forEach((paramActuator) -> {
                                paramActuator.execute();
                            });
                            localTask.get(1).getKey().register();
                        }
                    }
                    Bukkit.getScheduler().cancelTask(tempTaskId);
                }
            }, 60, 20).getTaskId();
        }

        @Override
        public void end() {
            from.forEach((paramPlayer, paramLocation) -> {
                paramPlayer.teleport(paramLocation);
            });
            Bukkit.unloadWorld(uniqueId.toString(), false);
            Bukkit.getWorlds().remove(getWorld());
            CraftFile.delFile(world.getWorldFolder());
            world.getWorldFolder().deleteOnExit();
            zoneMap.remove(new CraftEntry<>(getName(), uniqueId));
            Bukkit.getScheduler().cancelTask(taskId);
        }

        @Override
        public List<Map.Entry<Trigger, Set<Actuator>>> getTask() {
            return localTask;
        }

        @Override
        public void trigger(Trigger trigger) {
            for (int loop = 0; loop < localTask.size(); loop++) {
                Map.Entry<Trigger, Set<Actuator>> entry = localTask.get(loop);
                if (entry.getKey().equals(trigger)) {
                    entry.getValue().stream().forEach((paramActuator) -> {
                        paramActuator.execute();
                    });
                    trigger.unregister();
                    if (loop + 1 < localTask.size()) {
                        Trigger t = localTask.get(loop + 1).getKey();
                        new Thread() {
                            @Override
                            public void run() {
                                // 没有任何同步的t.register（见Trigger Scheduler）——CustomStuff
                                try {
                                    Thread.sleep(4000);
                                    t.register();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(PixelInstanceZone.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }.start();
                    } else {
                        // 意义何在？——
                        new Thread() {
                            @Override
                            public void run() {

                            }
                        }.start();
                    }
                    break;
                }
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MultiZone && obj.hashCode() == hashCode();
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 13 * hash + Objects.hashCode(this.uniqueId);
            return hash;
        }

        @Override
        public World getWorld() {
            return world;
        }

        @Override
        public void addBreakedBlock(Block block) {

        }

        @Override
        public void addPlacedBlock(Block block) {

        }

        @Override
        public void reset() {

        }
    }

    protected static class SingleZone extends PixelInstanceZone {

        protected List<Map.Entry<Trigger, Set<Actuator>>> localTask = new ArrayList();
        protected int executedTask = 0;
        protected boolean started = false;
        protected Set<Player> players = new HashSet();
        protected CraftRegion region;

        public SingleZone(InstanceZone zone, UUID paramUniqueId) {
            super(zone.getArea(), zone.getEnterMode(), zone.getName());
            region = new CraftRegion(super.localArea.getLoc1(), super.localArea.getLoc2());
        }

        @Override
        public void join(Player paramPlayer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void join(Party paramParty) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void leave(Player paramPlayer) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void leaveAll() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void start() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void end() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UUID getUniqueId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<Entry<Trigger, Set<Actuator>>> getTask() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void trigger(Trigger trigger) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Set<Player> getPlayers() {
            return players;
        }

        @Override
        public World getWorld() {
            return super.localArea.getWorld();
        }

        @Override
        public void addBreakedBlock(Block block) {
            region.addBreakedBlock(block);
        }

        @Override
        public void addPlacedBlock(Block block) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void reset() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    protected static class BlockBreakZoneHandler implements BlockBreakHandler {

        @Override
        public void handleEvent(BlockBreakEvent paramBlockBreakEvent) {
            Block block = paramBlockBreakEvent.getBlock();
            Player player = paramBlockBreakEvent.getPlayer();
            paramBlockBreakEvent.setCancelled(true);
            InstanceZone zone = PixelInstanceZone.getZoneByPlayer(player);
            zone.addBreakedBlock(block);
        }

    }
}
