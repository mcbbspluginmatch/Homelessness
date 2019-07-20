/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api.util;

import java.util.Map;

/**
 *
 * @author BergStudio
 */
public class IMap {
    public static boolean containsKey(Map map, Object obj){
        return map.keySet().stream().anyMatch((object) -> (object.equals(obj)));
    }
}
