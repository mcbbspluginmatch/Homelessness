/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Friends;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class PixelFriends implements Friends {

    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Friend").replace("&", "§");
    protected static ItemStack ADD_FRIEND = new CraftItemStack(Material.WOOL, (short)5, "§a添加好友").create();
    protected static ItemStack APPLICATION_LIST = new CraftItemStack(Material.WOOL, (short)6, "§a申请列表").create();
    protected static ItemStack REFRESH = new CraftItemStack(Material.COMPASS, "§a刷新好友列表").create();

    protected UUID localUniqueId;
    protected Set<UUID> localApplicationSet = new HashSet();
    protected List<UUID> localFriendList = new ArrayList();

    protected PixelFriends(List<String> paramStringList, UUID paramUniqueId) {
        paramStringList.stream().forEach((paramString) -> { 
            localFriendList.add(UUID.fromString(paramString));
        });
        localUniqueId = paramUniqueId;
    }

    protected PixelFriends(UUID paramUniqueId) {
        localUniqueId = paramUniqueId;
    }

    @Override
    public void add(OfflinePlayer paramOfflinePlayer) {
        localFriendList.add(paramOfflinePlayer.getUniqueId());
    }

    @Override
    public void add(UUID paramUniqueId) {
        localFriendList.add(paramUniqueId);
    }

    @Override
    public void accept(OfflinePlayer paramOfflinePlayer) {
        if (localApplicationSet.contains(paramOfflinePlayer.getUniqueId())) {
            OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(localUniqueId);
            offlineInstance.getPlayer().sendMessage(prefix + "§a你已经与 " + paramOfflinePlayer.getName() + " 成为好友");
            PixelRisker.get(paramOfflinePlayer).friends().add(offlineInstance);
            if (paramOfflinePlayer.isOnline()) {
                paramOfflinePlayer.getPlayer().sendMessage(prefix + "§a你已经与 " + offlineInstance.getName() + " 成为好友");
            }
            localFriendList.add(paramOfflinePlayer.getUniqueId());
            localApplicationSet.remove(paramOfflinePlayer.getUniqueId());
        }
    }

    @Override
    public void deny(OfflinePlayer paramOfflinePlayer) {
        if (localApplicationSet.contains(paramOfflinePlayer.getUniqueId())) {
            OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(localUniqueId);
            offlineInstance.getPlayer().sendMessage(prefix + "§c你拒绝了 " + paramOfflinePlayer.getName() + " 的好友申请");
            if (paramOfflinePlayer.isOnline()) {
                paramOfflinePlayer.getPlayer().sendMessage(prefix + "§c" + offlineInstance.getName() + " 拒绝了你的好友申请");
            }
            localApplicationSet.remove(paramOfflinePlayer.getUniqueId());
        }
    }

    @Override
    public List<UUID> all() {
        return localFriendList;
    }

    @Override
    public void clear() {
        localFriendList.clear();
    }

    @Override
    public void remove(OfflinePlayer paramOfflinePlayer) {
        if (localFriendList.contains(paramOfflinePlayer.getUniqueId())) {
            OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(localUniqueId);
            offlineInstance.getPlayer().sendMessage(prefix + "§a你已经将 " + paramOfflinePlayer.getName() + " 从好友列表移除");
            if (paramOfflinePlayer.isOnline()) {
                paramOfflinePlayer.getPlayer().sendMessage(prefix + "§a" + offlineInstance.getName() + " 将你从好友列表中移除");
            }
            localFriendList.remove(paramOfflinePlayer.getUniqueId());
        }
    }

    @Override
    public void remove(UUID paramUniqueId) {
        remove(Bukkit.getOfflinePlayer(paramUniqueId));
    }

    @Override
    public ItemStack icon() {
        return Homelessness.core.getReflection().set(new CraftItemStack(Material.NAME_TAG, "§a好友", new String[]{"§7一共有 " + localFriendList.size() + " 位好友在你的好友列表中"}).create(), "uid", localUniqueId.toString());
    }

    @Override
    public View page(int paramInteger) {
        View view = new PixelView(localUniqueId);
        for(int i = (paramInteger - 1) * 27; i < (paramInteger * 27) && i < localFriendList.size(); i++) view.addItem(PixelRisker.localMap.get(localFriendList.get(i)).icon());
        view.setItem(27, ObjectSet.itemStackLastPage);
        view.setItem(30, ADD_FRIEND);
        view.setItem(31, REFRESH);
        view.setItem(32, APPLICATION_LIST);
        view.setItem(35, ObjectSet.itemStackNextPage);
        view.setPageNumber(paramInteger);
        return view;
    }

    @Override
    public List<String> toList() {
        List<String> listString = new ArrayList();
        localFriendList.stream().forEach((paramUniqueId) -> { listString.add(paramUniqueId.toString()); });
        return listString;
    }

    @Override
    public void apply(OfflinePlayer paramOfflinePlayer) {
        Player owner = Bukkit.getPlayer(localUniqueId);
        PixelFriends pfInstance = (PixelFriends) PixelRisker.localMap.get(paramOfflinePlayer.getUniqueId()).friends();
        if(pfInstance.localApplicationSet.size() < 36) {
            pfInstance.localApplicationSet.add(localUniqueId);
        } else {
            owner.sendMessage(prefix + "§c该玩家的好友申请列表已满");
        }
        if(paramOfflinePlayer.isOnline()) paramOfflinePlayer.getPlayer().sendMessage(prefix + "§a玩家 §7" + owner.getName() + " §a申请添加你为好友");
    }

    @Override
    public Set<UUID> applications() {
        return localApplicationSet;
    }

    @Override
    public List<UUID> getOnlines() {
        List<UUID> list = new ArrayList();
        localFriendList.stream().filter((paramUniqueId) -> ((Bukkit.getOfflinePlayer(paramUniqueId)).isOnline())).forEach((paramUniqueId) -> {
            list.add(paramUniqueId);
        });
        return list;
    }

}
