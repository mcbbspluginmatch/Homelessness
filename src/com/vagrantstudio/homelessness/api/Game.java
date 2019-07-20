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
public abstract class Game {
    private int maxTime = 600;
    private GameState state = GameState.STOPPED;
    
    public Game(){}
    
    public Game(int time){ maxTime = time; }
    
    public void setMaxTime(int time) { maxTime = time; }
    
    public void setGameState(GameState gs) { state = gs; }
    
    public int getMaxTime() { return maxTime; }
    
    public GameState getState() { return state; }
    
    public boolean isPlaying() { return state == GameState.PLAYING; }
    
    public abstract void start();
    
    public abstract void stop();
    
    public abstract void pause();
    
    public abstract void resume();
}
