package Hash.model;

import java.util.ArrayList;
import java.util.List;

public class Bucket<K extends Comparable<K>, V> {

    private int localDepth;
    private final int capacity;
    private List<DictionaryPair<K, V>> pair;


    public Bucket(int capacity, int localDepth) {
        this.capacity = capacity;
        this.localDepth = localDepth;
        this.pair = new ArrayList<>(capacity); 
    }


    public boolean insert(DictionaryPair<K, V> par) {
        if (isFull()) {
            return false; 
        }
        pair.add(par);
        return true; 
    }


    public V search(K key) {
        for (DictionaryPair<K, V> par : pair) {
            if (par.key.equals(key)) {
                return par.value;
            }
        }
        return null; 
    }
   
    public boolean remove(K key) {
        DictionaryPair<K, V> toRemove = null;
        for (DictionaryPair<K, V> pair : pair) {
            if (pair.key.equals(key)) {
                toRemove = pair;
                break;
            }
        }
        
        if (toRemove != null) {
            pair.remove(toRemove);
            return true;
        }
        return false;
    }

 
    public boolean isFull() {
        return pair.size() == capacity;
    }

    public boolean isEmpty() {
        return pair.isEmpty();
    }


    public int getLocalDepth() {
        return localDepth;
    }

    public void setLocalDepth(int p) {
        this.localDepth = p;
    }

    public List<DictionaryPair<K, V>> getPair() {
        return pair;
    }
    public void ClearPair() {
        this.pair.clear();
    }

    @Override
    public String toString() {
        return "Balde[p=" + localDepth + ", n=" + pair.size() + "/" + capacity + ", pares=" + pair + "]";
    }
}