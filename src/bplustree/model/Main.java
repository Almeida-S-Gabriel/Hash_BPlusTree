package bplustree.model;

public class Main {
    public static void main(String[] args) {
      
        BBplusTree<Integer, Integer> bPlusTree = new BBplusTree<>(3);
        
        System.out.println("=== INSERINDO VALORES (m=3) ===");
        int[] valores = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int valor : valores) {
            bPlusTree.insert(valor, valor);
        }
        
        System.out.println("\n=== ESTRUTURA INICIAL COMPLETA ===");
        bPlusTree.printTree();
        bPlusTree.printAllLeaves();
        
        System.out.println("\n\n=== REMOVENDO 17 (Remoção Simples) ===");
        bPlusTree.remove(17);
        bPlusTree.printTree();
    
        System.out.println("\n\n=== REMOVENDO 12 (Causa Borrow de Folha) ===");
        bPlusTree.remove(12);
        bPlusTree.printTree();
        
        System.out.println("\n\n=== REMOVENDO 7 (Prepara para o Merge) ===");
        bPlusTree.remove(7);
        bPlusTree.printTree();
        
        System.out.println("\n\n=== REMOVENDO 6 (Causa Merge de Folha) ===");
        bPlusTree.remove(6);
        bPlusTree.printTree();
    
        System.out.println("\n\n=== REMOVENDO 20 (Causa Merge Interno) ===");
        bPlusTree.remove(20);
        bPlusTree.printTree();
        
        System.out.println("\n\n=== ESTADO ATUAL (ANTES DO TESTE FINAL) ===");
        System.out.println("A 'chave morta' 12 está aqui, como 'placa'.");
        bPlusTree.printTree();
        
        // --- AQUI ESTÁ O NOVO TESTE ---
        System.out.println("\n\n=== REMOVENDO 30 (Força o merge de Leaf[10] e Leaf[30], matando a 'placa' 12) ===");
        bPlusTree.remove(30);
        bPlusTree.printTree();
        
        System.out.println("\n\n=== TESTE DE FOLHAS FINAL ===");
        bPlusTree.printAllLeaves();
    }
}