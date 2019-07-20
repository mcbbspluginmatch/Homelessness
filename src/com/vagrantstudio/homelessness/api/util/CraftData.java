/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.util;

/**
 *
 * @since v0.0.3
 * @author BergStudio
 * @param <E>
 */
public class CraftData<E> {
    private int localInteger = 2;
    private E[] localArray;
    
    public CraftData(int length, E... params){
        this.localArray = params;
        this.localInteger = length;
    }
    
    public CraftData(int length){
        localArray = (E[]) new Object[length];
    }
    
    public void set(int paramInteger, E paramData){
        if(paramInteger > localInteger) return;
        localArray[paramInteger] = paramData;
    }
    
    public E get(int paramInteger){
        return localArray[paramInteger];
    }
    
    public int length(){
        int i = 0;
        for(E e : localArray){
            if(e != null) i++;
        }
        return i;
    }
    
    public boolean isFull(){ return localInteger == length(); }
}
