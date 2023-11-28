package proj1;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        QuadraticSieve sieve = new QuadraticSieve("./src/main/java/proj1/primes.txt", new BigInteger("92434447339770015548544881401"));
        sieve.factor();
        long endTime = System.currentTimeMillis();
        System.out.println("Time to factor: " + (endTime - startTime) + " ms");
    }
}
