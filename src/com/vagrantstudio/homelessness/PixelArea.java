/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Area;
import com.vagrantstudio.homelessness.api.Feudal;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.CraftChunkSnapshot;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import com.vagrantstudio.homelessness.api.util.IMap;
import com.vagrantstudio.homelessness.api.util.Numeric;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelArea implements Area {

    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Area").replace("&", "§");
    protected static Map<CraftChunkSnapshot, Set<UUID>> localChunkAsUniqueIdMap = new ConcurrentHashMap();
    protected static Map<UUID, Area> localAreaMap = new HashMap();
    protected static Map<Player, AreaEditor> arenaEditorMap = new HashMap(); //用于创建领地时选取目标
    protected static final File localFile = new File("plugins/Homelessness/Area");

    protected Location min; //代表了领地的顶点A
    protected Location max; //代表了领地的顶点B
    protected Location localTpLocation; //代表了在传送到该领地时会传送到哪个位置
    protected UUID localUniqueId; //代表了领地所有者的UUID
    protected UUID localUniqueId0; //代表了领地的UUID
    protected Feudal localFeudal; //代表了这个领地的所有者是一个玩家还是公会
    protected String localString; //代表了领地名
    protected OwnerType localOwnerType; //代表了这个领地的所有者形式
    protected boolean localBoolean = false; //当这个选项被设置为true时 视为领地可以破坏 但不会掉落物品 调用方法以将其复原
    protected World localWorld;
    
    static{
        localFile.mkdirs();
        for(File file : localFile.listFiles()){
            localAreaMap.put(UUID.fromString(file.getName().replace(".yml", "")), new PixelArea(file));
        }
    }

    protected static boolean create(Player paramPlayer) {
        if (!arenaEditorMap.containsKey(paramPlayer)) {
            paramPlayer.sendMessage(prefix + PixelConfiguration.lang.getString("Message.Area.Create.NoEditor").replace("&", "§"));
            return false;
        }
        AreaEditor editor = arenaEditorMap.get(paramPlayer);
        if (!editor.alreadyChoosePoint()) {
            paramPlayer.sendMessage(prefix + PixelConfiguration.lang.getString("Message.Area.Create.NeedTwoPoint").replace("&", "§"));
            return false;
        }
        if (isOverlay(editor.loc0, editor.loc1)) {
            paramPlayer.sendMessage(prefix + PixelConfiguration.lang.getString("Message.Area.Create.Overlay").replace("&", "§"));
            return false;
        }
        if (editor.owner == null && editor.type == OwnerType.OWNED) {
            paramPlayer.sendMessage(prefix + PixelConfiguration.lang.getString("Message.Area.Create.NotOwned").replace("&", "§"));
            return false;
        }
        UUID randomUid = UUID.randomUUID();
        localAreaMap.put(randomUid, new PixelArea(editor.loc0, editor.loc1, editor.name, editor.feudal, paramPlayer.getUniqueId(), editor.type, randomUid));
        paramPlayer.sendMessage(prefix + PixelConfiguration.lang.getString("Message.Area.Create.Success").replace("&", "§"));
        arenaEditorMap.remove(paramPlayer);
        Bukkit.getConsoleSender().sendMessage(localChunkAsUniqueIdMap.toString());
        return true;
    }
    
    protected static boolean remove(Location paramLocation){
        Area area = forLocation(paramLocation);
        if(area == null) return false;
        setAllChunk(area.getLoc1(), area.getLoc2(), area.getUniqueId(), false);
        if(area.getFeudal() != Feudal.NOT_AVAILABLE){
            if(area.getFeudal() == Feudal.GUILD){
                PixelGuild.forUniqueId(area.getOwnerId()).getOwnedArea().remove(area.getUniqueId());
            } else {
                PixelRisker.localMap.get(area.getOwnerId()).getOwnedArea().remove(area.getUniqueId());
            }
        }
        return true;
    }

    protected static Area forUniqueId(UUID paramUniqueId) {
        return localAreaMap.get(paramUniqueId);
    }

    protected static Area forLocation(Location paramLocation) {
        CraftChunkSnapshot chunk = CraftChunkSnapshot.getByChunk(paramLocation.getWorld().getChunkAt(paramLocation.getBlockX(), paramLocation.getBlockZ()));
        if (!IMap.containsKey(localChunkAsUniqueIdMap, chunk)) {
            return null;
        }
        for (UUID paramUniqueId : localChunkAsUniqueIdMap.get(chunk)) {
            Area area = localAreaMap.get(paramUniqueId);
            if (area.inArea(paramLocation)) {
                return area;
            }
        }
        return null;
    }
    
    protected static void setAllChunk(Location min, Location max, UUID uniqueId, boolean reg){
        World world = min.getWorld();
        int minX = (int) Math.floor(min.getX());
        int maxX = (int) Math.ceil(max.getX());
        int minZ = (int) Math.floor(min.getZ());
        int maxZ = (int) Math.ceil(max.getZ());
        for (int x = minX; x <= maxX; x += 16) {
            for (int z = minZ; z <= maxZ; z += 16) {
                CraftChunkSnapshot chunk = CraftChunkSnapshot.getByChunk(world.getChunkAt(x, z));
                Set<UUID> uidSet = localChunkAsUniqueIdMap.containsKey(chunk) ? localChunkAsUniqueIdMap.get(chunk) : new HashSet();
                uidSet.add(uniqueId);
                localChunkAsUniqueIdMap.put(chunk, uidSet);
            }
        }
        for (int sideX = minX; sideX <= maxX; sideX++) {
            CraftChunkSnapshot chunk = CraftChunkSnapshot.getByChunk(world.getChunkAt(sideX, maxZ));
            Set<UUID> uidSet = localChunkAsUniqueIdMap.containsKey(chunk) ? localChunkAsUniqueIdMap.get(chunk) : new HashSet();
            uidSet.add(uniqueId);
            localChunkAsUniqueIdMap.put(chunk, uidSet);
        }
        for (int sideZ = minZ; sideZ <= maxZ; sideZ++) {
            CraftChunkSnapshot chunk = CraftChunkSnapshot.getByChunk(world.getChunkAt(maxX, sideZ));
            Set<UUID> uidSet = localChunkAsUniqueIdMap.containsKey(chunk) ? localChunkAsUniqueIdMap.get(chunk) : new HashSet();
            if(reg) uidSet.add(uniqueId); else uidSet.remove(uniqueId);
            localChunkAsUniqueIdMap.put(chunk, uidSet);
        }
    }

    protected static boolean isOverlay(Location loc0, Location loc1) {
        int minX = Math.min(loc0.getBlockX(), loc1.getBlockX());
        int maxX = Math.max(loc0.getBlockX(), loc1.getBlockX());
        int minZ = Math.min(loc0.getBlockZ(), loc1.getBlockZ());
        int maxZ = Math.max(loc0.getBlockZ(), loc1.getBlockZ());
        for (int x = minX; x <= maxX; x += 16) {
            for (int z = minZ; z <= maxZ; z += 16) {
                CraftChunkSnapshot chunk = CraftChunkSnapshot.getByChunk(loc0.getWorld().getChunkAt(x, z));
                if (localChunkAsUniqueIdMap.containsKey(chunk)) {
                    if (localChunkAsUniqueIdMap.get(chunk).stream().anyMatch((uid) -> (localAreaMap.get(uid).corners().stream().anyMatch((location) -> (Numeric.insideArea(location, loc0, loc1)))))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected PixelArea(File paramFile) {
        FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(paramFile);
        localWorld = Bukkit.getWorld(UUID.fromString(fileConfig.getString("Location.World")));
        min = new Location(localWorld,
                fileConfig.getDouble("Location.A.x"), fileConfig.getDouble("Location.A.y"), fileConfig.getDouble("Location.A.z"));
        max = new Location(localWorld,
                fileConfig.getDouble("Location.B.x"), fileConfig.getDouble("Location.B.y"), fileConfig.getDouble("Location.B.z"));
        localTpLocation = new Location(Bukkit.getWorld(UUID.fromString(fileConfig.getString("Location.World"))),
                fileConfig.getDouble("Location.Teleport.x"), fileConfig.getDouble("Location.Teleport.y"), fileConfig.getDouble("Location.Teleport.z"));
        localUniqueId0 = UUID.fromString(paramFile.getName().replace(".yml", ""));
        localOwnerType = OwnerType.valueOf(fileConfig.getString("Owner.Type"));
        if (localOwnerType == OwnerType.OWNED) {
            localFeudal = "Player".equalsIgnoreCase(fileConfig.getString("Owner.Feudal")) ? Feudal.PLAYER : Feudal.GUILD;
            localUniqueId = UUID.fromString(fileConfig.getString("Owner.UniqueId"));
        } else {
            localFeudal = Feudal.NOT_AVAILABLE;
        }
        localString = fileConfig.getString("Name");
        setAllChunk(min, max, localUniqueId0, true);
    }

    protected PixelArea(Location paramLocation0, Location paramLocation1, String paramString, Feudal paramFeudal, UUID paramUniqueId, OwnerType paramOwnerType, UUID paramUniqueId0) {
        if (paramLocation0.getWorld() != paramLocation1.getWorld()) {
            return;
        }
        localWorld = paramLocation0.getWorld();
        min = new Location(localWorld, Math.min(paramLocation0.getX(), paramLocation1.getX()),
                Math.min(paramLocation0.getY(), paramLocation1.getY()), Math.min(paramLocation0.getZ(), paramLocation1.getZ()));
        max = new Location(localWorld, Math.max(paramLocation0.getX(), paramLocation1.getX()),
                Math.max(paramLocation0.getY(), paramLocation1.getY()), Math.max(paramLocation0.getZ(), paramLocation1.getZ()));
        localTpLocation = new Location(localWorld,
                ((paramLocation0.getX() + paramLocation1.getX()) / 2),
                ((paramLocation0.getY() + paramLocation1.getY()) / 2),
                ((paramLocation0.getZ() + paramLocation1.getZ()) / 2));
        localOwnerType = paramOwnerType;
        localFeudal = paramOwnerType == OwnerType.OWNED ? paramFeudal : Feudal.NOT_AVAILABLE;
        localUniqueId = paramOwnerType == OwnerType.OWNED ? paramUniqueId : null;
        localUniqueId0 = paramUniqueId0;
        localString = paramString;
        setAllChunk(min, max, paramUniqueId0, true);
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
    public Location getLoc1() {
        return min;
    }

    @Override
    public Location getLoc2() {
        return max;
    }

    @Override
    public void setPos1(Location paramLocation) {
        min = paramLocation;
    }

    @Override
    public void setPos2(Location paramLocation) {
        max = paramLocation;
    }

    @Override
    public Location getTeleportLocation() {
        return localTpLocation;
    }

    @Override
    public void setTeleportLocation(Location paramLocation) {
        localTpLocation = paramLocation;
    }

    @Override
    public Feudal getFeudal() {
        return localFeudal;
    }

    @Override
    public void setFeudal(Feudal paramFeudalLord) {
        localFeudal = paramFeudalLord;
    }

    @Override
    public boolean inArea(Location paramLocation) {
        return inArea(paramLocation.getBlockX(), paramLocation.getBlockY(), paramLocation.getBlockZ());
    }

    @Override
    public boolean inArea(int x, int y, int z) {
        return (min.getBlockX() <= x && max.getBlockX() >= x)
                && (min.getBlockY() <= y && max.getBlockY() >= y)
                && (min.getBlockZ() <= z && max.getBlockZ() >= z);
    }

    @Override
    public OwnerType getOwnerType() {
        return localOwnerType;
    }

    @Override
    public void setOwnerType(OwnerType paramOwnerType) {
        localOwnerType = paramOwnerType;
    }

    @Override
    public ItemStack icon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public View getView() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateView(Player paramPlayer) {
        PixelView.addView(paramPlayer, "§a领域信息", getView());
    }

    @Override
    public boolean isPlayerOwned() {
        return localOwnerType == OwnerType.OWNED && localFeudal == Feudal.PLAYER;
    }

    @Override
    public boolean isGuildOwned() {
        return localOwnerType == OwnerType.OWNED && localFeudal == Feudal.GUILD;
    }

    @Override
    public Set<Location> corners() {
        Set<Location> locationSet = new HashSet();
        locationSet.add(min);
        locationSet.add(new Location(localWorld, min.getBlockX(), min.getBlockY(), max.getBlockZ()));
        locationSet.add(new Location(localWorld, min.getBlockX(), max.getBlockY(), max.getBlockZ()));
        locationSet.add(new Location(localWorld, min.getBlockX(), max.getBlockY(), min.getBlockZ()));
        locationSet.add(new Location(localWorld, max.getBlockX(), min.getBlockY(), min.getBlockZ()));
        locationSet.add(new Location(localWorld, max.getBlockX(), min.getBlockY(), max.getBlockZ()));
        locationSet.add(new Location(localWorld, max.getBlockX(), max.getBlockY(), min.getBlockZ()));
        locationSet.add(max);
        return locationSet;
    }

    @Override
    public int getLengthX() {
        return max.getBlockX() - min.getBlockX();
    }

    @Override
    public int getLengthY() {
        return max.getBlockY() - min.getBlockY();
    }

    @Override
    public int getLengthZ() {
        return max.getBlockZ() - min.getBlockZ();
    }

    @Override
    public String toString() {
        return "PixelArea{Max[x=" + max.getBlockX() + ",y=" + max.getBlockY() + ",z=" + max.getBlockZ() + "],Min[x=" + min.getBlockX() + ",y=" + min.getBlockY() + ",z=" + min.getBlockZ() + "],Name=" + localString + "}";
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId0;
    }

    @Override
    public void save(File file) {
        try {
            file.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Location.A", Numeric.locationToSection(min));
            yaml.set("Location.B", Numeric.locationToSection(max));
            yaml.set("Location.World", localWorld.getUID().toString());
            yaml.set("Location.Teleport", Numeric.locationToSection(localTpLocation));
            yaml.set("Owner.Type", localOwnerType.toString());
            yaml.set("Owner.Feudal", localFeudal.toString());
            if(localUniqueId != null){
                yaml.set("Owner.UniqueId", localUniqueId.toString());
            }
            yaml.set("UniqueIdentify", localUniqueId0.toString());
            yaml.set("Name", localString);
            yaml.save(file);
        } catch (IOException ex) {
            Logger.getLogger(PixelGuild.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public World getWorld() {
        return localWorld;
    }

    @Override
    public UUID getOwnerId() {
        return localUniqueId;
    }

    @Override
    public void setOwnerId(UUID paramUniqueId) {
        localUniqueId = paramUniqueId;
    }

    protected static class AreaEditor implements View.Viewable {

        private static ItemStack CREATE = new CraftItemStack(Material.WOOL, (short) 5, "§a创建领域").create();
        private static ItemStack CANCEL = new CraftItemStack(Material.WOOL, (short) 14, "§c放弃创建").create();
        private static ItemStack OWNER = new CraftItemStack(Material.CHEST, "§a为领地选择一名所有者").create();

        private Location loc0 = null;
        private Location loc1 = null;
        private String name = "§a新建领域";
        private Feudal feudal = Feudal.PLAYER;
        private OwnerType type = OwnerType.NOT_OWNED;
        private UUID owner;

        protected void setFirstLocation(Location location) {
            loc0 = location;
            if (loc0 != null && loc1 != null) {
                updateLocation();
            }
        }

        protected void setSecondLocation(Location location) {
            loc1 = location;
            if (loc0 != null && loc1 != null) {
                updateLocation();
            }
        }

        private void updateLocation() {
            Location min = new Location(loc0.getWorld(), Math.min(loc0.getX(), loc1.getX()), Math.min(loc0.getY(), loc1.getY()), Math.min(loc0.getZ(), loc1.getZ()));
            Location max = new Location(loc0.getWorld(), Math.max(loc0.getX(), loc1.getX()), Math.max(loc0.getY(), loc1.getY()), Math.max(loc0.getZ(), loc1.getZ()));
            loc0 = min;
            loc1 = max;
        }

        protected void setName(String string) {
            name = string;
        }

        protected void setFeudal(Feudal f) {
            feudal = f;
        }

        protected void setOwnerType(OwnerType ownerType) {
            type = ownerType;
        }

        protected Feudal getFeudal() {
            return feudal;
        }

        protected OwnerType getOwnerType() {
            return type;
        }

        protected boolean alreadyChoosePoint() {
            return loc0 != null && loc1 != null;
        }

        protected void setOwner(UUID uid) {
            owner = uid;
        }

        @Override
        public View getView() {
            View view = new PixelView();
            view.setItem(2, new CraftItemStack(Material.WOOL, (short) 5, "§a第一个点", loc0 == null ? new String[]{"§c未选取"}
                    : new String[]{"§7X=" + loc0.getBlockX(), "§7Y=" + loc0.getBlockY(), "§7Z=" + loc0.getBlockZ()}).create());
            view.setItem(3, new CraftItemStack(Material.WOOL, (short) 5, "§a第二个点", loc1 == null ? new String[]{"§c未选取"}
                    : new String[]{"§7X=" + loc1.getBlockX(), "§7Y=" + loc1.getBlockY(), "§7Z=" + loc1.getBlockZ()}).create());
            view.setItem(4, new CraftItemStack(Material.WOOL, (short) 1, "§a领地名", new String[]{name}).create());
            view.setItem(5, new CraftItemStack(Material.WOOL, (short) 6, "§a领地类型", new String[]{type.getName()}).create());
            view.setItem(6, type == OwnerType.OWNED ? new CraftItemStack(Material.WOOL, (short) 14, "§a所有者类型", new String[]{feudal.getName()}).create()
                    : new CraftItemStack(Material.BEDROCK, "§c无法设置领地所有者类型").create());
            if (type == OwnerType.OWNED) {
                view.setItem(15, owner == null ? OWNER : (feudal == Feudal.GUILD ? PixelGuild.localGuildMap.get(owner).icon() : PixelRisker.localMap.get(owner).icon()));
            }
            view.setItem(29, CREATE);
            view.setItem(33, CANCEL);
            return view;
        }

        @Override
        public void updateView(Player paramPlayer) {
            PixelView.addView(paramPlayer, "§a创建领域", getView());
        }
    }
}
