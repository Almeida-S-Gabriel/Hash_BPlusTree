package Hash.model;

public class DictionaryPair<K extends Comparable<K>, V> implements Comparable<DictionaryPair<K, V>> {
    K key;  
    V value;

    public DictionaryPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(DictionaryPair<K, V> o) {
        return this.key.compareTo(o.key);
    }
}