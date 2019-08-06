/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.PixelArea.AreaEditor;
import com.vagrantstudio.homelessness.api.Access;
import com.vagrantstudio.homelessness.api.Area.OwnerType;
import com.vagrantstudio.homelessness.api.ChatChannel;
import com.vagrantstudio.homelessness.api.Feudal;
import com.vagrantstudio.homelessness.api.Friends;
import com.vagrantstudio.homelessness.api.Guild;
import com.vagrantstudio.homelessness.api.Guild.Grade;
import com.vagrantstudio.homelessness.api.Party;
import com.vagrantstudio.homelessness.api.Task;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.WareCollection;
import com.vagrantstudio.homelessness.api.Warehouse;
import com.vagrantstudio.homelessness.api.util.CraftEntry;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Retr0
 */
public class ListenerOfInventory implements Listener {

    protected Map<String, Click> localClickMap = new HashMap();
    protected Map<String, Close> localCloseMap = new HashMap();

    protected ListenerOfInventory() {
        localClickMap.put("§a玩家信息", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a聊天频道":
                        PixelView.addView(player, "§a聊天频道", PixelChatChannel.forPlayer(Bukkit.getPlayer(uid)).getView());
                        Homelessness.core.openView(player, "§a聊天频道");
                        break;
                    case "§c没有公会":
                        PixelView.addView(player, "§a开始你的公会生涯", ObjectSet.viewStartGuild);
                        Homelessness.core.openView(player, "§a开始你的公会生涯");
                        break;
                    case "§a公会":
                        PixelView.addView(player, "§a公会", PixelGuild.forPlayer(player).getView());
                        Homelessness.core.openView(player, "§a公会");
                        break;
                    case "§a好友":
                        PixelView.addView(player, "§a好友", PixelRisker.get(player).friends().page(1));
                        Homelessness.core.openView(player, "§a好友");
                        break;
                    case "§c不在组队中":
                        PixelView.addView(player, "§a开始使用组队", ObjectSet.viewStartParty);
                        Homelessness.core.openView(player, "§a开始使用组队");
                        break;
                    case "§a组队":
                        Party party = PixelParty.forPlayer(player);
                        PixelView.addView(player, "§a组队", party.getView());
                        // 不应在一个 InventoryEvent 中开启其他背包 —— 754503921
                        Homelessness.core.openView(player, "§a组队");
                        break;
                }
            }
        });
        localClickMap.put("§a开始你的公会生涯", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a加入一个公会":
                        PixelView.addView(player, "§a公会列表", PixelGuild.viewPage(1));
                        Homelessness.core.openView(player, "§a公会列表");
                        break;
                    case "§a创建一个公会":
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("创建公会", ""));
                        player.closeInventory();
                        player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.InputGuildName"));
                        break;
                }
            }
        });
        localClickMap.put("§a聊天频道", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                ChatChannel ccInstance = PixelChatChannel.forPlayer(player);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a创建新的频道":
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("创建聊天频道", ""));
                        player.closeInventory();
                        player.sendMessage(PixelChatChannel.prefix + PixelConfiguration.getLang("Message.ChatChannel.InputChannelName"));
                        break;
                    case "§c解散当前频道":
                        if (ccInstance.getOwner() == player) {
                            PixelView.addView(player, "§a操作确认", ObjectSet.viewConfirm);
                            Homelessness.core.openView(player, "§a操作确认");
                            ObjectSet.localInvActionMap.put(player, new CraftEntry("解散聊天频道", ccInstance.getUniqueId()));
                        } else {
                            player.sendMessage(PixelChatChannel.prefix + PixelConfiguration.getLang("Message.ChatChannel.NotOwner"));
                            player.closeInventory();
                        }
                        break;
                    case "§a查看聊天频道列表":
                        PixelView.addView(player, "§a聊天频道列表", PixelChatChannel.viewPage(1, player));
                        Homelessness.core.openView(player, "§a聊天频道列表");
                        break;
                    case "§a访问权限":
                        if (ccInstance.getOwner() == null || !ccInstance.getOwner().getUniqueId().equals(player.getUniqueId())) {
                            return;
                        }
                        try {
                            View view = ObjectSet.viewSetAccess.clone();
                            view.setUniqueId(uid);
                            PixelView.addView(player, "§a选择访问权限", view);
                            Homelessness.core.openView(player, "§a选择访问权限");
                        } catch (CloneNotSupportedException ex) {
                            Logger.getLogger(ListenerOfInventory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case "§a频道密码":
                        if (ccInstance.getOwner() == null || !ccInstance.getOwner().getUniqueId().equals(player.getUniqueId())) {
                            return;
                        }
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("修改频道密码", uid));
                        player.closeInventory();
                        break;
                    case "§a频道内玩家":
                        new Thread() {
                            @Override
                            public void run() {
                                View view = new PixelView(uid);
                                List<Player> listPlayer = ccInstance.listAll();
                                for (int loop = 0; loop < 36 && loop < listPlayer.size(); loop++) {
                                    view.setItem(loop, PixelRisker.get(listPlayer.get(loop)).icon());
                                }
                                PixelView.addView(player, "§a频道玩家列表", view);
                                Homelessness.core.openView(player, "§a频道玩家列表");
                            }
                        }.start();
                        break;
                }
            }
        });
        localClickMap.put("§a操作确认", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                if (clickedItemStack.getItemMeta().getDisplayName().equals("§a我确认该操作")) {
                    Entry<String, UUID> entry = ObjectSet.localInvActionMap.get(player);
                    switch (entry.getKey()) {
                        case "解散聊天频道":
                            ChatChannel cc = PixelChatChannel.localChatChannelMap.get(entry.getValue());
                            cc.listAll().stream().forEach((paramPlayer) -> {
                                PixelChatChannel.move(paramPlayer, PixelChatChannel.localChatChannel);
                            });
                            PixelChatChannel.localChatChannelMap.remove(cc.getUniqueId());
                            PixelView.removeView(player, "§a聊天频道");
                            PixelRisker.get(player).updateView(player);
                            break;
                        case "解散公会":
                            PixelGuild.dissolve(entry.getValue());
                            break;
                        case "退出公会":
                            break;
                        case "组队-集结":
                            OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(entry.getValue());
                            if (offlineInstance.isOnline()) {
                                player.teleport(offlineInstance.getPlayer());
                            } else {
                                player.sendMessage(PixelParty.prefix + PixelConfiguration.getLang("Message.Party.TargetOffline"));
                            }
                            break;
                        case "踢出公会成员":
                            PixelGuild.forPlayer(player).kick(uid);
                            break;
                    }
                }
                PixelView.removeView(player, "§a操作确认");
                Homelessness.core.openView(player, null);
                ObjectSet.localInvActionMap.remove(player);
            }
        });
        localClickMap.put("§a个人银行", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a存款":
                        player.closeInventory();
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("银行存款", uid));
                        player.sendMessage(PixelBank.prefix + PixelConfiguration.getLang("Message.Bank.InputValue"));
                        break;
                    case "§a取款":
                        player.closeInventory();
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("银行取款", uid));
                        player.sendMessage(PixelBank.prefix + PixelConfiguration.getLang("Message.Bank.InputValue"));
                        break;
                }
            }
        });
        localClickMap.put("§a公会", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                Guild guild = PixelGuild.forPlayer(player);
                /*if (guild != null && guild.getUniqueId() != uid) {
                 return;
                 }*/
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§c退出公会":
                        if (!guild.getOwner().getName().equals(player.getName())) {
                            PixelView.addView(player, "§a操作确认", ObjectSet.viewConfirm);
                            Homelessness.core.openView(player, "§a操作确认");
                            ObjectSet.localInvActionMap.put(player, new CraftEntry("退出公会", uid));
                        } else {
                            player.closeInventory();
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.CannotDissolve"));
                        }
                        break;
                    case "§a仓库":
                        PixelView.addView(player, "§a仓库储集", guild.ware().getView());
                        Homelessness.core.openView(player, "§a仓库储集");
                        break;
                    case "§a公会银行":
                        View viewBank = new PixelView(guild.getUniqueId());
                        viewBank.setItem(6, guild.bank().icon());
                        viewBank.setItem(20, PixelBank.DONATE);
                        viewBank.setItem(21, PixelBank.WITHDRAW);
                        PixelView.addView(player, "§a公会银行", viewBank);
                        Homelessness.core.openView(player, "§a公会银行");
                        break;
                    case "§a公会选项":
                        if (guild.getGrade(player) == Grade.MEMBER) {
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.NoPerm"));
                            player.closeInventory();
                            return;
                        }
                        PixelView.addView(player, "§a公会选项", guild.getOptionInterface());
                        Homelessness.core.openView(player, "§a公会选项");
                        break;
                    case "§a公会广播":
                        if (guild.getGrade(player) == Grade.MEMBER) {
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.NoPerm"));
                            player.closeInventory();
                            return;
                        }
                        player.closeInventory();
                        player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.Broadcast"));
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("公会广播", uid));
                        break;
                    case "§a公会聊天频道":
                        PixelChatChannel.move(player, guild.getChatChannel());
                        break;
                }
            }
        });
        localClickMap.put("§a公会选项", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                Guild guild = PixelGuild.localGuildMap.get(uid);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a升级公会":
                        if(guild.upgrade()){
                            guild.broadcast(PixelConfiguration.getLang("Message.Guild.UpgradeSuccess"));
                        } else {
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.UpgradeFailed"));
                        }
                        break;
                    case "§a管理入会申请":
                        View view = new PixelView();
                        guild.getApplications().stream().forEach((paramUniqueId) -> {
                            view.addItem(PixelRisker.localMap.get(paramUniqueId).icon());
                        });
                        PixelView.addView(player, "§a入会申请列表", view);
                        Homelessness.core.openView(player, title);
                        break;
                    case "§a管理成员":
                        View member = new PixelView(uid);
                        int loop = 0;
                        for (UUID uniqueId : guild.getPlayers().keySet()) {
                            if (loop == 27) {
                                break;
                            }
                            member.addItem(PixelRisker.localMap.get(uniqueId).icon());
                        }
                        PixelView.addView(player, "§a公会成员管理", member);
                        Homelessness.core.openView(player, "§a公会成员管理");
                        break;
                    case "§a编辑公会宣言":
                        if (paramInventoryClickEvent.isLeftClick()) {
                            ObjectSet.localChatActionMap.put(player, new CraftEntry("增加公会宣言", uid));
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.AddManifesto"));
                            player.closeInventory();
                        } else {
                            List<String> manifesto = guild.getManifesto();
                            manifesto.remove(manifesto.size() - 1);
                        }
                        break;
                    case "§c解散公会":
                        if (guild.getGrade(player) == Grade.CAPTAIN) {
                            PixelView.addView(player, "§a操作确认", ObjectSet.viewConfirm);
                            Homelessness.core.openView(player, "§a操作确认");
                            ObjectSet.localInvActionMap.put(player, new CraftEntry("解散公会", uid));
                        } else {
                            player.closeInventory();
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.NoPerm"));
                        }
                        break;
                }
            }
        });
        localClickMap.put("§a入会申请列表", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                Guild guild = PixelGuild.forPlayer(player);
                UUID playerId = UUID.fromString(Homelessness.core.getReflection().getTagString(clickedItemStack, "uid"));
                if (paramInventoryClickEvent.isLeftClick()) {
                    guild.accpet(playerId);
                } else {
                    guild.deny(playerId);
                }
            }
        });
        localClickMap.put("§a公会成员管理", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                Guild guild = PixelGuild.forUniqueId(uid);
                if (paramInventoryClickEvent.getRawSlot() < 45) {
                    UUID playerId = UUID.fromString(Homelessness.core.getReflection().getTagString(clickedItemStack, "uid"));
                    if (paramInventoryClickEvent.isLeftClick()) {
                        OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(playerId);
                        Grade grade = guild.getGrade(offlineInstance);
                        if (grade == Grade.CAPTAIN) {
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.CannotSetCaptain"));
                            return;
                        }
                        if (grade == Grade.MEMBER) {
                            guild.setGrade(offlineInstance, Grade.VICE_CAPTAIN);
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.SetToViceCaptain"));
                        } else {
                            guild.setGrade(offlineInstance, Grade.MEMBER);
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.SetToMember"));
                        }
                    } else {
                        if (guild.getOwner().getUniqueId().equals(playerId)) {
                            player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Guild.CannotKickOwner"));
                            player.closeInventory();
                            return;
                        }
                        ObjectSet.localInvActionMap.put(player, new CraftEntry("踢出公会成员", playerId));
                        PixelView.addView(player, "§a操作确认", ObjectSet.viewConfirm);
                        Homelessness.core.openView(player, "§a操作确认");
                    }
                } else {
                    
                }
            }
        });
        localClickMap.put("§a公会列表", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                if (paramInventoryClickEvent.getRawSlot() < 45) {
                    UUID uniqueId = UUID.fromString(Homelessness.core.getReflection().getTagString(clickedItemStack, "uid"));
                }
            }
        });
        localClickMap.put("§a公会银行", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch(clickedItemStack.getItemMeta().getDisplayName()){
                    case "§a捐献":
                        ObjectSet.localChatActionMap.put(player, new CraftEntry<>("公会捐款", null));
                        player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Bank.InputValue"));
                        break;
                    case "§a取款":
                        ObjectSet.localChatActionMap.put(player, new CraftEntry<>("公会取款", null));
                        player.sendMessage(PixelGuild.prefix + PixelConfiguration.getLang("Message.Bank.InputValue"));
                        break;
                }
            }
        });
        localClickMap.put("§a仓库储集", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                WareCollection wa = PixelWareCollection.localWareCollectionMap.get(uid);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a点击创建新的仓库":
                        int index = paramInventoryClickEvent.getRawSlot() - 18;
                        wa.set(index, new PixelWare(uid, index));
                        player.sendMessage(PixelWareCollection.prefix + PixelConfiguration.getLang("Message.Ware.CreateSuccess").replace("%slot",
                                String.valueOf(PixelWareCollection.allowed.indexOf(paramInventoryClickEvent.getRawSlot() - 18) + 1)));
                        PixelView.addView(player, "§a仓库储集", wa.getView());
                        Homelessness.core.openView(player, "§a仓库储集");
                        break;
                    case "§a仓库":
                        PixelView.addView(player, "§a仓库", wa.get(paramInventoryClickEvent.getRawSlot() - 18).page(1));
                        Homelessness.core.openView(player, "§a仓库");
                        break;
                }
            }
        });
        localClickMap.put("§a仓库", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                if ("container.inventory".equals(paramInventoryClickEvent.getClickedInventory().getTitle())) {
                    return;
                }
                if (clickedItemStack == null || clickedItemStack.getType() == Material.AIR) {
                    if (paramInventoryClickEvent.getRawSlot() >= 45) {
                        paramInventoryClickEvent.setCancelled(true);
                    }
                    return;
                }
                if ((paramInventoryClickEvent.getRawSlot() >= 45)
                        || "§c未解锁✘".equals(clickedItemStack.getItemMeta().getDisplayName())) {
                    paramInventoryClickEvent.setCancelled(true);
                }
            }
        });
        localClickMap.put("§a频道玩家列表", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                if (paramInventoryClickEvent.getRawSlot() < 45) {
                    PixelView.addView(player, "§a玩家信息",
                            PixelRisker.localMap.get(UUID.fromString(Homelessness.core.getReflection().getTagString(clickedItemStack, "uid"))).getView());
                    Homelessness.core.openView(player, "§a玩家信息");
                }
            }
        });
        localClickMap.put("§a好友", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a添加好友":
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("搜索玩家", null));
                        ObjectSet.localInteractActionMap.put(player, new CraftEntry("添加好友", null));
                        player.sendMessage(PixelFriends.prefix + PixelConfiguration.getLang("Message.Friends.AddFriend"));
                        player.closeInventory();
                        break;
                    case "§a上一页":
                        break;
                    case "§a下一页":
                        break;
                    case "§a刷新好友列表":
                        PixelView.addView(player, "§a好友", PixelRisker.localMap.get(uid).friends().page(1));
                        Homelessness.core.openView(player, "§a好友");
                        break;
                    case "§a申请列表":
                        View view = new PixelView(player.getUniqueId());
                        int i = 0;
                        for (UUID paramUniqueId : PixelRisker.get(player).friends().applications()) {
                            if (i == 36) {
                                break;
                            }
                            view.setItem(i, PixelRisker.localMap.get(paramUniqueId).icon());
                        }
                        PixelView.addView(player, "§a好友申请列表", view);
                        Homelessness.core.openView(player, "§a好友申请列表");
                        break;
                    default:
                        break;
                }
            }
        });
        localClickMap.put("§a选择访问权限", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                ChatChannel cc = PixelChatChannel.forUniqueId(uid);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a公开的":
                        cc.setAccessLevel(Access.PUBLIC);
                        break;
                    case "§a仅好友可见":
                        cc.setAccessLevel(Access.FRIENDLY);
                        break;
                    case "§a私有的":
                        cc.setAccessLevel(Access.PRIVATE);
                        break;
                }
                PixelView.removeView(player, "§a选择访问权限");
                PixelView.addView(player, "§a聊天频道", cc.getView());
                Homelessness.core.openView(player, "§a聊天频道");
            }
        });
        localClickMap.put("§a聊天频道列表", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                if (paramInventoryClickEvent.getRawSlot() < 45) {
                    PixelChatChannel.move(player, UUID.fromString(Homelessness.core.getReflection().getTagString(clickedItemStack, "uid")));
                } else {
                    switch (clickedItemStack.getItemMeta().getDisplayName()) {
                        case "§a上一页":
                            break;
                        case "§a下一页":
                            break;
                    }
                }
            }
        });
        localClickMap.put("§a好友申请列表", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                Friends friends = PixelRisker.localMap.get(uid).friends();
                OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(UUID.fromString(Homelessness.core.getReflection().getTagString(clickedItemStack, "uid")));
                if (paramInventoryClickEvent.isLeftClick()) {
                    friends.accept(offlineInstance);
                    player.sendMessage(PixelFriends.prefix + PixelConfiguration.getLang("Message.Friends.AcceptApplication").replace("%name", offlineInstance.getName()));
                    if (offlineInstance.isOnline()) {
                        offlineInstance.getPlayer().sendMessage(PixelFriends.prefix + PixelConfiguration.getLang("Message.Friends.PlayerAcceptYourApplication").replace("%name", player.getName()));
                    }
                } else {
                    friends.deny(offlineInstance);
                }
                View view = new PixelView(player.getUniqueId());
                int i = 0;
                for (UUID paramUniqueId : PixelRisker.get(player).friends().applications()) {
                    if (i == 36) {
                        break;
                    }
                    view.setItem(i, PixelRisker.localMap.get(paramUniqueId).icon());
                }
                PixelView.addView(player, "§a好友", friends.page(1));
                PixelView.addView(player, "§a好友申请列表", view);
                Homelessness.core.openView(player, "§a好友申请列表");
            }
        });
        localClickMap.put("§a创建领域", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                AreaEditor editor = PixelArea.arenaEditorMap.get(player);
                switch (paramInventoryClickEvent.getRawSlot()) {
                    case 22:
                        player.closeInventory();
                        player.sendMessage(PixelArea.prefix + PixelConfiguration.getLang("Message.Area.InputAreaName"));
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("创建领域-改名", null));
                        break;
                    case 23:
                        if (editor.getOwnerType() == OwnerType.NOT_OWNED) {
                            editor.setOwnerType(OwnerType.OWNED);
                        } else if (editor.getOwnerType() == OwnerType.OWNED) {
                            editor.setOwnerType(OwnerType.SERVER_LAND);
                        } else {
                            editor.setOwnerType(OwnerType.NOT_OWNED);
                        }
                        PixelView.addView(player, "§a创建领域", editor.getView());
                        Homelessness.core.openView(player, "§a创建领域");
                        break;
                    case 24:
                        if (editor.getOwnerType() != OwnerType.OWNED) {
                            return;
                        }
                        editor.setFeudal(editor.getFeudal() == Feudal.GUILD ? Feudal.PLAYER : Feudal.GUILD);
                        PixelView.addView(player, "§a创建领域", editor.getView());
                        Homelessness.core.openView(player, "§a创建领域");
                        break;
                    case 33:
                        ObjectSet.localChatActionMap.put(player, new CraftEntry(editor.getFeudal() == Feudal.PLAYER ? "创建领域-选择玩家" : "创建领域-选择公会", null));
                        break;
                    case 47:
                        PixelArea.create(player);
                        player.closeInventory();
                        PixelView.removeView(player, "§a创建领域");
                        break;
                    case 51:
                        PixelArea.arenaEditorMap.remove(player);
                        player.closeInventory();
                        PixelView.removeView(player, "§a创建领域");
                        break;
                }
            }
        });
        localClickMap.put("§a任务委托", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                List<String> completedTasks = PixelTask.localCompletedTaskMap.get(player);
                List<ItemStack> taskIcon = new ArrayList();
                View view = new PixelView();
                Task.Type type = null;
                if ("§c放弃任务".equals(clickedItemStack.getItemMeta().getDisplayName())) {
                    PixelTask.taskSnapshotMap.get(player.getUniqueId()).cancel();
                    return;
                }
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a主线任务":
                        type = Task.Type.MAIN_TASK;
                        break;
                    case "§a支线任务":
                        type = Task.Type.SIDE_TASK;
                        break;
                    case "§a普通任务":
                        type = Task.Type.NORMAL;
                        break;
                }
                for (Task paramTask : PixelTask.localTaskMap.values()) {
                    if (completedTasks.containsAll(paramTask.getRequirement()) && paramTask.getType() == type) {
                        taskIcon.add(paramTask.icon());
                    }
                }
                player.closeInventory();
                for (int i = 0; i < taskIcon.size() && i < 36; i++) {
                    view.setItem(i, taskIcon.get(i));
                }
                view.setItem(27, ObjectSet.itemStackLastPage);
                view.setItem(35, ObjectSet.itemStackNextPage);
                PixelView.addView(player, "§a" + type.getName(), view);
                Homelessness.core.openView(player, "§a" + type.getName());
            }
        });
        localClickMap.put("§a开始使用组队", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a查找组队":
                        player.closeInventory();
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("查找组队", null));
                        player.sendMessage(PixelParty.prefix + PixelConfiguration.getLang("Message.Party.Search"));
                        break;
                    case "§a创建组队":
                        player.closeInventory();
                        ObjectSet.localChatActionMap.put(player, new CraftEntry("创建组队", null));
                        player.sendMessage(PixelParty.prefix + PixelConfiguration.getLang("Message.Party.Create"));
                        break;
                }
            }
        });
        localClickMap.put("§a组队", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                Party party = PixelParty.forPlayer(player);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a邀请玩家":
                        View view = new PixelView();
                        List<UUID> players = PixelRisker.get(player).friends().getOnlines();
                        for (int loop = 0; loop < 36 && loop < players.size(); loop++) {
                            view.addItem(PixelRisker.localMap.get(players.get(loop)).icon());
                        }
                        PixelView.addView(player, "§a邀请好友加入组队", view);
                        Homelessness.core.openView(player, "§a邀请好友加入组队");
                        break;
                    case "§a查看组队内玩家列表":
                        View members = new PixelView();
                        party.getPlayers().stream().forEach((paramOfflinePlayer) -> {
                            members.addItem(PixelRisker.get(paramOfflinePlayer).icon());
                        });
                        PixelView.addView(player, "§a组队玩家列表", members);
                        Homelessness.core.openView(player, "§a组队玩家列表");
                        break;
                    case "§a召集成员":
                        party.convene(player);
                        break;
                }
            }
        });
        localClickMap.put("§a邀请好友", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
            }
        });
        localClickMap.put("§a组队玩家列表", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
            }
        });
        localClickMap.put("§a主线任务", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a上一页":
                        break;
                    case "§a下一页":
                        break;
                    default:
                        PixelTask.startTask(Homelessness.core.getReflection().getTagString(clickedItemStack, "name"), player);
                        break;
                }
            }
        });
        localClickMap.put("§a支线任务", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a上一页":
                        break;
                    case "§a下一页":
                        break;
                    default:
                        PixelTask.startTask(Homelessness.core.getReflection().getTagString(clickedItemStack, "name"), player);
                        break;
                }
            }
        });
        localClickMap.put("§a普通任务", new Click() {
            @Override
            protected void invoke(InventoryClickEvent paramInventoryClickEvent) {
                paramInventoryClickEvent.setCancelled(true);
                switch (clickedItemStack.getItemMeta().getDisplayName()) {
                    case "§a上一页":
                        break;
                    case "§a下一页":
                        break;
                    default:
                        PixelTask.startTask(Homelessness.core.getReflection().getTagString(clickedItemStack, "name"), player);
                        break;
                }
            }
        });

        localCloseMap.put("§a仓库", new Close() {
            @Override
            protected void invoke(InventoryCloseEvent paramInventoryCloseEvent) {
                int page = Homelessness.core.getReflection().getTagInteger(inventory.getItem(49), "page"), location = Homelessness.core.getReflection().getTagInteger(inventory.getItem(49), "location");
                WareCollection wcInstance = PixelWareCollection.localWareCollectionMap.get(uid);
                Warehouse wInstance = wcInstance.get(location);
                wInstance.set(page, Arrays.asList(Arrays.copyOfRange(inventory.getContents(), 18, 45)));
                wcInstance.updateView(player);
                PixelView.localMenuMap.get(player).replace("§a仓库", wInstance.page(page));
            }
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent paramInventoryClickEvent) {
        Inventory inventory = paramInventoryClickEvent.getInventory();
        // 基于 title 的背包菜单判断，建议更换为 InventoryHolder 的判断 —— 754503921
        if (inventory == null || !ObjectSet.inventoryTitle.equals(inventory.getTitle())) {
            return;
        }
        ItemStack itemStack = paramInventoryClickEvent.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }
        ItemStack viewItemStack = null;
        String title = "";
        Player player = (Player) paramInventoryClickEvent.getWhoClicked();
        UUID uid = null;

        ItemStack[] itemStackArray = inventory.getStorageContents();
        int i = 0;
        for (ItemStack paramItemStack : itemStackArray) {
            if (i == 8) {
                break;
            }
            if (paramItemStack == null) {
                continue;
            }
            if (paramItemStack.hasItemMeta() && paramItemStack.getItemMeta().hasEnchants()) {
                viewItemStack = paramItemStack;
                title = paramItemStack.getItemMeta().getDisplayName();
                String value = Homelessness.core.getReflection().getTagString(paramItemStack, "uid");
                uid = value == null || "".equals(value) ? null : UUID.fromString(value);
                break;
            }
        }
        if ("§c菜单占位符".equals(itemStack.getItemMeta().getDisplayName())) {
            paramInventoryClickEvent.setCancelled(true);
            return;
        }
        if (paramInventoryClickEvent.getRawSlot() < 9) {
            paramInventoryClickEvent.setCancelled(true);
            if ("§a仓库".equals(title)) {
                player.closeInventory();
            }
            if (paramInventoryClickEvent.isLeftClick()) {
                Homelessness.core.openView(player, inventory.getItem(paramInventoryClickEvent.getRawSlot()).getItemMeta().getDisplayName());
            } else {
                String remove = itemStack.getItemMeta().getDisplayName();
                PixelView.removeView(player, remove);
                Homelessness.core.openView(player, "".equals(title) ? null : (remove.equals(title) ? null : title));
            }
            return;
        }
        if (paramInventoryClickEvent.getRawSlot() >= 9 && paramInventoryClickEvent.getRawSlot() < 18) {
            paramInventoryClickEvent.setCancelled(true);
        }
        if (viewItemStack == null) {
            paramInventoryClickEvent.setCancelled(true);
            return;
        }
        if (localClickMap.containsKey(title)) {
            Click instance = localClickMap.get(title);
            instance.init(paramInventoryClickEvent, viewItemStack, uid);
            instance.invoke(paramInventoryClickEvent);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent paramInventoryCloseEvent) {
        Inventory inventory = paramInventoryCloseEvent.getInventory();
        if (!ObjectSet.inventoryTitle.equals(inventory.getTitle())) {
            return;
        }
        ItemStack viewItemStack = null;
        ItemStack[] itemStackArray = inventory.getStorageContents();
        String title = "";
        UUID uid = null;
        int i = 0;
        for (ItemStack paramItemStack : itemStackArray) {
            if (i == 9) {
                break;
            }
            if (paramItemStack == null) {
                continue;
            }
            if (paramItemStack.hasItemMeta() && paramItemStack.getItemMeta().hasEnchants()) {
                viewItemStack = paramItemStack;
                title = viewItemStack.getItemMeta().getDisplayName();
                String value = Homelessness.core.getReflection().getTagString(paramItemStack, "uid");
                uid = value == null || "".equals(value) ? null : UUID.fromString(value);
                break;
            }
        }
        if (localCloseMap.containsKey(title)) {
            Close instance = localCloseMap.get(title);
            instance.init(paramInventoryCloseEvent, viewItemStack, uid);
            instance.invoke(paramInventoryCloseEvent);
        }
    }

    protected abstract static class Click {

        protected Inventory inventory;
        protected ItemStack clickedItemStack;
        protected ItemStack viewItemStack;
        protected Player player; // 保存玩家引用 —— 754503921
        protected String title;
        protected UUID uid;

        protected void init(InventoryClickEvent paramInventoryClickEvent, ItemStack paramViewItemStack, UUID paramUniqueId) {
            inventory = paramInventoryClickEvent.getInventory();
            player = (Player) paramInventoryClickEvent.getWhoClicked();
            clickedItemStack = paramInventoryClickEvent.getCurrentItem();
            viewItemStack = paramViewItemStack;
            uid = paramUniqueId;
        }

        protected abstract void invoke(InventoryClickEvent paramInventoryClickEvent);
    }

    protected abstract static class Close {

        protected Inventory inventory;
        protected ItemStack viewItemStack;
        protected Player player;
        protected String title;
        protected UUID uid;

        protected void init(InventoryCloseEvent paramInventoryCloseEvent, ItemStack paramViewItemStack, UUID paramUniqueId) {
            inventory = paramInventoryCloseEvent.getInventory();
            player = (Player) paramInventoryCloseEvent.getPlayer();
            viewItemStack = paramViewItemStack;
            uid = paramUniqueId;
        }

        protected abstract void invoke(InventoryCloseEvent paramInventoryCloseEvent);

    }
}
