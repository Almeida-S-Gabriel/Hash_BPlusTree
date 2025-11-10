package Hash.model;

import java.util.ArrayList;
import java.util.List;

public class ExtendibleHashing<K extends Comparable<K>, V> {

   private int globalDepth;
   private final int bucketCapacity;
   private ArrayList<Bucket<K, V>> directory;
   public ExtendibleHashing(int bucketCapacity) {
      this.bucketCapacity = bucketCapacity;
      this.globalDepth = 1;

      this.directory = new ArrayList<>();
         
      Bucket<K, V> b0 = new Bucket<>(bucketCapacity, 1);
      Bucket<K, V> b1 = new Bucket<>(bucketCapacity, 1);
         
      directory.add(b0);
      directory.add(b1);
   }   

   private int getDirectoryIndex(K key) {
      int hash = key.hashCode();
      int mask = (1 << globalDepth) - 1;
      return hash & mask; 
   }   

   public V search(K key) {
      int index = getDirectoryIndex(key);
      Bucket<K, V> bucket = directory.get(index);
      return bucket.search(key);
   }

    
   public void insert(K key, V value) {
      DictionaryPair<K, V> pair = new DictionaryPair<>(key, value);
      int index = getDirectoryIndex(key);
      Bucket<K, V> bucket = directory.get(index);

      if (bucket.insert(pair)) {
         return;
      }
      
      int localDepth = bucket.getLocalDepth();

      if (localDepth == globalDepth) {
         doubleDirectory();
      }

      
      int newIndex = getDirectoryIndex(key);
      Bucket<K, V> bucketToSplit = directory.get(newIndex);  
      splitBucket(newIndex, bucketToSplit);

      insert(key, value); 
   }

  
   private void doubleDirectory() {
      int oldSize = directory.size();
      for (int i = 0; i < oldSize; i++) {
         directory.add(directory.get(i));
      }
        
      this.globalDepth++;
   }

   
   private void splitBucket(int bucketIndex, Bucket<K, V> fullBucket) {
        
      List<DictionaryPair<K, V>> oldPairs = new ArrayList<>(fullBucket.getPair());
      fullBucket.ClearPair(); 


      fullBucket.setLocalDepth(fullBucket.getLocalDepth() + 1);
      int newLocalDepth = fullBucket.getLocalDepth();
      Bucket<K, V> newBucket = new Bucket<>(bucketCapacity, newLocalDepth);

      int pairIndex = bucketIndex ^ (1 << (newLocalDepth - 1));
      int stride = 1 << newLocalDepth;

      for (int i = pairIndex; i < directory.size(); i += stride) {
            directory.set(i, newBucket);
      }
      for (DictionaryPair<K, V> pair : oldPairs) {
         insert(pair.key, pair.value);
      }
   }
    
   
   public boolean remove(K key) {
      int index = getDirectoryIndex(key);
      Bucket<K, V> bucket = directory.get(index);
      return bucket.remove(key);
    }

  
   public void printStructure() {
      System.out.println("--- Hashing Extensível ---");
      System.out.println("Global Depth: " + globalDepth);
      System.out.println("Directory Size: " + directory.size());
      System.out.println("Directory:");
        
      List<Bucket<K, V>> printedBuckets = new ArrayList<>();
        
      for (int i = 0; i < directory.size(); i++) {
         Bucket<K, V> bucket = directory.get(i);
         String binIndex = Integer.toBinaryString(i);
         while(binIndex.length() < globalDepth) {
            binIndex = "0" + binIndex;
         }
            
         System.out.print("  Index " + binIndex + " (hash " + i + ") -> ");
            
         if(!printedBuckets.contains(bucket)) {
               System.out.println("Bucket " + bucket.toString());
               printedBuckets.add(bucket);
            } else {
               System.out.println("... (ponteiro para balde já mostrado)");
            }
        }

        System.out.println("-------------------------");
    }
}