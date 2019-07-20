/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BergStudio
 */
public enum Attribute {
    WEAR,
    ON_HAND,
    IN_BAG_SLOT,
    CLICK,
    DROP,
    PICK_UP;
    
    private static final List<String> localAttributeList = new ArrayList();
    
    static{
        for(Attribute paramAttribute : Attribute.values()){
            localAttributeList.add(paramAttribute.toString());
        }
    }
    
    private Attribute(){
        
    }
    
    public static Attribute getAttribute(String paramString){
        String string = paramString.toUpperCase();
        if(localAttributeList.contains(string)){
            return Attribute.valueOf(string);
        } else {
            return null;
        }
    }
}
