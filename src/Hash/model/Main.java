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

        // --- LOOP ALEATÓRIO ---
        for (int i = 0; i < numInsertions; i++) {
            
            int key = random.nextInt(maxKeyValue) + 1;
            String value = "Valor-" + key;
            
            System.out.println("\n======================================");
            System.out.println("-> Tentando inserir (Aleatório): " + key);
            System.out.println("======================================");

            hash.insert(key, value);
            hash.printStructure();
        }

        System.out.println("\n--- TESTE DE INSERÇÃO MANUAL ---"); 

        
        System.out.println("\n======================================");
        System.out.println("-> Inserindo MANUALMENTE: 100");
        System.out.println("======================================");
        hash.insert(100, "Valor-100");
        hash.printStructure();

        System.out.println("\n======================================");
        System.out.println("-> Inserindo MANUALMENTE: 101");
        System.out.println("======================================");
        hash.insert(101, "Valor-101");
        hash.printStructure();

        System.out.println("\n--- TESTE DE REMOÇÃO MANUAL (TESTANDO MERGE) ---"); 

        
        System.out.println("\n======================================");
        System.out.println("-> Removendo MANUALMENTE: 100");
        System.out.println("======================================");
        hash.remove(100);
        hash.printStructure();
        
        System.out.println("\n======================================");
        System.out.println("-> Removendo MANUALMENTE: 101");
        System.out.println("======================================");
        hash.remove(101); 
        hash.printStructure();
        
        
        System.out.println("\n--- TESTE CONCLUÍDO ---");
        
        System.out.println("\nBuscando chave 10 (pode ser null): " + hash.search(10));
        System.out.println("Buscando chave 101 (deve ser null agora): " + hash.search(101));
    }
}