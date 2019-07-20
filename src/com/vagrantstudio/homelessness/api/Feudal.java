/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api;

/**
 *
 * @author BergStudio
 */
public enum Feudal {

    PLAYER("§a玩家"),
    GUILD("§a公会"),
    NOT_AVAILABLE("不可用");
    
    private final String name;
    
    private Feudal(String s){ name = s; }
    
    public String getName(){ return name; }
}
