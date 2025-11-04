package bplustree.model;

import java.util.Arrays;

class SplitResult<T> {
    Node newNode;  
    int newKey;    
}

public class BBplusTree<T>  {
   int m;
   Node  root;
   LeafNode<T>  firstLeaf;

   private LeafNode findLeafRecursive(Node node, int key) {
      if (node instanceof LeafNode leafNode) {
         return leafNode;
      }

      if (node instanceof InternalNode internalNode) {
         int i = 0;
         while (i < internalNode.degree - 1 && key >= internalNode.keys[i]) {
            i++;
         }

         Node child = internalNode.childPointers[i];
         if(child != null) {
            return findLeafRecursive(child, key);

         }
      }
    
      return null;
   }

   private LeafNode findLeafNode(int key) {
      if (this.root == null) return this.firstLeaf;
         return findLeafRecursive(this.root, key);
   }

   public BBplusTree(int m) {
      this.m = m;
      this.root = null;
      this.firstLeaf = null;
   }

   private LeafNode<T> splitDictionary(LeafNode<T> leaf, DictionaryPair<T> newDP) {
      int total = leaf.numPairs + 1;
      DictionaryPair<T>[] temp = new DictionaryPair[total];
      for (int i = 0; i < leaf.numPairs; i++) temp[i] = leaf.dictionary[i];
      temp[leaf.numPairs] = newDP;

      Arrays.sort(temp, 0, total, (a, b) -> Integer.compare(a.key, b.key));

      int midpoint = total / 2; 

      DictionaryPair<T>[] newDict = (DictionaryPair<T>[]) new DictionaryPair[leaf.maxNumPairs + 1];
      LeafNode<T> newLeaf = new LeafNode<T>(this.m, newDict, (InternalNode) leaf.parent);

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


   private SplitResult<T> splitInternalNode(InternalNode node, int newKey, Node newChild) {
    int oldKeys = node.degree - 1;
    int totalKeys = oldKeys + 1;
    int totalPointers = node.degree + 1;

    Integer[] tempKeys = new Integer[totalKeys];
    Node[] tempPointers = new Node[totalPointers];

    for (int i = 0; i < oldKeys; i++) tempKeys[i] = node.keys[i];
    for (int i = 0; i < node.degree; i++) tempPointers[i] = node.childPointers[i];

    int insertPos = 0;
    while (insertPos < oldKeys && newKey >= tempKeys[insertPos]) insertPos++;

    for (int i = oldKeys - 1; i >= insertPos; i--) tempKeys[i + 1] = tempKeys[i];
    tempKeys[insertPos] = newKey;

    for (int i = node.degree - 1; i >= insertPos + 1; i--) tempPointers[i + 1] = tempPointers[i];
    tempPointers[insertPos + 1] = newChild;

    int midpointIndex = totalKeys / 2; 

    int promoteKey = tempKeys[midpointIndex];

    int leftKeyCount = midpointIndex; 
    int leftPointerCount = leftKeyCount + 1;

    Arrays.fill(node.keys, null);
    Arrays.fill(node.childPointers, null);

    for (int i = 0; i < leftKeyCount; i++) node.keys[i] = tempKeys[i];
    for (int i = 0; i < leftPointerCount; i++) node.childPointers[i] = tempPointers[i];
    node.degree = leftPointerCount;

    Integer[] rightKeys = new Integer[node.maxDegree - 1];
    Node[] rightPointers = new Node[node.maxDegree];

    int rightKeyIndex = 0;
    for (int i = midpointIndex + 1; i < totalKeys; i++) {
        rightKeys[rightKeyIndex++] = tempKeys[i];
    }
    int rightPointerIndex = 0;
    for (int i = midpointIndex + 1; i < totalPointers; i++) {
        rightPointers[rightPointerIndex++] = tempPointers[i];
    }

    InternalNode newNode = new InternalNode(node.maxDegree, rightKeys, rightPointers);
    newNode.parent = node.parent;
    for (int i = 0; i < rightPointerIndex; i++) {
        if (newNode.childPointers[i] != null) newNode.childPointers[i].parent = newNode;
    }

    SplitResult<T> result = new SplitResult<>();
    result.newNode = newNode;
    result.newKey = promoteKey;
    return result;
}

   private void insertIntoInternalNode(InternalNode node, int newKey, Node newChild) {
      int insertPos = 0;
      while (insertPos < node.degree - 1 && newKey >= node.keys[insertPos]) {
         insertPos++;
      }

      for (int i = node.degree - 2; i >= insertPos; i--) {
         node.keys[i + 1] = node.keys[i];
      }
      for (int i = node.degree - 1; i >= insertPos + 1; i--) {
         node.childPointers[i + 1] = node.childPointers[i];
      }

      node.keys[insertPos] = newKey;
      node.childPointers[insertPos + 1] = newChild;
      if (newChild != null) {
         newChild.parent = node;
      }
      node.degree++;
   }

   public void insert(T value, int key) {
    if (this.root == null) {
        LeafNode<T> newLeaf = new LeafNode<T>(m,new DictionaryPair<T>(value, key));
        this.firstLeaf = newLeaf;
        this.root = newLeaf;
        return;
    }

    SplitResult<T> splitResult = insertSplitRecursive(this.root, value, key);
    
    if (splitResult != null) {
        Integer[] rootKeys = new Integer[m - 1];
        Node[] rootPointers = new Node[m];
        
        rootKeys[0] = splitResult.newKey;
        rootPointers[0] = this.root;      
        rootPointers[1] = splitResult.newNode; 
        
        InternalNode newRoot = new InternalNode(m, rootKeys, rootPointers);
        newRoot.degree = 2;
        
        this.root.parent = newRoot;
        if (splitResult.newNode != null) {
            splitResult.newNode.parent = newRoot;
        }
        
        this.root = newRoot;
    }
}

   private SplitResult<T>  insertSplitRecursive(Node root,T obj, int key) {
      if (root instanceof LeafNode) {
        LeafNode<T> leaf = (LeafNode<T>) root;
        if(leaf.insert(new DictionaryPair<T>(obj, key))){
            return null;
         } else {
            LeafNode<T> newLeaf = splitDictionary(leaf,(new DictionaryPair<T>(obj, key)));
            SplitResult<T> splitResult = new SplitResult<T>();
            splitResult.newNode = newLeaf;
            splitResult.newKey = newLeaf.dictionary[0].key;
            return splitResult;
        }
      }

      if (root instanceof InternalNode internalNode) {
         int i = 0;
         while (i < internalNode.degree - 1 && key >= internalNode.keys[i]) {
            i++;
         }

         Node child = internalNode.childPointers[i];
         if(child != null) {
            SplitResult<T> splitResult =  insertSplitRecursive(internalNode.childPointers[i], obj, key);
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
      
      LeafNode<T> current = firstLeaf;
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



