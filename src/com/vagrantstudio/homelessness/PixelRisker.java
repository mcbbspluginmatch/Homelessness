/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.Experience;
import com.vagrantstudio.homelessness.api.Friends;
import com.vagrantstudio.homelessness.api.Guild;
import com.vagrantstudio.homelessness.api.Party;
import com.vagrantstudio.homelessness.api.Risker;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Retr0
 */
public class PixelRisker implements Risker {

    private Combat localCombat = Combat.STANDARD; //玩家战斗状态
    private Experience localExperience; //玩家经验槽
    private UUID localUniqueId; //玩家UUID
    private Friends localFriends;
    private Set<UUID> localOwnedArea = new HashSet();

    protected static final String prefix = PixelConfiguration.lang.getString("Message.Prefix.Risker").replace("&", "§");
    protected static final Map<UUID, Risker> localMap = new HashMap();
    protected static final File localFile = new File("plugins/Homelessness/Playerdata");

    static {
        localFile.mkdirs();
        for (File paramFile : localFile.listFiles()) {
            String name = paramFile.getName();
            if (name.endsWith(".yml")) {
                localMap.put(UUID.fromString(name.replace(".yml", "")), new PixelRisker(paramFile));
            }
        }
    }

    public static Risker get(OfflinePlayer paramPlayer) {
        return localMap.get(paramPlayer.getUniqueId());
    }

    protected PixelRisker(File paramFile) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(paramFile);
        
        localExperience = yaml.contains("Experience") ? new PixelExperience(yaml.getInt("Experience")) : new PixelExperience();
        localUniqueId = UUID.fromString(yaml.getString("UniqueId"));
        OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(localUniqueId);
        if(PixelBank.sync == PixelBank.Sync.LOCAL)
                PixelBank.localMap.put(localUniqueId, yaml.contains("Bank") ? new PixelBank(localUniqueId, yaml.getDouble("Bank")) : new PixelBank(localUniqueId));
        localFriends = new PixelFriends(yaml.getStringList("Friends"), localUniqueId);
        yaml.getStringList("Arenas").stream().forEach((paramString) -> {
            localOwnedArea.add(UUID.fromString(paramString));
        });
        PixelTask.localCompletedTaskMap.put(offlineInstance, yaml.contains("CompletedTask") ? yaml.getStringList("CompletedTask") : new ArrayList());
    }

    protected PixelRisker(Player paramPlayer) {
        localExperience = new PixelExperience();
        localUniqueId = paramPlayer.getUniqueId();
        localFriends = new PixelFriends(localUniqueId);
        if(PixelBank.sync == PixelBank.Sync.LOCAL)
                PixelBank.localMap.put(localUniqueId, new PixelBank(localUniqueId));
        PixelTask.localCompletedTaskMap.put(paramPlayer, new ArrayList());
    }

    @Override
    public Combat getCombat() {
        return localCombat;
    }

    @Override
    public void setCombat(Combat paramCombat) {
        localCombat = paramCombat == null ? localCombat : paramCombat;
    }

    @Override
    public Experience experience() {
        return localExperience;
    }

    @Override
    public Bank bank() {
        return PixelBank.localMap.get(localUniqueId);
    }

    @Override
    public ItemStack icon() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(localUniqueId);
        Guild guild = PixelGuild.forPlayer(offlinePlayer);
        return Homelessness.core.getReflection().set(new CraftItemStack(Material.SKULL_ITEM, (short) 3, "§a玩家信息", new String[]{
            "§a名称 §7>> " + offlinePlayer.getName(),
            "§a等级 §7>> §6" + localExperience.getLevel() + "★",
            "§a经验 §7>> §6" + localExperience.getExp(),
            "§a经济 §7>> " + bank().getBalance(),
            "§a状态 §7>> " + (offlinePlayer.isOnline() ? "§a在线 §7- " + localCombat.getDescribe() : "§a最后在线时间为 "
            + new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz").format(new Date(offlinePlayer.getLastPlayed()))),
            "§a公会 §7>> " + (guild == null ? "§f无" : guild.getName())}).create(), "uid", localUniqueId.toString());
    }

    @Override
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(localUniqueId);
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId;
    }

    @Override
    public boolean withdraw(double paramDouble) {
        if (bank().withdraw(paramDouble)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(localUniqueId);
            if (offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().sendMessage(PixelBank.prefix + "§f已经从账户中扣除 " + paramDouble + " 元");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deposit(double paramDouble) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(localUniqueId);
        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().sendMessage(PixelBank.prefix + "§f账户中已存入 " + paramDouble + " 元");
        }
        bank().deposit(paramDouble);
    }

    @Override
    public View getView() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(localUniqueId);
        Guild guild = PixelGuild.forPlayer(offlinePlayer);
        Party party = PixelParty.forPlayer(offlinePlayer);
        View view = new PixelView(localUniqueId);
        view.setItem(1, guild == null ? new CraftItemStack(Material.BARRIER, "§c没有公会").create() : guild.icon());
        view.setItem(2, offlinePlayer.isOnline() ? PixelChatChannel.forPlayer(offlinePlayer.getPlayer()).icon() : new CraftItemStack(Material.BARRIER, "§c玩家不在线").create());
        view.setItem(3, PixelBank.localMap.get(localUniqueId).icon());
        view.setItem(4, new CraftItemStack(Material.NAME_TAG, "§a好友", new String[]{"§7有 " + localFriends.all().size() + " 位好友在你的好友列表"}).create());
        view.setItem(5, party == null ? new CraftItemStack(Material.BARRIER, "§c不在组队中").create() : party.icon());
        return view;
    }

    @Override
    public void updateView(Player paramPlayer) {
        PixelView.addView(paramPlayer, "§a玩家信息", getView());
    }

    @Override
    public boolean isFriend(OfflinePlayer paramOfflinePlayer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Friends friends() {
        return localFriends;
    }

    @Override
    public void save(File file) {
        try {
            file.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            if(PixelBank.sync == PixelBank.Sync.LOCAL) yaml.set("Bank", bank().getBalance());
            yaml.set("Experience", localExperience.getExp());
            yaml.set("UniqueId", localUniqueId.toString());
            yaml.set("Friends", localFriends.toList());
            yaml.set("CompletedTask", PixelTask.localCompletedTaskMap.get(Bukkit.getOfflinePlayer(localUniqueId)));
            yaml.save(file);
        } catch (IOException ex) {
            Logger.getLogger(PixelRisker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Set<UUID> getOwnedArea() {
        return localOwnedArea;
    }
    
}
