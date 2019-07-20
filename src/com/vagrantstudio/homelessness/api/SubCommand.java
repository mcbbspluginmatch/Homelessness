/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import org.bukkit.entity.Player;

/**
 *
 * @author BergStudio
 */
public interface SubCommand {
    public abstract boolean onCommand(Player paramPlayer, String[] args);

    public abstract String help();

    public abstract String permission();
}
