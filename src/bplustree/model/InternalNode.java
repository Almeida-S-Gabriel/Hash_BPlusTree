package bplustree.model;

public class InternalNode<K extends Comparable<K>, V> extends Node {
    int maxDegree;
    int minDegree;
    int degree;
    InternalNode<K, V> leftSibling;
    InternalNode<K, V> rightSibling;
    K[] keys;
    Node[] childPointers;
    InternalNode<K, V> parent;

    public boolean isDeficient() {
        return degree < minDegree;
    }

    public boolean isFull() {
        return degree == maxDegree;
    }

    public boolean isLendable() {
        return degree > minDegree;
    }

    public boolean isMergeable() {
        return degree == minDegree;
    }

    private int linearNullSearch(Node[] pointers) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == null) return i;
        }
        return pointers.length;
    }

    public InternalNode(int m, K[] keys) {
        this.maxDegree = m;
        this.minDegree = (int) Math.ceil(m / 2.0);
        this.degree = 0;
        this.keys = keys;
        this.childPointers = new Node[this.maxDegree + 1];
    }

    public InternalNode(int m, K[] keys, Node[] pointers) {
        this.maxDegree = m;
        this.minDegree = (int) Math.ceil(m / 2.0);
        this.degree = linearNullSearch(pointers);
        this.keys = keys;
        this.childPointers = pointers;
    }
}
