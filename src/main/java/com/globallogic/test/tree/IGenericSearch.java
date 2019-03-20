package com.globallogic.test.tree;

public interface IGenericSearch<K, V> {
    boolean containsKey(K key);
    boolean containsValue(V val);
}
