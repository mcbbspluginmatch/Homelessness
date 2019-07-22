/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Area;
import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.Challenge;
import com.vagrantstudio.homelessness.api.ChatChannel;
import com.vagrantstudio.homelessness.api.Core;
import com.vagrantstudio.homelessness.api.Experience;
import com.vagrantstudio.homelessness.api.Function;
import com.vagrantstudio.homelessness.api.Guild;
import com.vagrantstudio.homelessness.api.InstanceZone;
import com.vagrantstudio.homelessness.api.Risker;
import com.vagrantstudio.homelessness.api.TriggerScheduler;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.CraftFile;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import com.vagrantstudio.homelessness.reflect.Reflection;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.spigotmc.AsyncCatcher;

/**
 *
 * @author Retr0
 */
public class Homelessness extends Core {

    protected static BridgeVault hookVault = null;
    protected static BridgePlayerPoints hookPlayerPoints = null;
    protected static BridgeMythicMobs hookMythicMobs = null;
    protected static BridgeProtocol hookProtocol = null;
    protected static BridgePlaceholder hookPlaceholderAPI = null;
    protected static BridgeMultiverseCore hookMultiverse = null;
    protected static PixelConfiguration localStaticConfig;
    protected static Map<String, Function> localFunctionMap = new HashMap();
    protected Reflection nmsReflection;
    protected TriggerScheduler eventScheduler = new TriggerScheduler();
    protected ItemStack NO_MENU = new CraftItemStack(Material.WOOL, "§c菜单占位符").create();

    public static Core core;

    @Override
    public void onEnable() {
        AsyncCatcher.enabled = false;
        core = this;
        getCommand("homelessness").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        try { //建立反射
            nmsReflection = (Reflection) Class.forName("com.vagrantstudio.homelessness.reflect.version."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Homelessness.class.getName()).log(Level.SEVERE, null, ex);
        }
        Plugin vault = getServer().getPluginManager().getPlugin("Vault"), playerpoints = getServer().getPluginManager().getPlugin("PlayerPoints"),
                mythicmobs = getServer().getPluginManager().getPlugin("MythicMobs"), protocol = getServer().getPluginManager().getPlugin("ProtocolLib"),
                placeholder = getServer().getPluginManager().getPlugin("PlaceholderAPI"), multiverse = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (vault != null) {
            hookVault = new BridgeVault(getServer());
            getServer().getPluginManager().registerEvents(hookVault, this);
            getLogger().info("Homelessness > Vault Hooked!");
        } else getLogger().info("Homelessness > Plugin [Vault] not found");//建立插件连接
        if (playerpoints != null) {
            hookPlayerPoints = new BridgePlayerPoints();
            getLogger().info("Homelessness > PlayerPoints Hooked!");
        }
        if (mythicmobs != null) {
            hookMythicMobs = new BridgeMythicMobs();
            getServer().getPluginManager().registerEvents(hookMythicMobs, this);
            getLogger().info("Homelessness > MythicMobs Hooked!");
        }
        if (protocol != null) {
            hookProtocol = new BridgeProtocol();
            getLogger().info("Homelessness > ProtocolLib Hooked!");
        }
        if (placeholder != null) {
            getLogger().info("Homelessness > Something unexpected will happen if we hook PlaceholderAPI. We will fix it in next version!");
        }
        if (multiverse != null) {
            hookMultiverse = new BridgeMultiverseCore();
            getLogger().info("Homelessness > MultiverseCore Hooked!");
        }
        localStaticConfig = new PixelConfiguration(); //初始化配置文件
        getServer().getPluginManager().registerEvents(new ListenerOfInventory(), this); //初始化监听器
        getServer().getPluginManager().registerEvents(new ListenerOfBlock(), this);
        getServer().getPluginManager().registerEvents(new ListenerOfPlayer(), this);
        getServer().getPluginManager().registerEvents(new ListenerOfEntity(), this);
        getServer().getPluginManager().registerEvents(eventScheduler, this);
        
    }

    @Override
    public void onDisable() {
        for (File paramFile : PixelGuild.localFile.listFiles()) {
            paramFile.delete();
        }
        for (File paramFile : PixelRisker.localFile.listFiles()) {
            paramFile.delete();
        }
        for (File paramFile : PixelArea.localFile.listFiles()){
            paramFile.delete();
        }
        for (File paramFile : PixelInstanceZone.localFile.listFiles()){
            paramFile.delete();
        }
        PixelRisker.localMap.forEach((paramUniqueId, paramRisker) -> {
            paramRisker.save(new File(PixelRisker.localFile, paramUniqueId.toString() + ".yml"));
        });
        PixelGuild.localGuildMap.forEach((paramUniqueId, paramGuild) -> {
            paramGuild.save(new File(PixelGuild.localFile, paramUniqueId.toString() + ".yml"));
        });
        PixelArea.localAreaMap.forEach((paramUniqueId, paramArea) -> {
            paramArea.save(new File(PixelArea.localFile, paramUniqueId.toString() + ".yml"));
        });
        PixelInstanceZone.zoneMap.values().stream().forEach(InstanceZone::end);
        PixelInstanceZone.instanceZoneModelMap.forEach((paramString, paramInstanceZone) -> {
            paramInstanceZone.save(new File(PixelInstanceZone.localFile, paramString + ".yml"));
        });
    }

    @Override
    public boolean onCommand(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramStringArray) {
        if (paramStringArray.length == 0) {
            paramCommandSender.sendMessage(PixelConfiguration.lang.getStringList("CommandHelp").toArray(new String[]{}));
        } else {
            Player player = (Player) paramCommandSender;
            switch (paramStringArray[0].toLowerCase()) {
                case "removearea":
                    if(paramStringArray.length != 2) return false;
                    PixelArea.remove(player.getLocation());
                    break;
                case "createzone":
                    if(paramStringArray.length != 2) return false;
                    PixelInstanceZone.createZone(paramStringArray[1], PixelArea.forLocation(player.getLocation()), InstanceZone.EnterMode.SINGLE_PLAYER);
                    break;
            }
        }
        return true;
    }

    @Override
    public TriggerScheduler getEventScheduler() {
        return eventScheduler;
    }

    @Override
    public Guild getGuildByPlayer(OfflinePlayer offline) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Guild getGuildByName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Guild getGuildByUniqueId(UUID uid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Area getAreaByLocation(Location location) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Area getAreaByUniqueId(UUID uid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Risker asRisker(OfflinePlayer offline) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ChatChannel getChatChannelByPlayer(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ChatChannel getChatChannelByUniqueId(UUID uid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Area createArea(Location location0, Location location1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Area createArea(Location location0, Location location1, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Area createArea(Location location0, Location location1, String name, Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Area createArea(Location location0, Location location1, String name, Guild guild) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bank getBankByPlayer(OfflinePlayer offline) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bank getBankByUniqueId(UUID uid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Experience getExperience(OfflinePlayer offline) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reflection getReflection() {
        return nmsReflection;
    }

    @Override
    public void deleteGuild(UUID uid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteGuild(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getStructure(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void upgrade(OfflinePlayer offline) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEconomy(OfflinePlayer offline, double amount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void completeChallenge(OfflinePlayer offline, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void completeChallenge(OfflinePlayer offline, Challenge challenge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        if (hookProtocol != null) {
            hookProtocol.sendTitle(player, fadeIn, stay, fadeOut, title, subTitle);
        } else {
            player.sendTitle(title, subTitle);
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle) {
        sendTitle(player, title, subTitle, 5, 20, 5);
    }

    @Override
    public void openView(Player player, String viewName) {
        player.closeInventory();
        Inventory inventoryView = Bukkit.createInventory(null, 54, ObjectSet.inventoryTitle);
        Map<String, View> map = PixelView.localMenuMap.get(player);
        map.forEach((paramString0, paramView) -> {
            ItemStack item = new CraftItemStack(Material.WOOL, (short) 5, paramString0).create();
            if (paramString0.equals(viewName)) {
                ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(Material.WOOL);
                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
            if (paramView.getUniqueId() != null) {
                item = nmsReflection.set(nmsReflection.set(item, "uid", paramView.getUniqueId().toString()), "page", paramView.getPageNumber());
            }
            inventoryView.addItem(item);
        });
        for (int i = 0; i < 9; i++) {
            if (inventoryView.getItem(i) == null) {
                inventoryView.setItem(i, NO_MENU);
            }
        }
        for (int i = 9; i < 18; i++) {
            inventoryView.setItem(i, ObjectSet.itemStackHolder);
        }
        if (viewName != null) {
            View view = map.get(viewName);
            view.getItems().forEach((paramInteger, paramItemStack) -> {
                inventoryView.setItem(paramInteger + 18, paramItemStack);
            });
        }
        player.openInventory(inventoryView);
    }

    @Override
    public void sendBar(Player player, String message) {
        if (hookProtocol != null) {
            hookProtocol.sendBar(player, message);
        }
    }

    @Override
    public World copyWorld(String from, String to) {
            File fromDirectory = Bukkit.getWorld(from).getWorldFolder();
            File f = new File(Bukkit.getWorldContainer().getAbsolutePath(), to);
            if (f.exists()) {
                f.delete();
            }
            CraftFile.copyFolder(fromDirectory, f);
            new File(f.getAbsolutePath(), "uid.dat").delete();
            hookMultiverse.addWorld(to, World.Environment.NORMAL, null, null, null, "VoidWorld");
            return Bukkit.getWorld(to);
    }

    @Override
    public World copyWorld(World world, String newName) {
        File worldDir = world.getWorldFolder();
        File to = new File(worldDir.getParent(), newName);
        CraftFile.copyFolder(worldDir, to);
        new File(to.getAbsolutePath(), "uid.dat").delete();

        WorldCreator creator = new WorldCreator(newName);
        return creator.createWorld();
    }

    @Override
    public void spawnParticle(Location location, int amount, Particle... particle) {
        for(int randomAccess = 0, loop = 0; loop < amount; loop++, randomAccess = (int) (Math.random() * (particle.length - 1))){
            location.getWorld().spawnParticle(particle[randomAccess], location, 1);
        }
    }

    @Override
    public void spawnParticle(Location location, int amount, double offsetX, double offsetY, double offsetZ, Particle... particle) {
        for(int randomAccess = 0, loop = 0; loop < amount; loop++, randomAccess = (int) (Math.random() * (particle.length - 1))){
            location.getWorld().spawnParticle(particle[randomAccess], location, 1, offsetX, offsetY, offsetZ);
        }
    }

    @Override
    public void spawnParticle(Location location, Particle... particle) {
        spawnParticle(location, 1, particle);
    }

    @Override
    public void setFunction(String name, Function function) {
        localFunctionMap.put(name, function);
    }

}
