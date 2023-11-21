package org.example;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        QuadraticSieve sieve = new QuadraticSieve("./src/main/java/org/example/primes.txt", new BigInteger("392742364277"));
        sieve.factor(0, 5);
    }

    static void printRow(boolean[] row) {
        for(boolean p : row) {
            int v = p ? 1 : 0;
            System.out.print(v + " ");
        }
        System.out.println();
    }

}