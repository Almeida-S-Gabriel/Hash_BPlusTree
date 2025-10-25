package bplustree.model;

public class DictionaryPair<T> implements Comparable<DictionaryPair<T>> {
    int key;  
    T object;

    public DictionaryPair(T value, int key) {
        this.key = key;
        this.object = value;
    }

    @Override
    public int compareTo(DictionaryPair<T> o) {
        return Integer.compare(this.key, o.key);
    }
}
