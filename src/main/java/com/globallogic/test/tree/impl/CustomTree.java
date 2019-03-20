package com.globallogic.test.tree.impl;

import com.globallogic.test.tree.ICustomTree;
import com.globallogic.test.tree.IGenericSearch;
import com.globallogic.test.tree.exceptions.EmptyKeyException;

import java.util.*;

public class CustomTree<K, V> implements ICustomTree<K, V>, IGenericSearch<K, V> {

    private int size;
    private Node<K, V> root;
    private Comparator<? super K> comparator;
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    public CustomTree() {
    }

    @Override
    public V get(K key) {
        if (key == null)
            throw new EmptyKeyException("Key is null!");

        Node<K, V> current = root;
        while (current != null) {
            int c = compare(key, current.key);
            if (c < 0) {
                current = current.left;
            } else if (c > 0) {
                current = current.right;
            } else {
                return current.value;
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (key == null)
            throw new EmptyKeyException("Key is null!");
        root = put(root, key, value);
        root.color = BLACK;
        size++;
        return value;
    }

    private Node<K, V> put(Node<K, V> node, K key, V value) {
        if (node == null)
            return new Node<>(key, value, RED);

        int c = compare(key, node.key);
        if (c < 0) node.left = put(node.left, key, value);
        else if (c > 0)
            node.right = put(node.right, key, value);
        else
            node.value = value;

        // fix-up any right-leaning links
        if (isNodeRed(node.right) && !isNodeRed(node.left))
            node = rotateLeft(node);
        if (isNodeRed(node.left) && isNodeRed(node.left.left))
            node = rotateRight(node);
        if (isNodeRed(node.left) && isNodeRed(node.right))
            flipColors(node);
        return node;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null)
            throw new NullPointerException("The given key is null.");

        Node<K, V> current = root;
        while (current != null) {
            int c = compare(key, current.key);
            if (c < 0) {
                current = current.left;
            } else if (c > 0) {
                current = current.right;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> result = new HashSet<>();
        keySet(root, result);
        return result;
    }

    private void keySet(Node<K, V> node, Set<K> result) {
        if (node == null)
            return;
        keySet(node.left, result);
        result.add(node.key);
        keySet(node.right, result);
    }

    @Override
    public boolean containsValue(V val) {
        return containsValue(root, val);
    }

    private boolean containsValue(Node<K, V> node, V val) {
        if (node == null)
            return false;
        if (((val != null) && (val.equals(node.value))) || ((val == null) && (node.value == null))) {
            return true;
        }
        return containsValue(node.left, val) || containsValue(node.right, val);
    }

    @Override
    public V remove(K key) {
        if (key == null)
            throw new NullPointerException("The given key is null.");
        if (containsKey(key)) {
            V val = get(key);

            // if both children of root are black, set root to red
            if (!isNodeRed(root.left) && !isNodeRed(root.right))
                root.color = RED;

            root = remove(root, key);
            size--;
            if (!isEmpty())
                root.color = BLACK;
            return val;
        }
        return null;
    }

    private Node<K, V> remove(Node<K, V> node, K key) {
        if (compare(key, node.key) < 0) {
            if (!isNodeRed(node.left) && !isNodeRed(node.left.left))
                node = moveRedLeft(node);
            node.left = remove(node.left, key);
        } else {
            if (isNodeRed(node.left))
                node = rotateRight(node);
            if (compare(key, node.key) == 0 && (node.right == null))
                return null;
            if (!isNodeRed(node.right) && !isNodeRed(node.right.left))
                node = moveRedRight(node);
            if (compare(key, node.key) == 0) {
                Node<K, V> x = min(node.right);
                node.key = x.key;
                node.value = x.value;
                node.right = removeMin(node.right);
            } else
                node.right = remove(node.right, key);
        }
        return balance(node);
    }

    private Node<K, V> balance(Node<K, V> node) {
        if(node != null){
            if (isNodeRed(node.right))
                node = rotateLeft(node);
            if (isNodeRed(node.left) && isNodeRed(node.left.left))
                node = rotateRight(node);
            if (isNodeRed(node.left) && isNodeRed(node.right))
                flipColors(node);
        }
        return node;
    }

    private Node<K, V> moveRedRight(Node<K, V> node) {
        if(isNodeRed(node) && !isNodeRed(node.right) && !isNodeRed(node.right.left)){
            flipColors(node);
            if (isNodeRed(node.left.left)) {
                node = rotateRight(node);
                flipColors(node);
            }
        }
        return node;
    }

    private Node<K, V> moveRedLeft(Node<K, V> node) {
        if(isNodeRed(node) && !isNodeRed(node.left) && !isNodeRed(node.left.left)){
        flipColors(node);
        if (isNodeRed(node.right.left)) {
            node.right = rotateRight(node.right);
            node = rotateLeft(node);
            flipColors(node);
        }
        }
        return node;
    }

    private Node<K, V> min(Node<K, V> node) {
        if (node.left == null)
            return node;
        else
            return min(node.left);
    }

    private Node<K, V> removeMin(Node<K, V> node) {
        if (node.left == null)
            return null;

        if (!isNodeRed(node.left) && !isNodeRed(node.left.left))
            node = moveRedLeft(node);

        node.left = removeMin(node.left);
        return balance(node);
    }

    private int compare(K key1, K key2) {
        try {
            return ((Comparable) key1).compareTo(key2);
        } catch (ClassCastException e) {
            throw new ClassCastException("You should use only comparable key.");
        }
    }

    private boolean isNodeRed(Node<K, V> node) {
        if (node == null)
            return false;
        return node.color == RED;
    }

    private Node<K, V> rotateLeft(Node<K, V> h) {
        Node<K, V> node = h.right;
        if (isNodeRed(h.right)) {
            h.right = node.left;
            node.left = h;

            node.color = node.left.color;
            node.left.color = RED;
        }
        return node;
    }

    private Node<K, V> rotateRight(Node<K, V> h) {
        Node<K, V> node = h.left;
        if (isNodeRed(h.left)) {
            h.left = node.right;
            node.right = h;
            node.color = node.right.color;
            node.right.color = RED;
        }
        return node;
    }

    private void flipColors(Node<K, V> node) {
        if((!isNodeRed(node) && isNodeRed(node.left) && isNodeRed(node.right)) || (isNodeRed(node) && !isNodeRed(node.left) && !isNodeRed(node.right))){
            node.color = !node.color;
            node.left.color = !node.left.color;
            node.right.color = !node.right.color;
        }
    }

    static class Node<K, V> {
        K key;
        V value;
        boolean color;
        Node<K, V> left;
        Node<K, V> right;

        public Node(K key, V value, boolean col) {
            this.key = key;
            this.value = value;
            this.color = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(key, node.key) &&
                    Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    public Iterator iterator() {
        return new CustomIterator();
    }

    private class CustomIterator implements Iterator<K> {

        private int index;
        private K[] keys;

        CustomIterator() {
            index = 0;
            keys = (K[]) new Object[size];
            InorderTraversal(root, keys, 0);
        }

        private int InorderTraversal(Node<K, V> node, K[] keys, int currIndex) {
            if (node == null)
                return currIndex;
            int indexLeft = InorderTraversal(node.left, keys, currIndex);
            keys[indexLeft] = node.key;
            indexLeft++;
            return InorderTraversal(node.right, keys, indexLeft);
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public K next() {
            return keys[index++];
        }

    }
}
