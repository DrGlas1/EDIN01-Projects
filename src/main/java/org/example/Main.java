package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        QuadraticSieve sieve = new QuadraticSieve("./src/main/java/org/example/primes.txt", 16637);
        MatrixSolver solver = sieve.generateMatrix();
        List<boolean[]> matrix = solver.solve();
        for(boolean[] row : matrix) {
            for(boolean val : row) {
                int p = val ? 1 : 0;
                System.out.print(p + " ");
            }
            System.out.println();
        }
    }

    static void printRow(boolean[] row) {
        for(boolean p : row) {
            int v = p ? 1 : 0;
            System.out.print(v + " ");
        }
        System.out.println();
    }

}