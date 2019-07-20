/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Retr0
 */
public class PixelGuild implements Guild {

    protected static List<String> flags = Arrays.asList(new String[]{"container", "use", ""});
    protected static String prefix = PixelConfiguration.lang.getString("Message.Prefix.Guild").replace("&", "§");

    private String localString = "§4§lunnamed or unknown name"; //公会名
    private String localString0 = "default"; //默认权限等级
    private OfflinePlayer localOfflinePlayer; //所有者
    private List<OfflinePlayer> localOfflinePlayerList = new ArrayList(); //申请列表
    protected List<String> localStringList = new ArrayList(); //公会公告
    private Map<UUID, String> localPlayerMap = new HashMap(); //公会玩家 K=玩家UUID V=权限等级
    private Map<String, List<String>> localMap = new HashMap<>(); //权限详情 K=权限等级 V=权限列表
    private ItemStack localItemStack; //公会图标
    private UUID localUniqueId = UUID.randomUUID(); //公会UUID
    private ChatChannel localChatChannel; //公会频道
    private Set<UUID> localAreaSet = new HashSet(); //公会领地

    protected static final File localFile = new File("plugins/Homelessness/Guild");
    protected static final Map<UUID, Guild> localGuildMap = new HashMap<>(); //UUID 是公会ID Guild 是公会实例
    protected static final Inventory localInventory = Bukkit.createInventory(null, 9, "§c公会系统功能操作");
    protected static final ItemStack DISSOLVE = new CraftItemStack(Material.BARRIER, "§c解散公会").create();
    protected static final ItemStack BROADCAST = new CraftItemStack(Material.CAKE, "§a公会广播").create();

    static {
        localFile.mkdirs();
        for (File paramFile : localFile.listFiles()) {
            if (paramFile.getName().endsWith(".yml")) {
                localGuildMap.put(UUID.fromString(paramFile.getName().replace(".yml", "")),
                        new PixelGuild(paramFile));
            }
        }
    }
    
    protected static Guild forUniqueId(UUID paramUniqueId){
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
    
    public static void dissolve(UUID paramUniqueId){
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
        localString0 = configuration.getString("Permission.Default");
        ConfigurationSection cs = configuration.getConfigurationSection("Permission.Levels");
        if (cs != null) {
            cs.getKeys(false).stream().forEach((paramString) -> {
                localMap.put(paramString, cs.getStringList("Permission.Levels." + paramString));
            });
        }
        List<String> membersKeyList = configuration.getStringList("Members");
        for (String paramString : membersKeyList) {
            String[] array = paramString.split(",");
            if (array.length != 2) {
                throw new IllegalArgumentException("§c在公会 " + localString + " §c的文件中读取玩家数据第 §e"
                        + membersKeyList.indexOf(paramString) + " §c时发生错误，以 \'|\' 为标识符分析数组时遇到了数组长度不对等的错误");
            } else {
                localPlayerMap.put(UUID.fromString(array[0]), array[1]);
            }
        }
        localUniqueId = UUID.fromString(configuration.getString("UniqueId"));
        localChatChannel = new PixelChatChannel(localString, localUniqueId);
        PixelWareCollection.localWareCollectionMap.put(localUniqueId,
                configuration.contains("Ware") ? new PixelWareCollection(configuration.getConfigurationSection("Ware"), localUniqueId) : new PixelWareCollection(localUniqueId));
        PixelBank.localMap.put(localUniqueId, new PixelBank(localUniqueId, configuration.getDouble("Bank")));
        configuration.getStringList("Areas").stream().forEach((paramString) -> {
            localAreaSet.add(UUID.fromString(paramString));
        });
        localStringList = configuration.getStringList("Manifesto");
        List<String> listString = new ArrayList();
        listString.add("§a公会名 §7>> " + localString);
        listString.add("§a所有者 §7>> " + localOfflinePlayer.getName());
        listString.add("§a宣言 §7>>--------------------");
        if(localStringList.isEmpty()){
            listString.add("§7这个公会没有任何宣言");
        } else {
            localStringList.addAll(localStringList);
        }
        localItemStack = Homelessness.core.getReflection().set(new CraftItemStack(Material.valueOf(configuration.getString("Icon.Material").toUpperCase()), "§a公会",
                listString.toArray(new String[]{})).create(), "uid", localUniqueId.toString());
    }

    protected PixelGuild(String paramString, Player paramPlayer) {
        localString = paramString;
        localOfflinePlayer = paramPlayer;
        localPlayerMap.put(paramPlayer.getUniqueId(), localString0);

        List<String> perms = new ArrayList();
        PixelConfiguration.option.getStringList("Guild.DefaultPermissions").stream().forEach(perms::add);
        localMap.put("default", perms);
    }

    protected PixelGuild(String paramString, Player paramPlayer, UUID paramUniqueId) {
        this(paramString, paramPlayer);
        localUniqueId = paramUniqueId;
        localItemStack = Homelessness.core.getReflection().set(new CraftItemStack(Material.IRON_SWORD, "§a公会",
                new String[]{"§a公会名 §7>> " + localString,
                    "§a所有者 §7>> " + localOfflinePlayer.getName(), "§a宣言 §7>>--------------------", "§7这个公会没有任何宣言"}).create(), "uid", localUniqueId.toString());
        PixelBank.localMap.put(localUniqueId, new PixelBank(localUniqueId));
        PixelWareCollection.localWareCollectionMap.put(localUniqueId, new PixelWareCollection(localUniqueId));
        localChatChannel = new PixelChatChannel(paramString, localUniqueId);
    }

    @Override
    public void add(Player paramPlayer) {
        if (PixelGuild.forPlayer(paramPlayer) == null) {
            localPlayerMap.put(paramPlayer.getUniqueId(), localString0);
        }
    }

    @Override
    public void remove(Player paramPlayer) {
        localOfflinePlayerList.remove(paramPlayer);
    }

    @Override
    public boolean contains(OfflinePlayer paramPlayer) {
        return localPlayerMap.keySet().contains(paramPlayer.getUniqueId()) || localOfflinePlayer == paramPlayer;
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ChatChannel getChatChannel() {
        return localChatChannel;
    }

    @Override
    public void reloadChatChannel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return PixelBank.localMap.get(localUniqueId);
    }

    @Override
    public Map<UUID, String> getPlayers() {
        return localPlayerMap;
    }

    @Override
    public Map<String, List<String>> getLevels() {
        return localMap;
    }

    @Override
    public ItemStack icon() {
        return localItemStack;
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
    public Inventory getOptionInterface() {
        Inventory options = Bukkit.createInventory(null, 54, "§c§l公会选项 §a" + localString);
        options.setItem(4, localItemStack);
        for (int i = 9; i < 18; i++) {
            options.setItem(i, ObjectSet.itemStackHolder);
        }
        options.setItem(18, new CraftItemStack(Material.DIAMOND_BLOCK, "§a扩建公会").create());
        options.setItem(26, new CraftItemStack(Material.BARRIER, "§c解散公会").create());
        return options;
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
        view.setItem(4, BROADCAST);
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
    public void addManifesto(String paramString) {
        localStringList.add(paramString);
    }

    @Override
    public void setManifesto(List<String> paramStringList) {
        localStringList = paramStringList;
    }

    @Override
    public void setManifesto(int paramInteger, String paramString) {
        if (paramString == null) {
            localStringList.remove(paramInteger);
        } else {
            localStringList.set(paramInteger, paramString);
        }
    }

    @Override
    public void clearManifesto() {
        localStringList.clear();
    }

    @Override
    public boolean hasPermission(Player paramPlayer, String paramString) {
        return paramPlayer.getUniqueId().equals(localOfflinePlayer.getUniqueId()) || localMap.get(localPlayerMap.get(paramPlayer.getUniqueId())).contains(paramString);
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
            yaml.set("Permission.Default", localString0);
            localMap.forEach((paramString, paramStringList) -> {
                yaml.set("Permission.Levels." + paramString, paramStringList);
            });
            List<String> listString0 = new ArrayList<>();
            localPlayerMap.forEach((paramUniqueId, paramString) -> {
                listString0.add(paramUniqueId.toString() + "," + paramString);
            });
            yaml.set("Members", listString0);
            yaml.set("Icon.Material", localItemStack.getType().toString());
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

}
