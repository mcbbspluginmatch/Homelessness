/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vagrantstudio.homelessness.api;

import java.util.Map;

/**
 *
 * @author BergStudio
 */
public abstract interface Item extends Cloneable{
    
    public abstract void setSort(ItemAttribute.Sort paramSort);
    
    public abstract ItemAttribute.Sort getSort();
    
    public abstract Map<String, Object> elements();
    
    public abstract void setValue(String paramString, Object paramObject);
    
    public abstract Object getValue(String paramString);
    
    public abstract boolean isElement(String paramString);
}
