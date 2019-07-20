/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness;

import com.vagrantstudio.homelessness.api.Access;
import com.vagrantstudio.homelessness.api.View;
import com.vagrantstudio.homelessness.api.util.CraftItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BergStudio
 */
public class ObjectSet {

    protected static final Map<Player, Map.Entry<String, UUID>> localChatActionMap = new HashMap<>();
    protected static final Map<Player, Map.Entry<String, UUID>> localInvActionMap = new HashMap<>();
    protected static final Map<Player, Map.Entry<String, UUID>> localInteractActionMap = new HashMap<>();
    protected static final Map<Player, UUID> localPasswordQueue = new HashMap();

    protected static final View viewConfirm = new PixelView();
    protected static final View viewSetAccess = new PixelView();
    protected static final View viewTask = new PixelView();
    protected static final View viewStartGuild = new PixelView();
    protected static final View viewStartParty = new PixelView();

    protected static final ItemStack itemStackLastPage = new CraftItemStack(Material.PAPER, "§a上一页").create();
    protected static final ItemStack itemStackNextPage = new CraftItemStack(Material.PAPER, "§a下一页").create();
    protected static final ItemStack itemStackHolder = new CraftItemStack(Material.STAINED_GLASS_PANE, (short) 1, " ").create();
    protected static final ItemStack itemStackKickPlayer = new CraftItemStack(Material.SKULL_ITEM, (short) 3, "§a踢出玩家", new String[]{"§7打开玩家列表并选择以踢出玩家"}).create();
    protected static final ItemStack itemStackTask = new CraftItemStack(Material.BOOK, "§a任务委托书", new Enchantment[]{Enchantment.DAMAGE_ALL}, new int[]{1}, new ItemFlag[]{ItemFlag.HIDE_ENCHANTS}).create();

    protected static String inventoryTitle = PixelConfiguration.lang.getString("Inventory.Title").replace("&", "§");

    static {
        viewConfirm.setItem(11, new CraftItemStack(Material.WOOL, (short) 5, "§a我确认该操作").create());
        viewConfirm.setItem(15, new CraftItemStack(Material.WOOL, (short) 14, "§c撤销该操作").create());

        viewSetAccess.setItem(1, Access.PUBLIC.getIcon());
        viewSetAccess.setItem(2, Access.FRIENDLY.getIcon());
        viewSetAccess.setItem(3, Access.PRIVATE.getIcon());

        viewTask.setItem(10, new CraftItemStack(Material.BOOK_AND_QUILL, "§a主线任务", new Enchantment[]{Enchantment.DAMAGE_ALL}, new int[]{1}, new ItemFlag[]{ItemFlag.HIDE_ENCHANTS}).create());
        viewTask.setItem(13, new CraftItemStack(Material.BOOK_AND_QUILL, "§a支线任务").create());
        viewTask.setItem(16, new CraftItemStack(Material.BOOK, "§a普通任务").create());

        viewStartGuild.setItem(20, new CraftItemStack(Material.CHEST, "§a加入一个公会").create());
        viewStartGuild.setItem(24, new CraftItemStack(Material.CHEST, "§a创建一个公会",
                new String[]{"§7需要花费 " + PixelConfiguration.option.getInt("Guild.Cost.Create") + " 元来创建"}).create());
        
        viewStartParty.setItem(20, new CraftItemStack(Material.CHEST, "§a查找组队").create());
        viewStartParty.setItem(24, new CraftItemStack(Material.CHEST, "§a创建组队").create());
    }
}
