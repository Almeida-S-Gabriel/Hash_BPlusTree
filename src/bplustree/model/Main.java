package bplustree.model;

public class Main {
    public static void main(String[] args) {
        BBplusTree<Integer, Integer> bPlusTree = new BBplusTree<>(3);
        
        System.out.println("=== INSERINDO VALORES ===");
        int[] valores = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int valor : valores) {
            System.out.println("Inserindo: " + valor);
            bPlusTree.insert(valor, valor);
        }
        
        System.out.println("\n=== ESTRUTURA DA B+ TREE ===");
        bPlusTree.printTree();
        
        System.out.println("\n=== TODAS AS FOLHAS EM SEQUÊNCIA ===");
        bPlusTree.printAllLeaves();
        
        System.out.println("\n=== TESTES ADICIONAIS ===");
        
        if (bPlusTree.root instanceof InternalNode) {
            System.out.println("✓ Raiz é InternalNode (correto!)");
        } else {
            System.out.println("✗ Raiz deveria ser InternalNode mas é: " + bPlusTree.root.getClass().getSimpleName());
        }
        
        if (bPlusTree.firstLeaf != null) {
            System.out.println("✓ FirstLeaf existe");
        }
    }
}
