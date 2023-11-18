package org.example;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        QuadraticSieve sieve = new QuadraticSieve("./src/main/java/org/example/primes.txt", new BigInteger("3205837387"));
        int TOTAL = 1;
        for(int i = 0; i < TOTAL; i++) {
            int finalI = i;
            new Thread(() -> {
                sieve.factor(finalI+1, TOTAL);
                System.out.println("Thread " + finalI + " has finished");
            }).start();
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