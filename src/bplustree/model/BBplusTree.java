package bplustree.model;

import java.util.Arrays;

class SplitResult<K, V> {
    Node newNode;  
    K newKey;    
}

public class BBplusTree<K extends Comparable<K>, V> {
   int m;
   Node  root;
   LeafNode<K, V>  firstLeaf;

   private LeafNode<K, V> findLeafRecursive(Node node, K key) {
      if (node instanceof LeafNode leafNode) {
         return leafNode;
      }

      if (node instanceof InternalNode internalNode) {
         InternalNode<K,V> internal = (InternalNode<K,V>) node;
         int i = 0;
         while (i < internal.degree - 1 && key.compareTo(internal.keys[i]) >= 0) {
            i++;
         }

         Node child = internalNode.childPointers[i];
         if(child != null) {
            return findLeafRecursive(child, key);

         }
      }
    
      return null;
   }

   private LeafNode<K, V> findLeafNode(K key) {
      if (this.root == null) return this.firstLeaf;
         return findLeafRecursive(this.root, key);
   }

   public BBplusTree(int m) {
      this.m = m;
      this.root = null;
      this.firstLeaf = null;
   }

   private LeafNode<K, V> splitDictionary(LeafNode<K, V> leaf, DictionaryPair<K, V> newDP) {
      int total = leaf.numPairs + 1;
      DictionaryPair<K, V>[] temp = new DictionaryPair[total];
      for (int i = 0; i < leaf.numPairs; i++) temp[i] = leaf.dictionary[i];
      temp[leaf.numPairs] = newDP;

      Arrays.sort(temp, 0, total, (a, b) -> a.key.compareTo(b.key));

      int midpoint = total / 2; 

      DictionaryPair<K, V>[] newDict = (DictionaryPair<K, V>[]) new DictionaryPair[leaf.maxNumPairs + 1];
      LeafNode<K, V> newLeaf = new LeafNode<K, V>(this.m, newDict, (InternalNode) leaf.parent);

      for (int i = 0; i < midpoint; i++) {
         leaf.dictionary[i] = temp[i];
      }
      for (int i = midpoint; i < leaf.maxNumPairs + 1; i++) {
         leaf.dictionary[i] = null;
      }
      leaf.numPairs = midpoint;

      for (int i = midpoint; i < total; i++) {
         newLeaf.dictionary[i - midpoint] = temp[i];
         newLeaf.numPairs++;
      }

      newLeaf.rightSibling = leaf.rightSibling;
      if (leaf.rightSibling != null) leaf.rightSibling.leftSibling = newLeaf;
      newLeaf.leftSibling = leaf;
      leaf.rightSibling = newLeaf;

      newLeaf.parent = leaf.parent;
      return newLeaf;
   }


   private SplitResult<K, V> splitInternalNode(InternalNode<K, V> node, K newKey, Node newChild) {
      int oldKeys = node.degree - 1;
      int totalKeys = oldKeys + 1;
      int totalPointers = node.degree + 1;

      K[] tempKeys = (K[]) new Comparable[totalKeys];
      Node[] tempPointers = new Node[totalPointers];


      for (int i = 0; i < oldKeys; i++) tempKeys[i] = node.keys[i];
      for (int i = 0; i < node.degree; i++) tempPointers[i] = node.childPointers[i];

      int insertPos = 0;
      while (insertPos < oldKeys && newKey.compareTo(tempKeys[insertPos]) >= 0) insertPos++;

      for (int i = oldKeys - 1; i >= insertPos; i--) tempKeys[i + 1] = tempKeys[i];
      
      tempKeys[insertPos] = newKey;

      for (int i = node.degree - 1; i >= insertPos + 1; i--) tempPointers[i + 1] = tempPointers[i];
      
      tempPointers[insertPos + 1] = newChild;

      int midpointIndex = totalKeys / 2; 

      K promoteKey = tempKeys[midpointIndex];

      int leftKeyCount = midpointIndex; 
      int leftPointerCount = leftKeyCount + 1;

      Arrays.fill(node.keys, null);
      Arrays.fill(node.childPointers, null);

      for (int i = 0; i < leftKeyCount; i++) node.keys[i] = tempKeys[i];
      for (int i = 0; i < leftPointerCount; i++) node.childPointers[i] = tempPointers[i];
      node.degree = leftPointerCount;

      K[] rightKeys = (K[]) new Comparable[node.maxDegree - 1];
      Node[] rightPointers = new Node[node.maxDegree];

      int rightKeyIndex = 0;
      for (int i = midpointIndex + 1; i < totalKeys; i++) rightKeys[rightKeyIndex++] = tempKeys[i];
   
      int rightPointerIndex = 0;
      for (int i = midpointIndex + 1; i < totalPointers; i++) rightPointers[rightPointerIndex++] = tempPointers[i];
      

      InternalNode<K, V> newNode = new InternalNode<>(node.maxDegree, rightKeys, rightPointers);
      newNode.parent = node.parent;
      for (int i = 0; i < rightPointerIndex; i++) {
         if (newNode.childPointers[i] != null) newNode.childPointers[i].parent = newNode;
      }

      SplitResult<K,V> result = new SplitResult<>();
      result.newNode = newNode;
      result.newKey = promoteKey;
      return result;
}

   private void insertIntoInternalNode(InternalNode<K,V> node, K newKey, Node newChild) {
      int insertPos = 0;
      while (insertPos < node.degree - 1 && newKey.compareTo(node.keys[insertPos]) >= 0) insertPos++;

      for (int i = node.degree - 2; i >= insertPos; i--) node.keys[i + 1] = node.keys[i];
      
      for (int i = node.degree - 1; i >= insertPos + 1; i--) node.childPointers[i + 1] = node.childPointers[i];
      

      node.keys[insertPos] = newKey;
      node.childPointers[insertPos + 1] = newChild;
      if (newChild != null) newChild.parent = node;
   
      node.degree++;
   }

   public void insert(V value, K key) {
      if (this.root == null) {
            LeafNode<K, V> newLeaf = new LeafNode<>(m, new DictionaryPair<>(key, value));
            this.firstLeaf = newLeaf;
            this.root = newLeaf;
            return;
      }

      SplitResult<K, V> splitResult = insertSplitRecursive(this.root, key, value);
    
      if (splitResult != null) {
         K[] rootKeys = (K[]) new Comparable[m - 1];
         Node[] rootPointers = new Node[m];
         
         rootKeys[0] = splitResult.newKey;
         rootPointers[0] = this.root;      
         rootPointers[1] = splitResult.newNode; 
         
         InternalNode newRoot = new InternalNode(m, rootKeys, rootPointers);
         newRoot.degree = 2;
         
         this.root.parent = newRoot;
         if (splitResult.newNode != null) splitResult.newNode.parent = newRoot;
         
         this.root = newRoot;
      }
   }

   private SplitResult<K,V>  insertSplitRecursive(Node root, K key, V value) {
      if (root instanceof LeafNode) {
        LeafNode<K,V> leaf = (LeafNode<K,V>) root;
        if(leaf.insert(new DictionaryPair<K,V>(key, value))){
            return null;
         } else {
            LeafNode<K,V> newLeaf = splitDictionary(leaf,(new DictionaryPair<K,V>(key, value)));
            SplitResult<K,V> splitResult = new SplitResult<K,V>();
            splitResult.newNode = newLeaf;
            splitResult.newKey = newLeaf.dictionary[0].key;
            return splitResult;
        }
      }

      if (root instanceof InternalNode internalNode) {
         InternalNode<K,V> internal = (InternalNode<K,V>) root;
         int i = 0;
         while (i < internal.degree - 1 && key.compareTo(internal.keys[i]) >= 0) {
            i++;
         }

         Node child = internalNode.childPointers[i];
         if(child != null) {
            SplitResult<K,V> splitResult =  insertSplitRecursive(internalNode.childPointers[i], key, value);
            if (splitResult != null) {
               if (internalNode.degree < internalNode.maxDegree) {
                  insertIntoInternalNode(internalNode, splitResult.newKey, splitResult.newNode);
                  return null;
               } else {
                  return splitInternalNode(internalNode, splitResult.newKey, splitResult.newNode);
               }
            }
         }
      }

      return null;
   
   }

   
   private void borrowFromLeftLeaf(InternalNode<K,V> parent, LeafNode<K,V> deficientChild, LeafNode<K,V> leftSibling, int childIndex) {
    
      DictionaryPair<K,V> pairToMove = leftSibling.dictionary[leftSibling.numPairs - 1];
      
      leftSibling.remove(pairToMove.key);
                                          

      for (int i = deficientChild.numPairs - 1; i >= 0; i--) deficientChild.dictionary[i + 1] = deficientChild.dictionary[i];
      

      deficientChild.dictionary[0] = pairToMove;
      deficientChild.numPairs++;
      parent.keys[childIndex - 1] = deficientChild.dictionary[0].key;
   }

   private void borrowFromRightLeaf(InternalNode<K,V> parent, LeafNode<K,V> deficientChild, LeafNode<K,V> rightSibling, int childIndex) {
    
      DictionaryPair<K,V> pairToMove = rightSibling.dictionary[0];
      
      rightSibling.remove(pairToMove.key);
                                          
      deficientChild.dictionary[deficientChild.numPairs] = pairToMove;
      deficientChild.numPairs++;
      parent.keys[childIndex] = rightSibling.dictionary[0].key;
   }  

   private void mergeWithLeftLeaf(InternalNode<K,V> parent, LeafNode<K,V> deficientChild, LeafNode<K,V> leftSibling, int childIndex) {
      
      for (int i = 0; i < deficientChild.numPairs; i++) leftSibling.dictionary[leftSibling.numPairs + i] = deficientChild.dictionary[i];
      
      leftSibling.numPairs += deficientChild.numPairs; 

      leftSibling.rightSibling = deficientChild.rightSibling;
      if (deficientChild.rightSibling != null) {
         deficientChild.rightSibling.leftSibling = leftSibling;
      }

      for (int i = childIndex - 1; i < parent.degree - 2; i++) parent.keys[i] = parent.keys[i + 1];
      
      parent.keys[parent.degree - 2] = null; 

      for (int i = childIndex; i < parent.degree - 1; i++) {
         parent.childPointers[i] = parent.childPointers[i + 1];
      }

      parent.childPointers[parent.degree - 1] = null; 

      parent.degree--;
   }

   
   private void mergeWithRightLeaf(InternalNode<K,V> parent, LeafNode<K,V> deficientChild, LeafNode<K,V> rightSibling, int childIndex) {
      
      for (int i = 0; i < rightSibling.numPairs; i++) {
         deficientChild.dictionary[deficientChild.numPairs + i] = rightSibling.dictionary[i];
      }
      deficientChild.numPairs += rightSibling.numPairs; 

      deficientChild.rightSibling = rightSibling.rightSibling;
      if (rightSibling.rightSibling != null) rightSibling.rightSibling.leftSibling = deficientChild;
      
      
      for (int i = childIndex; i < parent.degree - 2; i++) parent.keys[i] = parent.keys[i + 1];
      
      parent.keys[parent.degree - 2] = null;

      for (int i = childIndex + 1; i < parent.degree - 1; i++) parent.childPointers[i] = parent.childPointers[i + 1];
      
      parent.childPointers[parent.degree - 1] = null;

      parent.degree--;
   }

  
   private void borrowFromLeftInternal(InternalNode<K,V> parent, InternalNode<K,V> deficientChild, InternalNode<K,V> leftSibling, int childIndex) {
      
      
      for (int i = deficientChild.degree - 2; i >= 0; i--) {
         deficientChild.keys[i + 1] = deficientChild.keys[i];
      }
      deficientChild.keys[0] = parent.keys[childIndex - 1]; 

      for (int i = deficientChild.degree - 1; i >= 0; i--) {
         deficientChild.childPointers[i + 1] = deficientChild.childPointers[i];
      }

      deficientChild.childPointers[0] = leftSibling.childPointers[leftSibling.degree - 1];
      
      if (deficientChild.childPointers[0] != null) deficientChild.childPointers[0].parent = deficientChild;
      

 
      parent.keys[childIndex - 1] = leftSibling.keys[leftSibling.degree - 2];
      leftSibling.keys[leftSibling.degree - 2] = null;
      leftSibling.childPointers[leftSibling.degree - 1] = null;
      
      leftSibling.degree--;
      deficientChild.degree++;
   }

   private void borrowFromRightInternal(InternalNode<K,V> parent, InternalNode<K,V> deficientChild, InternalNode<K,V> rightSibling, int childIndex) {

      deficientChild.keys[deficientChild.degree - 1] = parent.keys[childIndex];
      deficientChild.childPointers[deficientChild.degree] = rightSibling.childPointers[0];
      
      if (deficientChild.childPointers[deficientChild.degree] != null) {
         deficientChild.childPointers[deficientChild.degree].parent = deficientChild;
      }

      parent.keys[childIndex] = rightSibling.keys[0];

  
      for (int i = 0; i < rightSibling.degree - 2; i++) {
         rightSibling.keys[i] = rightSibling.keys[i + 1];
      }
      rightSibling.keys[rightSibling.degree - 2] = null;
      
      for (int i = 0; i < rightSibling.degree - 1; i++) {
         rightSibling.childPointers[i] = rightSibling.childPointers[i + 1];
      }
      rightSibling.childPointers[rightSibling.degree - 1] = null;

      deficientChild.degree++;
      rightSibling.degree--;
   }


 
   private void mergeWithLeftInternal(InternalNode<K,V> parent, InternalNode<K,V> deficientChild, InternalNode<K,V> leftSibling, int childIndex) {
    
      K separatorKey = parent.keys[childIndex - 1];
      leftSibling.keys[leftSibling.degree - 1] = separatorKey;
      
      for (int i = 0; i < deficientChild.degree - 1; i++) {
         leftSibling.keys[leftSibling.degree + i] = deficientChild.keys[i];
      }

      for (int i = 0; i < deficientChild.degree; i++) {
         leftSibling.childPointers[leftSibling.degree + i] = deficientChild.childPointers[i];
         
         if (deficientChild.childPointers[i] != null) {
               deficientChild.childPointers[i].parent = leftSibling;
         }
      }

      leftSibling.degree += deficientChild.degree;

      for (int i = childIndex - 1; i < parent.degree - 2; i++) {
         parent.keys[i] = parent.keys[i + 1];
      }
      parent.keys[parent.degree - 2] = null;

      for (int i = childIndex; i < parent.degree - 1; i++) {
         parent.childPointers[i] = parent.childPointers[i + 1];
      }
      parent.childPointers[parent.degree - 1] = null;

      parent.degree--;
   }

   private void mergeWithRightInternal(InternalNode<K,V> parent, InternalNode<K,V> deficientChild, InternalNode<K,V> rightSibling, int childIndex) {
      
      K separatorKey = parent.keys[childIndex];
      deficientChild.keys[deficientChild.degree - 1] = separatorKey;
      
      for (int i = 0; i < rightSibling.degree - 1; i++) {
         deficientChild.keys[deficientChild.degree + i] = rightSibling.keys[i];
      }

      for (int i = 0; i < rightSibling.degree; i++) {
         deficientChild.childPointers[deficientChild.degree + i] = rightSibling.childPointers[i];
         
         if (rightSibling.childPointers[i] != null) {
               rightSibling.childPointers[i].parent = deficientChild;
         }
      }

      deficientChild.degree += rightSibling.degree;

      
      for (int i = childIndex; i < parent.degree - 2; i++) {
         parent.keys[i] = parent.keys[i + 1];
      }
      parent.keys[parent.degree - 2] = null;

      for (int i = childIndex + 1; i < parent.degree - 1; i++) {
         parent.childPointers[i] = parent.childPointers[i + 1];
      }
      parent.childPointers[parent.degree - 1] = null;

      parent.degree--;
   }

   public void remove(K key) {
      if (this.root == null) {
         return; 
      }

      deleteRecursive(this.root, key);
   
      if (this.root instanceof InternalNode && ((InternalNode<K,V>)this.root).degree == 1) {
         Node newRoot = ((InternalNode<K,V>)this.root).childPointers[0];
         if (newRoot != null) {
               newRoot.parent = null;
         }
         this.root = newRoot;
      }
   }

   private void handleDeficiency(InternalNode<K,V> parent, int childIndex) {
    
      Node deficientChild = parent.childPointers[childIndex];
      
      if (childIndex > 0) { 
         Node leftSibling = parent.childPointers[childIndex - 1];
         if (leftSibling.isLendable()) {
               if (deficientChild instanceof LeafNode) {
                  borrowFromLeftLeaf(parent, (LeafNode<K,V>)deficientChild, (LeafNode<K,V>)leftSibling, childIndex);
               } else {
                  borrowFromLeftInternal(parent, (InternalNode<K,V>)deficientChild, (InternalNode<K,V>)leftSibling, childIndex);
               }
               return; 
         }
      }
      
      if (childIndex < parent.degree - 1) { 
         Node rightSibling = parent.childPointers[childIndex + 1];
         if (rightSibling.isLendable()) {
               if (deficientChild instanceof LeafNode) {
                  borrowFromRightLeaf(parent, (LeafNode<K,V>)deficientChild, (LeafNode<K,V>)rightSibling, childIndex);
               } else {
                  borrowFromRightInternal(parent, (InternalNode<K,V>)deficientChild, (InternalNode<K,V>)rightSibling, childIndex);
               }
               return; 
         }
      }

      if (childIndex > 0) {
         Node leftSibling = parent.childPointers[childIndex - 1];
         if (deficientChild instanceof LeafNode) {
               mergeWithLeftLeaf(parent, (LeafNode<K,V>)deficientChild, (LeafNode<K,V>)leftSibling, childIndex);
         } else {
               mergeWithLeftInternal(parent, (InternalNode<K,V>)deficientChild, (InternalNode<K,V>)leftSibling, childIndex);
         }
      } else {

            Node rightSibling = parent.childPointers[childIndex + 1];
            if (deficientChild instanceof LeafNode) {
                  mergeWithRightLeaf(parent, (LeafNode<K,V>)deficientChild, (LeafNode<K,V>)rightSibling, childIndex);
            } else {
                  mergeWithRightInternal(parent, (InternalNode<K,V>)deficientChild, (InternalNode<K,V>)rightSibling, childIndex);
            }
      }
   }

   

   private void deleteRecursive(Node node, K key) {
    
    if (node instanceof LeafNode) {
        LeafNode<K,V> leaf = (LeafNode<K,V>) node;
        
        leaf.remove(key);
        return;
    }
    
    if (node instanceof InternalNode) {
        InternalNode<K,V> internal = (InternalNode<K,V>) node;
        
        int i = 0;
        while (i < internal.degree - 1 && key.compareTo(internal.keys[i]) >= 0) {
            i++;
        }
        
        Node child = internal.childPointers[i];

        if (child == null) {
            return;
        }

        deleteRecursive(child, key);
        
        if (child.isDeficient()) {
            handleDeficiency(internal, i);
        }
    }
}

   public void printTree() {
      if (root == null) {
         System.out.println("Árvore vazia");
         return;
      }
      printNode(root, 0);
   }

   private void printNode(Node node, int level) {
      String indent = "  ".repeat(level);
      
      if (node instanceof LeafNode leaf) {
         System.out.print(indent + "Leaf[");
         for (int i = 0; i < leaf.numPairs; i++) {
               System.out.print(leaf.dictionary[i].key);
               if (i < leaf.numPairs - 1) System.out.print(", ");
         }
         System.out.println("]");
      } else if (node instanceof InternalNode internal) {
         System.out.print(indent + "Internal[");
         for (int i = 0; i < internal.degree - 1; i++) {
               System.out.print(internal.keys[i]);
               if (i < internal.degree - 2) System.out.print(", ");
         }
         System.out.println("]");
         
         for (int i = 0; i < internal.degree; i++) {
               if (internal.childPointers[i] != null) {
                  printNode(internal.childPointers[i], level + 1);
               }
         }
      }
   }

   public void printAllLeaves() {
      System.out.println("=== TODAS AS FOLHAS EM SEQUÊNCIA ===");
      if (firstLeaf == null) {
         System.out.println("Nenhuma folha encontrada");
         return;
      }
      
      LeafNode<K,V> current = firstLeaf;
      int leafCount = 0;
      
      while (current != null) {
         System.out.print("Folha " + leafCount + ": [");
         for (int i = 0; i < current.numPairs; i++) {
               if (current.dictionary[i] != null) {
                  System.out.print(current.dictionary[i].key);
                  if (i < current.numPairs - 1) System.out.print(", ");
               }
         }
         System.out.println("]");
         
         current = current.rightSibling;
         leafCount++;
      }
   }



}



