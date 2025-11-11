package Hash.model;

import java.util.Random;

public class Main {

   public static void main(String[] args) {
        
      int bucketCapacity = 3;
        
      int numInsertions = 20;
        
      int maxKeyValue = 50;

        ExtendibleHashing<Integer, String> hash = new ExtendibleHashing<>(bucketCapacity);
        Random random = new Random();

        System.out.println("--- INICIANDO TESTE ---");
        System.out.println("Capacidade do Balde: " + bucketCapacity);
        
        System.out.println("\n=== ESTADO INICIAL ===");
        hash.printStructure();

        for (int i = 0; i < numInsertions; i++) {
            
            int key = random.nextInt(maxKeyValue) + 1;
            String value = "Valor-" + key;
            
            System.out.println("\n======================================");
            System.out.println("-> Tentando inserir: " + key);
            System.out.println("======================================");

            hash.insert(key, value);
            hash.printStructure();
        }

        System.out.println("\n--- TESTE CONCLUÍDO ---");
        
        System.out.println("\nBuscando chave 10 (pode ser null se não foi inserida): " + hash.search(10));
    }
}