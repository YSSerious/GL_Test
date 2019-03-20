package com.globallogic.test.tree;

import java.util.Set;

public interface ICustomTree<K, V> extends Iterable<K>{
    V get(K key);
    V put(K key, V val);
    V remove(K key);
    int size();
    boolean isEmpty();
    Set<K> keySet();
}
