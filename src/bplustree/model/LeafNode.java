package bplustree.model;

import java.util.Arrays;

public class LeafNode<T> extends Node {
    int maxNumPairs;
    int minNumPairs;
    int numPairs;
    LeafNode<T> leftSibling;
    LeafNode<T> rightSibling;
    DictionaryPair<T>[] dictionary;
    public void delete(int index) {
      this.dictionary[index] = null;
      numPairs--;
    }

    public boolean insert(DictionaryPair<T> dp) {
      if (this.isFull()) {
        return false;
      } else {
        this.dictionary[numPairs] = dp;
        numPairs++;
        Arrays.sort(this.dictionary, 0, numPairs);

        return true;
      }
    }

    public boolean isDeficient() {
      return numPairs < minNumPairs;
    }

    public boolean isFull() {
      return numPairs == maxNumPairs;
    }

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

    public LeafNode(int m, DictionaryPair<T> dp) {
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
        this.dictionary = (DictionaryPair<T>[]) new DictionaryPair[m];
        this.numPairs = 0;
        this.insert(dp);

    }

    public LeafNode(int m, DictionaryPair[] dps, InternalNode parent){
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
        this.dictionary = dps;
        this.numPairs = linearNullSearch(dps);
        this.parent = parent;
    }


}
