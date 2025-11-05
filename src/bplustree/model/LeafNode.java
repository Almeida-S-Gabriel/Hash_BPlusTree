package bplustree.model;

import java.util.Arrays;

public class LeafNode<K extends Comparable<K>, V> extends Node {
    int maxNumPairs;
    int minNumPairs;
    int numPairs;
    LeafNode<K, V> leftSibling;
    LeafNode<K,V> rightSibling;
    DictionaryPair<K, V>[] dictionary;
    
    public void delete(int index) {
      this.dictionary[index] = null;
      numPairs--;
    }

    public boolean insert(DictionaryPair<K,V> dp) {
      if (this.isFull()) {
        return false;
      } else {
        this.dictionary[numPairs] = dp;
        numPairs++;
        Arrays.sort(this.dictionary, 0, numPairs);

        return true;
      }
    }

    @Override
    public boolean isDeficient() {
      return numPairs < minNumPairs;
    }

    public boolean isFull() {
      return numPairs == maxNumPairs;
    }

    @Override
    public boolean isLendable() {
      return numPairs > minNumPairs;
    }

    public boolean isMergeable() {
      return numPairs == minNumPairs;
    }
    
    private int linearNullSearch(DictionaryPair[] dps) {
    for (int i = 0; i < dps.length; i++) {
      if (dps[i] == null) {
        return i;
      }
    }
    return -1;
  }

    public boolean remove(K key) {
      int index = -1;

      for (int i = 0; i < numPairs; i++) {
          if (dictionary[i] != null && dictionary[i].key.compareTo(key) == 0) {
              index = i;
              break;
          }
      }

      if (index == -1) {
          return false;
      }

      for (int i = index; i < numPairs - 1; i++) {
          dictionary[i] = dictionary[i + 1];
      }

      dictionary[numPairs - 1] = null;
      numPairs--;

      return true;
  }





    public LeafNode(int m, DictionaryPair<K,V> dp) {
      this.maxNumPairs = m - 1;
      this.minNumPairs = (int) (Math.ceil(m / 2.0) - 1);
      this.dictionary = (DictionaryPair<K,V>[]) new DictionaryPair[m];
      this.numPairs = 0;
      this.insert(dp);

    }

    public LeafNode(int m, DictionaryPair<K,V>[] dps, InternalNode parent){
      this.maxNumPairs = m - 1;
      this.minNumPairs = (int) (Math.ceil(m / 2.0) - 1);      this.dictionary = dps;
      this.numPairs = linearNullSearch(dps);
      this.parent = parent;
    }


}
