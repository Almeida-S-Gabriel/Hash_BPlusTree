package bplustree.model;

public class InternalNode extends Node {
   int maxDegree;
   int minDegree;
   int degree;
   InternalNode  leftSibling;
   InternalNode  rightSibling;
   Integer[] keys;
   Node[] childPointers;

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
   
   private int linearNullSearch(Node[] dps) {
    for (int i = 0; i < dps.length; i++) {
      if (dps[i] == null) {
        return i;
      }
    }
    return -1;
  }
 
  public InternalNode (int m, Integer[] keys) {
      this.maxDegree = m;
      this.minDegree = (int) Math.ceil(m / 2.0);
      this.degree = 0;
      this.keys = keys;
      this.childPointers = new Node[this.maxDegree + 1];
    }

  public InternalNode(int m, Integer[] keys, Node[] pointers) {
      this.maxDegree = m;
      this.minDegree = (int) Math.ceil(m / 2.0);
      this.degree = linearNullSearch(pointers);
      this.keys = keys;
      this.childPointers = pointers;
    }
}