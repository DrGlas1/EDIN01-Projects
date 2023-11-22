package proj1;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        QuadraticSieve sieve = new QuadraticSieve("./src/main/java/proj1/primes.txt", new BigInteger("208863203872858491183629"));
        sieve.factor();
    }
}
