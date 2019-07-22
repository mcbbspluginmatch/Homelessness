/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Party;
import com.vagrantstudio.homelessness.api.Risker.Combat;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.CraftEntry;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class PixelParty implements Party {

    protected static Map<String, Party> localPartyMap = new HashMap<>();
    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Party").replace("&", "§");
    protected static int maxPlayer = PixelConfiguration.option.getInt("Party.Max_Player");
    protected static final ItemStack invitePlayer = new CraftItemStack(Material.CAKE, "§a邀请玩家",
            new String[]{"§7邀请一名好友加入你的组队", "§7这个邀请在对方处于战斗模式下时会无效化"}).create();
    protected static final ItemStack convene = new CraftItemStack(Material.COMPASS, "§a召集成员", new String[]{"§7召集你组队中所有成员传送至你的位置",
        "§7如果你在战斗状态下，这个功能不可用", "§7这个功能也不会向处于战斗状态下的队员发送任何信息"}).create();
    protected static final ItemStack leave = new CraftItemStack(Material.BARRIER, "§c离开组队", new String[]{"§7离开这个组队", "§7如果你是队长，将会解散该组队"}).create();
    protected static final ItemStack broadcast = new CraftItemStack(Material.CAKE, "§a广播", new String[]{"§7广播一条消息给你的所有队员", "§7处在其它频道和战斗状态下的队员也会收到该消息"}).create();

    protected Map<UUID, Party.Grade> localPlayerMap = new HashMap();
    protected List<OfflinePlayer> localApplicationList = new ArrayList();
    protected OfflinePlayer localOfflinePlayer;
    protected String localString = "";
    protected final UUID localUniqueId = UUID.randomUUID();

    protected static Party forPlayer(OfflinePlayer paramOfflinePlayer) {
        for (Party paramParty : localPartyMap.values()) {
            if (paramParty.contains(paramOfflinePlayer)) {
                return paramParty;
            }
        }
        return null;
    }

    protected static Party forName(String paramString) {
        return localPartyMap.get(paramString);
    }
    
    protected static Party forUniqueId(UUID paramUniqueId){
        for(Party paramParty : localPartyMap.values()){
            if(paramParty.getUniqueId().equals(paramUniqueId)){
                return paramParty;
            }
        }
        return null;
    }

    protected static void create(String paramString, Player paramPlayer) {
        if (localPartyMap.containsKey(paramString)) {
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Party.SameName"));
        } else {
            localPartyMap.put(paramString, new PixelParty(paramString, paramPlayer));
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Party.CreateSuccess"));
        }
    }

    protected PixelParty(String paramString, Player paramPlayer) {
        localPlayerMap.put(paramPlayer.getUniqueId(), Grade.CAPTAIN);
        localString = paramString;
    }

    @Override
    public ItemStack icon() {
        List<String> lore = new ArrayList();
        lore.add("§7组队名 §6§l> §a" + localString);
        lore.add("§7组队内玩家 §6§l");
        localPlayerMap.keySet().stream().forEach((paramUniqueId) -> {
            OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(paramUniqueId);
            lore.add("§a" + offlineInstance.getName() + " §7(" + (offlineInstance.isOnline() ? "在线" : "不在线") + ")");
        });
        return Homelessness.core.getReflection().set(new CraftItemStack(Material.BANNER, "§a组队", lore).create(), "name", localString);
    }

    @Override
    public void join(Player paramPlayer) {
        if (forPlayer(paramPlayer) == null) {
            add(paramPlayer);
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Party.JoinSuccess"));
        } else {
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Party.AlreadyJoinedOtherParty"));
        }
    }

    @Override
    public void kick(OfflinePlayer paramOfflinePlayer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<OfflinePlayer, Grade> getMembers() {
        Map<OfflinePlayer, Grade> member = new HashMap();
        localPlayerMap.forEach((paramUniqueId, paramGrade) -> {
            member.put(Bukkit.getOfflinePlayer(paramUniqueId), paramGrade);
        });
        return member;
    }

    @Override
    public Set<OfflinePlayer> getPlayers() {
        Set<OfflinePlayer> offlineSet = new HashSet();
        localPlayerMap.keySet().stream().forEach((paramUniqueId) -> {
            offlineSet.add(Bukkit.getOfflinePlayer(paramUniqueId));
        });
        return offlineSet;
    }

    @Override
    public void add(OfflinePlayer paramOfflinePlayer) {
        localPlayerMap.put(paramOfflinePlayer.getUniqueId(), Grade.MEMBER);
        broadcast(PixelConfiguration.getLang("Message.Party.PlayerJoin").replace("%name", paramOfflinePlayer.getName()));
    }

    @Override
    public void remove(OfflinePlayer paramOfflinePlayer) {
        localPlayerMap.remove(paramOfflinePlayer.getUniqueId());
    }

    @Override
    public boolean contains(OfflinePlayer paramOfflinePlayer) {
        return localPlayerMap.containsKey(paramOfflinePlayer.getUniqueId());
    }

    @Override
    public Grade getGrade(OfflinePlayer paramOfflinePlayer) {
        return localPlayerMap.get(paramOfflinePlayer.getUniqueId());
    }

    @Override
    public List<Player> getOnlines() {
        List<Player> onlines = new ArrayList();
        localPlayerMap.keySet().stream().forEach((paramUniqueId) -> {
            OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(paramUniqueId);
            if (offlineInstance.isOnline()) {
                onlines.add(offlineInstance.getPlayer());
            }
        });
        return onlines;
    }

    @Override
    public void convene(Player paramSender) {
        List<Player> online = getOnlines();
        Party.Grade level = localPlayerMap.get(paramSender.getUniqueId());
        if (level == Party.Grade.CAPTAIN) {
            online.stream().filter((paramPlayer) -> (PixelRisker.get(paramPlayer).getCombat() != Combat.FIGHTING)).forEach((paramPlayer) -> {
                paramPlayer.teleport(paramSender);
            });
        } else if (level == Party.Grade.VICE_CAPTAIN) {
            online.stream().filter((paramPlayer) -> (PixelRisker.get(paramPlayer).getCombat() != Combat.FIGHTING)).forEach((paramPlayer) -> {
                paramPlayer.sendMessage(prefix + "§a组队中 §7" + paramSender.getName() + "§a正在发布集结令，打开抬头菜单选择 操作确认 以确认或取消");
                if (!ObjectSet.localInvActionMap.containsKey(paramPlayer)) {
                    ObjectSet.localInvActionMap.put(paramPlayer, new CraftEntry<>("组队-集结", paramSender.getUniqueId()));
                }
                PixelView.addView(paramPlayer, "§a操作确认", ObjectSet.viewConfirm);
            });
        } else {
            paramSender.sendMessage(prefix + PixelConfiguration.getLang("Message.Party.CaptainOnly"));
            return;
        }
        paramSender.sendMessage(prefix + PixelConfiguration.getLang("Message.Party.Convene"));
    }

    @Override
    public void convene(Player paramSender, Player paramPlayer) {
        Party.Grade level = localPlayerMap.get(paramSender.getUniqueId());
        if (!localPlayerMap.containsKey(paramPlayer.getUniqueId())) {
            return;
        }
        if (level == Party.Grade.CAPTAIN) {
            paramPlayer.teleport(paramSender);
        } else {
            paramPlayer.sendMessage(prefix + "§a组队中 §7" + paramSender.getName() + "§a请求你传送到他的位置，打开抬头菜单选择 操作确认 以确认或取消");
            if (!ObjectSet.localInvActionMap.containsKey(paramPlayer)) {
                ObjectSet.localInvActionMap.put(paramPlayer, new CraftEntry<>("组队-集结", paramSender.getUniqueId()));
            }
            PixelView.addView(paramPlayer, "§a操作确认", ObjectSet.viewConfirm);
        }
    }

    @Override
    public void setGrade(OfflinePlayer paramOfflinePlayer, Grade paramGrade) {
        localPlayerMap.put(paramOfflinePlayer.getUniqueId(), paramGrade);
    }

    @Override
    public void setLeader(OfflinePlayer paramOfflinePlayer) {
        localOfflinePlayer = paramOfflinePlayer;
    }

    @Override
    public OfflinePlayer getLeader() {
        return localOfflinePlayer;
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
    public void invite(Player paramPlayer) {
        if(forPlayer(paramPlayer) == null){
            if(ObjectSet.localInvActionMap.containsKey(paramPlayer)) ObjectSet.localInvActionMap.put(paramPlayer, new CraftEntry<>("组队邀请", localUniqueId));
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Party.Invite"));
        }
    }
    
    @Override
    public void broadcast(String paramString){
        getOnlines().stream().forEach((paramPlayer) -> {
            paramPlayer.sendMessage(prefix + paramString);
        });
    }

    @Override
    public View getView() {
        View view = new PixelView();
        view.setItem(1, invitePlayer);
        view.setItem(2, new CraftItemStack(Material.SKULL_ITEM, (short) 3, "§a查看组队内玩家列表", new String[]{"§7在此管理你的队员",
            "§7右键以踢出玩家", "§7一共有(" + localPlayerMap.size() + ")位玩家"}).create());
        view.setItem(7, convene);
        view.setItem(16, broadcast);
        view.setItem(19, leave);
        return view;
    }

    @Override
    public void updateView(Player paramPlayer) {
        PixelView.addView(paramPlayer, "§a组队", getView());
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId;
    }

}
