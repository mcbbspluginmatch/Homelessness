/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Access;
import com.vagrantstudio.homelessness.api.ChatChannel;
import com.vagrantstudio.homelessness.api.Risker;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Retr0
 */
public class PixelChatChannel implements ChatChannel {

    protected List<Player> localPlayerList = new ArrayList(); //player list
    protected Player localPlayer;
    protected boolean localBoolean = false; //use password
    protected String localString; //password
    protected String localString0; //name
    protected Access localAccess = Access.PUBLIC;
    protected ItemStack localItemStack;
    protected UUID localUniqueId = UUID.randomUUID();

    protected static ItemStack create = new CraftItemStack(Material.WOOL, (short) 5, "§a创建新的频道", new String[]{"§7这会使你退出当前聊天频道", "§7如果你是频道所有者将会解散该频道"}).create();
    protected static ItemStack list = new CraftItemStack(Material.WOOL, (short) 1, "§a查看聊天频道列表").create();
    protected static ItemStack dissolve = new CraftItemStack(Material.WOOL, (short) 14, "§c解散当前频道", new String[]{"§7这会使包括你在内的玩家全部退回默认频道"}).create();

    protected static Map<UUID, ChatChannel> localChatChannelMap = new HashMap<>();
    protected static ChatChannel localChatChannel = new PixelChatChannel();
    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Chat").replace("&", "§");

    public static void move(Player paramPlayer, ChatChannel paramChatChannel) {
        ChatChannel cc = forPlayer(paramPlayer);
        if (cc == paramChatChannel) {
            paramPlayer.sendMessage(prefix + "§c你不能加入自己所在的频道");
            return;
        }
        if (cc.listAll().size() - 1 == 0 && cc != localChatChannel) {
            if (localChatChannelMap.containsKey(cc.getUniqueId())) {
                paramPlayer.sendMessage(prefix + "§c由于你退出的活动频道剩余人数为0，所以其已经被解散");
                localChatChannelMap.remove(cc.getUniqueId());
            }
        } else {
            cc.remove(paramPlayer);
        }
        PixelView.removeView(paramPlayer, "§a聊天频道", "§a频道玩家列表", "§a修改访问权限");
        paramPlayer.sendMessage(prefix + "§a你已经成功的加入了频道 §7" + paramChatChannel.getName());
        paramChatChannel.add(paramPlayer);
    }
    
    public static void move(Player paramPlayer, UUID paramUniqueId){
        ChatChannel ccInstance = forUniqueId(paramUniqueId);
        if(ccInstance != null) move(paramPlayer, ccInstance);
    }
    
    public static ChatChannel forUniqueId(UUID paramUniqueId){
        return localChatChannelMap.containsKey(paramUniqueId) ? localChatChannelMap.get(paramUniqueId) : (localChatChannel.getUniqueId().equals(paramUniqueId) ? localChatChannel : null);
    }

    public static void create(Player paramPlayer, String paramString) {
        UUID uniqueId = UUID.randomUUID();
        ChatChannel newChatChannelInstance = new PixelChatChannel(paramString, paramPlayer, uniqueId);
        PixelChatChannel.localChatChannelMap.put(uniqueId, newChatChannelInstance);
        PixelChatChannel.move(paramPlayer, newChatChannelInstance);
    }

    public static ChatChannel forPlayer(Player paramPlayer) {
        for (UUID paramUniqueId : localChatChannelMap.keySet()) {
            if (localChatChannelMap.get(paramUniqueId).contains(paramPlayer)) {
                return localChatChannelMap.get(paramUniqueId);
            }
        }
        return localChatChannel;
    }

    public static Set<ChatChannel> forName(String paramString) {
        Set<ChatChannel> set = new HashSet<>();
        localChatChannelMap.values().forEach((paramChatChannel) -> {
            if (paramChatChannel.getName().contains(paramString)) {
                set.add(paramChatChannel);
            }
        });
        return set;
    }
    
    protected static View viewPage(int paramInteger, Player paramPlayer) {
        View view = new PixelView();
        List<ChatChannel> available = new ArrayList();
        Risker r = PixelRisker.get(paramPlayer);
        available.add(localChatChannel);
        localChatChannelMap.values().forEach((paramChatChannel) -> {
            if (paramChatChannel.getAccessLevel() == Access.PUBLIC
                    || (paramChatChannel.getAccessLevel() == Access.FRIENDLY && r.isFriend(paramChatChannel.getOwner()))) {
                available.add(paramChatChannel);
            }
        });
        for (int i = 27 * (paramInteger - 1); (i < 27 + 27 * (paramInteger - 1)) && i < available.size(); i++) {
            view.setItem(i % 27, available.get(i).icon());
        }
        view.setItem(27, ObjectSet.itemStackLastPage);
        view.setItem(35, ObjectSet.itemStackNextPage);
        return view;
    }

    protected PixelChatChannel() {
        localString0 = "默认频道";
        localItemStack = Homelessness.core.getReflection().set(new CraftItemStack(Material.WOOL, (short) 14, "§a聊天频道",
                new String[]{"§a频道名 §7>> " + localString0,
                    "§a访问权限 §7>> " + localAccess.getName(),
                    "§a密码 §7>> " + (localBoolean ? "§a是" : "§c否")}).create(), "uid", localUniqueId.toString());
    }

    protected PixelChatChannel(String paramString, UUID paramUniqueId) {
        localString0 = paramString;
        localUniqueId = paramUniqueId;
        localItemStack = Homelessness.core.getReflection().set(new CraftItemStack(Material.WOOL, (short) 14, "§a聊天频道",
                new String[]{"§a频道名 §7>> " + localString0,
                    "§a访问权限 §7>> " + localAccess.getName(),
                    "§a密码 §7>> " + (localBoolean ? "§a是" : "§c否")}).create(), "uid", localUniqueId.toString());
    }

    protected PixelChatChannel(String paramString, Player paramPlayer, UUID paramUniqueId) {
        this(paramString, paramUniqueId);
        localPlayer = paramPlayer;
    }

    @Override
    public List<Player> listAll() {
        return localPlayerList;
    }

    @Override
    public void add(Player paramPlayer) {
        localPlayerList.add(paramPlayer);
        localPlayerList.stream().forEach((paramPlayer0) -> {
            paramPlayer0.sendMessage(prefix + "§a玩家 §7" + paramPlayer.getName() + " §a加入了聊天频道");
        });
    }

    @Override
    public void remove(Player paramPlayer) {
        localPlayerList.remove(paramPlayer);
        localPlayerList.stream().forEach((paramPlayer0) -> {
            paramPlayer0.sendMessage(prefix + "§a玩家 §7" + paramPlayer.getName() + " §a离开频道");
        });
    }

    @Override
    public boolean contains(Player paramPlayer) {
        return localPlayerList.contains(paramPlayer);
    }

    @Override
    public void check() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearMessageBar() {
        new Thread() {
            @Override
            public void run() {
                localPlayerList.stream().forEach((paramPlayer) -> {
                    for (int i = 0; i < 32; i++) {
                        paramPlayer.sendMessage(new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ",
                            " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "});
                    }
                });
            }
        }.start();
    }

    @Override
    public void chat(Player paramPlayer, String paramString) {
        YamlConfiguration config = PixelConfiguration.option;
        String format = config.contains("Chat.format") ? config.getString("Chat.format").replace("&", "§") : "§f<§a%player§f> %message";
        String message = format.replaceAll("%player", paramPlayer.getName()).replaceAll("%message", paramString);
        localPlayerList.stream().filter((paramPlayer0) -> !(paramPlayer0 == null)).forEach((paramPlayer0) -> {
            paramPlayer0.sendMessage(message);
        });
    }

    @Override
    public void setPassword(String paramString) {
        localString = paramString;
        localBoolean = !"null".equals(localString);
        localItemStack = Homelessness.core.getReflection().set(new CraftItemStack(Material.WOOL, (short) 14, "§a聊天频道",
                new String[]{"§a频道名 §7>> " + localString0,
                    "§a访问权限 §7>> " + localAccess.getName(),
                    "§a密码 §7>> " + (localBoolean ? "§a是" : "§c否")}).create(), "vagrant:chatchannel:name", localString0);
    }

    @Override
    public String getPassword() {
        return localString;
    }

    @Override
    public boolean usePassword() {
        return localBoolean;
    }

    @Override
    public String getName() {
        return localString0;
    }

    @Override
    public void setName(String paramString) {
        localString0 = paramString;
    }

    @Override
    public void setAccessLevel(Access paramAccessLevel) {
        localAccess = paramAccessLevel;
        localItemStack = Homelessness.core.getReflection().set(new CraftItemStack(Material.WOOL, (short) 14, "§a聊天频道",
                new String[]{"§a频道名 §7>> " + localString0,
                    "§a访问权限 §7>> " + localAccess.getName(),
                    "§a密码 §7>> " + (localBoolean ? "§a是" : "§c否")}).create(), "vagrant:chatchannel:name", localString0);
    }

    @Override
    public Access getAccessLevel() {
        return localAccess;
    }

    @Override
    public ItemStack icon() {
        return localItemStack;
    }

    @Override
    public void setOwner(Player paramPlayer) {
        localPlayer = paramPlayer;
    }

    @Override
    public Player getOwner() {
        return localPlayer;
    }

    @Override
    public void addAll(List<Player> paramPlayerList) {
        localPlayerList.addAll(paramPlayerList);
    }

    @Override
    public void removeAll(List<Player> paramPlayerList) {
        localPlayerList.removeAll(paramPlayerList);
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId;
    }

    @Override
    public View getView() {
        View view = new PixelView(localUniqueId);
        view.setItem(1, new CraftItemStack(Material.NAME_TAG, "§a访问权限", new String[]{"§b> §a" + localAccess.getName(), "§7点击修改"}).create());
        view.setItem(2, new CraftItemStack(Material.PAPER, "§a频道密码", new String[]{"§b> §a" + localString, "§7仅频道所有者为你时有效", "§7但这不会对当前已经在频道的用户产生影响"}).create());
        view.setItem(3, new CraftItemStack(Material.SKULL_ITEM, (short) 3, "§a频道内玩家", new String[]{"§b> §a" + localPlayerList.size() + " 人在此频道中"}).create());
        view.setItem(7, localItemStack);
        view.setItem(19, create);
        view.setItem(20, list);
        view.setItem(21, dissolve);
        return view;
    }

    @Override
    public void updateView(Player paramPlayer) {
        PixelView.addView(paramPlayer, "§a聊天频道", getView());
    }
}
