package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuadraticSieveTest {
    @Test
    void initialize() {
        int N = 16637;
        int expectedL = 110;
        int expectedB = 28;
        List<Integer> expectedPrimes = new ArrayList<>(Arrays.asList(2,3,5,7,11,13,17,19,23));
        QuadraticSieve sieve = new QuadraticSieve("./src/test/resources/primes.txt",  N);

        assertEquals(expectedL, sieve.L, "Improper choice of L");
        assertEquals(expectedB, sieve.B, "Improper choice of B");
        assertEquals(expectedPrimes, sieve.primes, "Selected primes not correct");


    }
}