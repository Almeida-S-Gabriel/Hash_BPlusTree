package bplustree.model;

public class BBplusTree {
   int m;
   InternalNode root;
   LeafNode firstLeaf;

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
         if(child == null) {
            return findLeafRecursive(internalNode.childPointers[i], key);

         }
      }
    
      return null;
   }

   
}
