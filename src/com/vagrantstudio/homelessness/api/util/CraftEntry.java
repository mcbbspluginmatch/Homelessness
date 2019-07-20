/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vagrantstudio.homelessness.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @since v0.0.3
 * @author BergStudio
 * @param <K>
 * @param <V>
 */
public class CraftEntry<K, V> implements Map.Entry<K, V> {

    private final K key;
    V value;
    final int hash;

    /**
     * Creates new entry.
     *
     * @param h
     * @param k
     * @param v
     * @param n
     */
    public CraftEntry(int h, K k, V v, Entry<K, V> n) {
        value = v;
        key = k;
        hash = h;
    }

    public CraftEntry(K k, V v) {
        key = k;
        value = v;
        hash = k == null ? 0 : k.hashCode();
    }

    @Override
    public final K getKey() {
        return key;
    }

    @Override
    public final V getValue() {
        return value;
    }

    @Override
    public final V setValue(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        Map.Entry e = (Map.Entry) o;
        Object k1 = getKey();
        Object k2 = e.getKey();
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
            Object v1 = getValue();
            Object v2 = e.getValue();
            if (v1 == v2 || (v1 != null && v1.equals(v2))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return (key == null ? 0 : key.hashCode())
                ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public final String toString() {
        return getKey() + "=" + getValue();
    }

    /**
     * This method is invoked whenever the value in an entry is overwritten by
     * an invocation of put(k,v) for a key k that's already in the HashMap.
     */
    void recordAccess(HashMap<K, V> m) {
    }

    /**
     * This method is invoked whenever the entry is removed from the table.
     */
    void recordRemoval(HashMap<K, V> m) {
    }
}
