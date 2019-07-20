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
public enum GameState {
    WAITING("§a等待中"),
    PREPARING("§6准备中"),
    PLAYING("§5游戏中"),
    PASUING("§b暂停中"),
    STOPPED("§4已结束");
    
    private final String state;
    
    private GameState(String s) { state = s; }
    
    public String getState() { return state; }
}
