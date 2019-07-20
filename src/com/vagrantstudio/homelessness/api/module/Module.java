/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api.module;

import com.vagrantstudio.homelessness.api.SubCommand;
import org.bukkit.event.Listener;

/**
 *
 * @author BergStudio
 */
public abstract interface Module extends SubCommand, Listener{

    /**
     * 启用本模块
     */
    public abstract void enable();

    /**
     * 禁用本模块
     */
    public abstract void disable();

    /**
     * 重载本模块
     * 即保存当前数据到配置文件再初始化模块
     */
    public abstract void reload();
    
    /**
     * 初始化
     * 此举将清空自模块第一次启动以来的所有设置和配置文件
     * 这是不可逆的 如果你想要重加载 请使用 reload();
     */
    public abstract void initialize();
}
