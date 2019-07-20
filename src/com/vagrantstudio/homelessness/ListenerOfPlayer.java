/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.PixelArea.AreaEditor;
import com.vagrantstudio.homelessness.api.Area;
import com.vagrantstudio.homelessness.api.Area.OwnerType;
import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.ChatChannel;
import com.vagrantstudio.homelessness.api.Feudal;
import com.vagrantstudio.homelessness.api.Party;
import com.vagrantstudio.homelessness.api.Risker;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.Numeric;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Retr0
 */
public class ListenerOfPlayer implements Listener {

    protected Map<String, Chat> localChatMap = new HashMap();
    protected Map<String, InteractEntity> localInteractEntityMap = new HashMap();
    protected Map<Material, Interact> localInteractMap = new HashMap();

    public ListenerOfPlayer() {
        localChatMap.put("创建聊天频道", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                if (message.length() <= 16 && message.length() >= 3) {
                    PixelChatChannel.create(player, message);
                    ObjectSet.localChatActionMap.remove(player);
                    PixelRisker.get(player).updateView(player);
                    PixelView.removeView(player, "§a聊天频道");
                    Homelessness.core.openView(player, "§a玩家信息");
                } else {
                    player.sendMessage("§c频道名的长度必须要在 §e3~16 §c之间，当前的长度为 §e" + message.length());
                }
            }
        });
        localChatMap.put("修改频道密码", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                ChatChannel ccInstance = PixelChatChannel.forPlayer(player);
                ccInstance.setPassword(message);
                ccInstance.updateView(player);
                player.sendMessage("§a密码修改成功了 新的密码是： §7" + message);
                ObjectSet.localChatActionMap.remove(player);
                Homelessness.core.openView(player, "§a聊天频道");
            }
        });
        localChatMap.put("创建公会", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                PixelView.removeView(player, "§a开始你的公会生涯");
                if (PixelGuild.create(player, message)) {
                    ObjectSet.localChatActionMap.remove(player);
                }
            }
        });
        localChatMap.put("公会广播", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                PixelGuild.forPlayer(player).broadcast(message);
                ObjectSet.localChatActionMap.remove(player);
            }
        });
        localChatMap.put("银行强制存款", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                if (Numeric.isInteger(message)) {
                    PixelBank.localMap.get(ObjectSet.localChatActionMap.get(player).getValue()).deposit(Integer.valueOf(message));
                    player.sendMessage(PixelBank.prefix + "§f存款已设置");
                } else {
                    player.sendMessage(PixelConfiguration.option.getString("Bank.Prefix").replace("&", "§") + "§c字符串不合格，请输入数字");
                }
            }
        });
        localChatMap.put("银行强制取款", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                if (Numeric.isInteger(message)) {
                    Bank bank = PixelBank.localMap.get(ObjectSet.localChatActionMap.get(player).getValue());
                    double after = bank.getBalance() - Integer.valueOf(message);
                    bank.setBalance(after < 0 ? 0 : after);
                    player.sendMessage(PixelBank.prefix + "§f存款已设置");
                } else {
                    player.sendMessage(PixelBank.prefix + "§c这不是一个纯数字");
                }
            }
        });
        localChatMap.put("银行设置金钱", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                if (Numeric.isInteger(message)) {
                    PixelBank.localMap.get(ObjectSet.localChatActionMap.get(player).getValue()).setBalance(Integer.valueOf(message));
                } else {
                    player.sendMessage(PixelBank.prefix + "§c这不是一个纯数字");
                }
            }
        });
        localChatMap.put("搜索玩家", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                if (player.getName().equals(message)) {
                    player.sendMessage(PixelFriends.prefix + "§c你不能添加自己为好友");
                    return;
                }
                OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(message);
                if (offlineInstance != null && offlineInstance.hasPlayedBefore()) {
                    PixelRisker.get(player).friends().apply(offlineInstance);
                    player.sendMessage(PixelFriends.prefix + "§a你向该玩家发送了好友请求");
                    ObjectSet.localChatActionMap.remove(player);
                    ObjectSet.localInteractActionMap.remove(player);
                } else {
                    player.sendMessage(PixelFriends.prefix + PixelConfiguration.getLang("Message.Risker.PlayerNotFound").replaceAll("%player", message));
                }
            }
        });
        localChatMap.put("创建领域-改名", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                AreaEditor editor = PixelArea.arenaEditorMap.get(player);
                editor.setName(message);
                PixelView.addView(player, "§a创建领域", editor.getView());
                Homelessness.core.openView(player, "§a创建领域");
            }
        });
        localChatMap.put("创建领域-选择玩家", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(message);
                if (offlineInstance != null && offlineInstance.hasPlayedBefore()) {
                    AreaEditor editor = PixelArea.arenaEditorMap.get(player);
                    if (editor.getOwnerType() == OwnerType.OWNED && editor.getFeudal() == Feudal.PLAYER) {
                        editor.setOwner(offlineInstance.getUniqueId());
                        player.sendMessage(PixelArea.prefix + PixelConfiguration.getLang("Message.Area.Create.SetPlayerOwner").replace("player_name", message));
                    } else {
                        player.sendMessage(PixelArea.prefix + PixelConfiguration.getLang("Message.Area.Create.UnableChooseOwner"));
                    }
                } else {
                    player.sendMessage(PixelRisker.prefix + PixelConfiguration.getLang("Message.Risker.PlayerNotFound").replaceAll("%player", message));
                }
            }
        });
        localChatMap.put("创建领域-选择公会", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        localChatMap.put("查找组队", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                Party party = PixelParty.forName(message);
                if(party == null){
                    player.sendMessage(PixelParty.prefix + PixelConfiguration.getLang("Message.Party.NoResult"));
                } else {
                    party.join(player);
                    ObjectSet.localChatActionMap.remove(player);
                    PixelView.removeView(player, "§a开始使用组队");
                }
            }
        });
        localChatMap.put("创建组队", new Chat() {
            @Override
            protected void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
                if(PixelParty.forName(message) != null){
                    player.sendMessage(PixelParty.prefix + PixelConfiguration.getLang("Message.Party.SameName"));
                } else {
                    PixelParty.create(message, player);
                    ObjectSet.localChatActionMap.remove(player);
                    PixelView.removeView(player, "§a开始使用组队");
                }
            }
        });

        localInteractMap.put(Material.WOOD_HOE, new Interact() {
            @Override
            protected void invoke(PlayerInteractEvent paramPlayerInteractEvent) {
                paramPlayerInteractEvent.setCancelled(true);
                AreaEditor editor = PixelArea.arenaEditorMap.containsKey(player) ? PixelArea.arenaEditorMap.get(player)
                        : new AreaEditor();
                if (paramPlayerInteractEvent.hasBlock()) {
                    Location loc = paramPlayerInteractEvent.getClickedBlock().getLocation();
                    if (action == Action.LEFT_CLICK_BLOCK) {
                        editor.setFirstLocation(loc);
                        player.sendMessage(PixelArea.prefix + "§a第一个点已更改 §7x=" + loc.getBlockX() + ",y=" + loc.getBlockY() + ",z=" + loc.getBlockZ());
                    } else if (action == Action.RIGHT_CLICK_BLOCK) {
                        editor.setSecondLocation(loc);
                        player.sendMessage(PixelArea.prefix + "§a第二个点已更改 §7x=" + loc.getBlockX() + ",y=" + loc.getBlockY() + ",z=" + loc.getBlockZ());
                    }
                    PixelArea.arenaEditorMap.put(player, editor);
                } else {
                    PixelView.addView(player, "§a创建领域", editor.getView());
                    Homelessness.core.openView(player, "§a创建领域");
                }
            }
        });
        localInteractMap.put(Material.BOOK, new Interact() {
            @Override
            protected void invoke(PlayerInteractEvent paramPlayerInteractEvent) {
                paramPlayerInteractEvent.setCancelled(true);
                if (item.hasItemMeta() && "§a任务委托书".equals(item.getItemMeta().getDisplayName())) {
                    PixelView.addView(player, "§a任务委托", ObjectSet.viewTask);
                    Homelessness.core.openView(player, "§a任务委托");
                }
            }
        });

        localInteractEntityMap.put("添加好友", new InteractEntity() {
            @Override
            protected void invoke(PlayerInteractEntityEvent paramPlayerInteractEntityEvent) {
                if (entity instanceof Player) {
                    Risker risker = PixelRisker.get(player);
                    risker.friends().apply((Player) entity);
                    ObjectSet.localInteractActionMap.remove(player);
                    ObjectSet.localChatActionMap.remove(player);
                    player.sendMessage(PixelFriends.prefix + "§a你向该玩家发送了好友请求");
                } else {
                    player.sendMessage(PixelFriends.prefix + "§c请点击一名玩家以发送好友请求");
                }
            }
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        String message = event.getMessage().replace("&", "§");
        Player player = event.getPlayer();
        if (ObjectSet.localChatActionMap.containsKey(player)) {
            if (message.equals("cancel")) {
                ObjectSet.localChatActionMap.remove(player);
                player.sendMessage("§a操作已取消");
                Homelessness.core.openView(player, null);
                return;
            }
            Chat ceInstance = localChatMap.get(ObjectSet.localChatActionMap.get(player).getKey());
            ceInstance.init(event);
            ceInstance.invoke(event);
            return;
        }
        if (ObjectSet.localPasswordQueue.containsKey(player)) {
            ChatChannel cc = PixelChatChannel.localChatChannelMap.get(ObjectSet.localPasswordQueue.get(player));
            if (cc.usePassword()) {
                if (cc.getPassword().equals(message)) {
                    PixelChatChannel.move(player, cc);
                    player.sendMessage("§a你已经成功加入了 §7" + cc.getName());
                    ObjectSet.localPasswordQueue.remove(player);
                } else if (message.equals("cancel")) {
                    player.sendMessage("§a操作已取消");
                    ObjectSet.localPasswordQueue.remove(player);
                } else {
                    player.sendMessage("§c密码错误");
                }
            } else {
                PixelChatChannel.move(player, cc);
                player.sendMessage("§a频道没有或者已取消密码，你已经成功加入了 §7" + cc.getName());
            }
            return;
        }
        if (message.startsWith("!!")) {
            Bukkit.broadcastMessage("§7[§e全服喊话§7] §f<§a" + player.getName() + "§f> " + message.substring(2));
            return;
        }
        if (message.startsWith("!")) {
            player.getWorld().getPlayers().forEach((paramPlayer) -> {
                paramPlayer.sendMessage("§7[§a世界喊话§7] §f<§a" + player.getName() + "§f> " + message.substring(1));
            });
            return;
        }
        if (message.startsWith("%")) {
            PixelChatChannel.forPlayer(player).chat(player, message + " §7(外部聊天)");
            return;
        }
        PixelChatChannel.forPlayer(player).chat(player, message);
    }

    @EventHandler
    public void onToggle(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking() && player.getLocation().getPitch() == -90) {
            Map<String, View> map = PixelView.localMenuMap.get(player);
            if(PixelInstanceZone.getZoneByPlayer(player) != null){
                View view = new PixelView();
                
            }
            if (!map.containsKey("§a玩家信息") || map.get("§a玩家信息").getUniqueId().equals(player.getUniqueId())) {
                map.put("§a玩家信息", PixelRisker.localMap.get(player.getUniqueId()).getView());
            }
            Homelessness.core.openView(player, null);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PixelRisker.localMap.containsKey(player.getUniqueId())) {
            PixelRisker.localMap.put(player.getUniqueId(), new PixelRisker(player));
        }
        PixelView.localMenuMap.put(player, new HashMap<>());
        PixelChatChannel.localChatChannel.add(player);
        boolean b = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && "§a任务委托书".equals(item.getItemMeta().getDisplayName())) {
                b = true;
            }
        }
        if (!b) {
            player.getInventory().addItem(ObjectSet.itemStackTask.clone());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        
    }

    @EventHandler
    public void onMove(PlayerMoveEvent paramPlayerMoveEvent) {
        if (Numeric.compareLocation(paramPlayerMoveEvent.getFrom(), paramPlayerMoveEvent.getTo())) {
            return;
        }
        Player player = paramPlayerMoveEvent.getPlayer();
        Area areaTo = PixelArea.forLocation(paramPlayerMoveEvent.getTo());
        Area areaFrom = PixelArea.forLocation(paramPlayerMoveEvent.getFrom());
        if (areaTo != areaFrom) {
            Homelessness.core.sendTitle(player, "", (areaTo == null ? "§a荒野" : (areaTo.getOwnerType() == OwnerType.NOT_OWNED
                    ? "§a无主之地" : "§a正在进入 [§7" + areaTo.getName() + "§a]")), 5, 35, 5);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent paramPlayerInteractEvent) {
        Player player = paramPlayerInteractEvent.getPlayer();
        Action action = paramPlayerInteractEvent.getAction();
        if (paramPlayerInteractEvent.hasItem()) {
            if (localInteractMap.containsKey(paramPlayerInteractEvent.getMaterial())) {
                Interact interact = localInteractMap.get(paramPlayerInteractEvent.getMaterial());
                interact.init(paramPlayerInteractEvent);
                interact.invoke(paramPlayerInteractEvent);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent paramPlayerInteractEntityEvent) {
        Player player = paramPlayerInteractEntityEvent.getPlayer();
        if (ObjectSet.localInteractActionMap.containsKey(player)) {
            InteractEntity ieInstance = localInteractEntityMap.get(ObjectSet.localInteractActionMap.get(player).getKey());
            ieInstance.init(paramPlayerInteractEntityEvent);
            ieInstance.invoke(paramPlayerInteractEntityEvent);
            return;
        }
    }

    protected static abstract class Chat {

        protected String message;
        protected Player player;

        protected void init(AsyncPlayerChatEvent paramAsyncPlayerChatEvent) {
            message = paramAsyncPlayerChatEvent.getMessage().replace("&", "§");
            player = paramAsyncPlayerChatEvent.getPlayer();
        }

        protected abstract void invoke(AsyncPlayerChatEvent paramAsyncPlayerChatEvent);

    }

    protected static abstract class Interact {

        protected Player player;
        protected Block block;
        protected BlockFace face;
        protected Action action;
        protected ItemStack item;

        protected void init(PlayerInteractEvent paramPlayerInteractEvent) {
            block = paramPlayerInteractEvent.getClickedBlock();
            face = paramPlayerInteractEvent.getBlockFace();
            action = paramPlayerInteractEvent.getAction();
            player = paramPlayerInteractEvent.getPlayer();
            item = paramPlayerInteractEvent.getItem();
        }

        protected abstract void invoke(PlayerInteractEvent paramPlayerInteractEvent);
    }

    protected static abstract class InteractEntity {

        protected EquipmentSlot slot;
        protected Entity entity;
        protected Player player;

        protected void init(PlayerInteractEntityEvent paramPlayerInteractEntityEvent) {
            slot = paramPlayerInteractEntityEvent.getHand();
            entity = paramPlayerInteractEntityEvent.getRightClicked();
            player = paramPlayerInteractEntityEvent.getPlayer();
        }

        protected abstract void invoke(PlayerInteractEntityEvent paramPlayerInteractEntityEvent);

    }
}
