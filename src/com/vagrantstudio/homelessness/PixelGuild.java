/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.google.common.collect.Lists;
import com.vagrantstudio.homelessness.api.Bank;
import com.vagrantstudio.homelessness.api.ChatChannel;
import com.vagrantstudio.homelessness.api.Guild;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.WareCollection;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Retr0
 */
public final class PixelGuild implements Guild {

    protected static List<String> flags = Arrays.asList(new String[]{"container", "use", ""});
    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Guild").replace("&", "§");

    private String localString = "§4§lunnamed or unknown name"; //公会名
    private OfflinePlayer localOfflinePlayer; //所有者
    private List<UUID> localUniqueIdList = new ArrayList(); //申请列表
    protected List<String> localStringList = new ArrayList(); //公会公告
    private Map<UUID, Grade> localPlayerMap = new HashMap(); //公会玩家 K=玩家UUID V=权限等级
    private UUID localUniqueId = UUID.randomUUID(); //公会UUID
    private ChatChannel localChatChannel; //公会频道
    private Set<UUID> localAreaSet = new HashSet(); //公会领地
    private int level = 0;
    private Material icon;

    protected static final File localFile = new File("plugins/Homelessness/Guild");
    protected static final Map<UUID, Guild> localGuildMap = new HashMap<>(); //UUID 是公会ID Guild 是公会实例
    
    protected static final ItemStack dissolve = new CraftItemStack(Material.BARRIER, "§c解散公会").create();
    protected static final ItemStack broadcast = new CraftItemStack(Material.CAKE, "§a公会广播").create();
    protected static final ItemStack transfer = new CraftItemStack(Material.WOODEN_DOOR, "§a转让公会").create();
    protected static final ItemStack member = new CraftItemStack(Material.SKULL_ITEM, (short) 3, "§a管理成员", new String[]{"§a左键授权§7/§c右键踢出"}).create();
    protected static final ItemStack upgrade = new CraftItemStack(Material.DIAMOND_BLOCK, "§a升级公会", new String[]{"§7增加人数上限 +2"}).create();

    static {
        localFile.mkdirs();
        for (File paramFile : localFile.listFiles()) {
            if (paramFile.getName().endsWith(".yml")) {
                localGuildMap.put(UUID.fromString(paramFile.getName().replace(".yml", "")),
                        new PixelGuild(paramFile));
            }
        }
    }

    protected static Guild forUniqueId(UUID paramUniqueId) {
        return localGuildMap.get(paramUniqueId);
    }

    protected static Guild forPlayer(OfflinePlayer paramOfflinePlayer) {
        for (Guild paramGuild : localGuildMap.values()) {
            if (paramGuild.contains(paramOfflinePlayer)) {
                return paramGuild;
            }
        }
        return null;
    }

    public static void dissolve(UUID paramUniqueId) {
        Guild guild = localGuildMap.get(paramUniqueId);
        guild.broadcast("§c会长确认了解散公会，§7" + guild.getName() + "§c公会被解散了");
        Set<UUID> players = guild.getPlayers().keySet();
        localGuildMap.remove(paramUniqueId);
        PixelWareCollection.localWareCollectionMap.remove(paramUniqueId);
        players.forEach((paramUniqueId0) -> {
            Player player = Bukkit.getOfflinePlayer(paramUniqueId0).getPlayer();
            PixelView.removeView(player, "§a公会", "§a公会选项", "§a公会银行", "§a仓库储集", "§a仓库");
            PixelRisker.get(player).updateView(player);
        });
    }

    public static boolean create(Player paramPlayer, String paramString) {
        UUID uid = UUID.randomUUID();
        while (localGuildMap.containsKey(uid)) {
            uid = UUID.randomUUID();
        }
        paramString = paramString.replace("&", "§");
        if (forPlayer(paramPlayer) == null) {
            if (!PixelRisker.localMap.get(paramPlayer.getUniqueId()).withdraw(PixelConfiguration.option.getInt("Guild.Cost.Create"))) {
                paramPlayer.sendMessage(PixelBank.prefix + "§c余额不足");
                return false;
            }
            paramPlayer.sendMessage("§a成功的创建了 §7" + paramString + " §a公会");
            localGuildMap.put(uid, new PixelGuild(paramString, paramPlayer, uid));
            PixelRisker.get(paramPlayer).updateView(paramPlayer);
            Homelessness.core.openView(paramPlayer, "§a玩家信息");
            return true;
        } else {
            paramPlayer.sendMessage("§c创建失败，你已经在一个公会里了");
            return false;
        }
    }

    protected static View viewPage(int paramInteger) {
        View view = new PixelView();
        List<Guild> available = new ArrayList();
        localGuildMap.values().forEach(available::add);
        for (int i = 27 * (paramInteger - 1); (i < 27 + 27 * (paramInteger - 1)) && i < available.size(); i++) {
            view.setItem(i % 27, available.get(i).icon());
        }
        view.setItem(27, ObjectSet.itemStackLastPage);
        view.setItem(35, ObjectSet.itemStackNextPage);
        return view;
    }

    protected static Inventory getInventory(int page) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§c§l公会 §7列表");
        int s = (page - 1) * 45, sum = 0;
        for (Guild paramGuild : localGuildMap.values()) {
            if (s > 0) {
                s--;
            } else {
                inventory.addItem(paramGuild.icon());
            }
        }
        inventory.setItem(45, ObjectSet.itemStackLastPage);
        inventory.setItem(53, ObjectSet.itemStackNextPage);
        return inventory;
    }

    /**
     *
     * @param paramFile
     * @throws IllegalArgumentException 在解析玩家数据发生数组长度不对等时
     */
    protected PixelGuild(File paramFile) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(paramFile);
        Set<String> keys = configuration.getKeys(false);
        localString = configuration.getString("Name");
        localOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(configuration.getString("Owner")));
        List<String> membersKeyList = configuration.getStringList("Members");
        localUniqueId = UUID.fromString(configuration.getString("UniqueId"));
        localChatChannel = new PixelChatChannel(localString, localUniqueId);
        configuration.getStringList("Members").stream().forEach((paramString) -> {
            String[] array = paramString.split(",");
            localPlayerMap.put(UUID.fromString(array[0]), Grade.valueOf(array[1]));
        });
        PixelWareCollection.localWareCollectionMap.put(localUniqueId,
                configuration.contains("Ware") ? new PixelWareCollection(configuration.getConfigurationSection("Ware"), localUniqueId) : new PixelWareCollection(localUniqueId));
        PixelBank.setBank(localUniqueId, new PixelBank(localUniqueId, configuration.getDouble("Bank")));
        configuration.getStringList("Areas").stream().forEach((paramString) -> {
            localAreaSet.add(UUID.fromString(paramString));
        });
        localStringList = configuration.getStringList("Manifesto");
        icon = Material.valueOf(configuration.getString("Icon.Material"));
        
    }

    protected PixelGuild(String paramString, Player paramPlayer) {
        localString = paramString;
        localOfflinePlayer = paramPlayer;
        localPlayerMap.put(paramPlayer.getUniqueId(), Grade.CAPTAIN);
    }

    protected PixelGuild(String paramString, Player paramPlayer, UUID paramUniqueId) {
        this(paramString, paramPlayer);
        localUniqueId = paramUniqueId;
        icon = Material.IRON_SWORD;
        PixelBank.setBank(localUniqueId, new PixelBank(localUniqueId));
        PixelWareCollection.localWareCollectionMap.put(localUniqueId, new PixelWareCollection(localUniqueId));
        localChatChannel = new PixelChatChannel(paramString, localUniqueId);
    }

    @Override
    public void add(Player paramPlayer) {
        if (PixelGuild.forPlayer(paramPlayer) == null) {
            localPlayerMap.put(paramPlayer.getUniqueId(), Grade.MEMBER);
        }
    }

    @Override
    public void remove(Player paramPlayer) {
        localPlayerMap.remove(paramPlayer.getUniqueId());
    }

    @Override
    public boolean contains(OfflinePlayer paramPlayer) {
        return localPlayerMap.keySet().contains(paramPlayer.getUniqueId()) || localOfflinePlayer == paramPlayer;
    }

    @Override
    public ChatChannel getChatChannel() {
        return localChatChannel;
    }

    @Override
    public String getName() {
        return localString;
    }

    @Override
    public void setName(String paramString) {
        localString = paramString;
    }

    @Override
    public void setOwner(OfflinePlayer paramOfflinePlayer) {
        localOfflinePlayer = paramOfflinePlayer;
    }

    @Override
    public OfflinePlayer getOwner() {
        return localOfflinePlayer;
    }

    @Override
    public Bank bank() {
        return PixelBank.forUniqueId(localUniqueId);
    }

    @Override
    public Map<UUID, Grade> getPlayers() {
        return localPlayerMap;
    }

    @Override
    public ItemStack icon() {
        List<String> listString = new ArrayList();
        listString.add("§a公会名 §7>> " + localString);
        listString.add("§a所有者 §7>> " + localOfflinePlayer.getName());
        listString.add("§a人数 §7>> " + (isFull() ? "§a" : "") + localPlayerMap.size() + "§a/" + ((level * 2) + 15));
        listString.add("§a宣言 §7>>--------------------");
        if (localStringList.isEmpty()) {
            listString.add("§7这个公会没有任何宣言");
        } else {
            listString.addAll(localStringList);
        }
        return Homelessness.core.getReflection().set(new CraftItemStack(icon, "§a公会",
                listString).create(), "uid", localUniqueId.toString());
    }

    @Override
    public void broadcast(String paramString) {
        localPlayerMap.keySet().forEach((paramUniqueId) -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(paramUniqueId);
            if (offlinePlayer.isOnline()) {
                ((Player) offlinePlayer).sendMessage(prefix + paramString + " §7[§b广播§7]");
            }
        });
    }

    @Override
    public WareCollection ware() {
        return PixelWareCollection.localWareCollectionMap.get(localUniqueId);
    }

    @Override
    public View getOptionInterface() {
        View view = new PixelView(localUniqueId);
        view.setItem(1, upgrade);
        view.setItem(2, member);
        view.setItem(3, transfer);
        List<String> recommendation = Lists.newArrayList(localStringList);
        recommendation.add("");
        recommendation.add("§a左键添加§7/§c右键删除");
        view.setItem(19, new CraftItemStack(Material.BOOK_AND_QUILL, "§a编辑公会宣言", recommendation).create());
        view.setItem(10, new CraftItemStack(Material.CAKE, "§a管理入会申请").create());
        view.setItem(7, dissolve);
        return view;
    }

    @Override
    public UUID getUniqueId() {
        return localUniqueId;
    }

    @Override
    public View getView() {
        View view = new PixelView(localUniqueId);
        view.setItem(1, ware().icon());
        view.setItem(2, new CraftItemStack(Material.EMERALD_BLOCK, "§a公会银行", new String[]{"§a余额 §7>> §e" + bank().getBalance()}).create());
        view.setItem(3, new CraftItemStack(Material.WOOL, (short) 14, "§a公会聊天频道", new String[]{"§a目前人数 §7>> §e" + localChatChannel.listAll().size()}).create());
        view.setItem(4, broadcast);
        view.setItem(7, PixelRisker.get(localOfflinePlayer).icon());
        view.setItem(19, new CraftItemStack(Material.BARRIER, "§c退出公会").create());
        view.setItem(25, new CraftItemStack(Material.ANVIL, "§a公会选项").create());
        return view;
    }

    @Override
    public void updateView(Player paramPlayer) {
        PixelView.addView(paramPlayer, "§a公会信息", getView());
    }

    @Override
    public List<String> getManifesto(){
        return localStringList;
    }
    
    @Override
    public void save(File file) {
        try {
            file.createNewFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Flags", flags);
            yaml.set("Name", localString);
            yaml.set("Manifesto", localStringList);
            yaml.set("Owner", localOfflinePlayer.getUniqueId().toString());
            yaml.set("Bank", bank().getBalance());
            List<String> listString0 = new ArrayList<>();
            localPlayerMap.forEach((paramUniqueId, paramGrade) -> {
                listString0.add(paramUniqueId.toString() + "," + paramGrade.toString());
            });
            yaml.set("Members", listString0);
            yaml.set("Icon.Material", icon.toString());
            yaml.set("UniqueId", localUniqueId.toString());
            yaml.set("Ware", PixelWareCollection.localWareCollectionMap.get(localUniqueId).toConfigurationSection());
            List<String> listString1 = new ArrayList<>();
            localAreaSet.stream().forEach((paramUniqueId) -> {
                listString1.add(paramUniqueId.toString());
            });
            yaml.set("Areas", listString1);
            yaml.save(file);
        } catch (IOException ex) {
            Logger.getLogger(PixelGuild.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Set<UUID> getOwnedArea() {
        return localAreaSet;
    }

    @Override
    public Grade getGrade(OfflinePlayer paramOfflinePlayer) {
        return localPlayerMap.get(paramOfflinePlayer.getUniqueId());
    }

    @Override
    public void setGrade(OfflinePlayer paramOfflinePlayer, Grade paramGrade) {
        localPlayerMap.replace(paramOfflinePlayer.getUniqueId(), paramGrade);
    }

    @Override
    public boolean isFull() {
        return localPlayerMap.size() == (level * 2) + 15;
    }

    @Override
    public boolean upgrade() {
        if (bank().getBalance() >= PixelConfiguration.option.getInt("Guild.UpgradeCost")) {
            bank().withdraw(PixelConfiguration.option.getInt("Guild.UpgradeCost"));
            level++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void apply(Player paramPlayer) {
        if (localUniqueIdList.size() >= 36) {
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Guild.ApplicationFull"));
        } else {
            localUniqueIdList.add(paramPlayer.getUniqueId());
            paramPlayer.sendMessage(prefix + PixelConfiguration.getLang("Message.Guild.ApplyJoinGuild"));
        }
    }

    @Override
    public void accpet(UUID paramUniqueId) {
        OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(paramUniqueId);
        localUniqueIdList.remove(paramUniqueId);
        if (PixelGuild.forPlayer(offlineInstance) == null) {
            localPlayerMap.put(paramUniqueId, Grade.MEMBER);
            if (offlineInstance.isOnline()) {
                offlineInstance.getPlayer().sendMessage(prefix + PixelConfiguration.getLang("Message.Guild.JoinGuild"));
            }
        }
    }

    @Override
    public void deny(UUID paramUniqueId) {
        OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(paramUniqueId);
        localUniqueIdList.remove(paramUniqueId);
        if (offlineInstance.isOnline()) {
            offlineInstance.getPlayer().sendMessage(prefix + PixelConfiguration.getLang("Message.Guild.Deny"));
        }
    }

    @Override
    public List<UUID> getApplications() {
        return localUniqueIdList;
    }

    @Override
    public void kick(UUID paramUniqueId) {
        if(localOfflinePlayer.getUniqueId().equals(paramUniqueId)) return;
        if(localPlayerMap.containsKey(paramUniqueId)){
            localPlayerMap.remove(paramUniqueId);
            OfflinePlayer offlineInstance = Bukkit.getOfflinePlayer(paramUniqueId);
            if(offlineInstance.isOnline()){
                Player player = offlineInstance.getPlayer();
                PixelView.removeView(player, "§a公会", "§a公会选项", "§a入会申请列表", "§公会银行", "§a仓库储集", "§a仓库");
                localChatChannel.remove(player);
                player.sendMessage(prefix + PixelConfiguration.getLang("Message.Guild.Kicked").replace("%name", localString));
            }
        }
    }

}
