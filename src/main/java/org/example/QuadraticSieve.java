package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class QuadraticSieve {
    BigInteger N;
    int L;
    BigInteger B;
    int F;
    volatile boolean done = false;
    List<BigInteger> primes = new ArrayList<>();

    public QuadraticSieve(String filepath, BigInteger N) {
        this.N = N;
        this.L = 2000;//(int)Math.round(Math.exp(Math.sqrt(Math.log(N) * Math.log(Math.log(N)))));
        this.B = BigInteger.valueOf(1500);//(int)Math.round(Math.pow(L, 1.0 / Math.sqrt(2)));
        System.out.println(L + " " + B);
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] primeStrings = line.split(",");
                for (String primeStr : primeStrings){
                    var prime = new BigInteger(primeStr);
                    if (prime.compareTo(B) >= 0) {
                        break;
                    }
                    primes.add(prime);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.F = primes.size();
    }

    public void factor(long pos, long total) {
        BigInteger val = BigInteger.valueOf(500);
        BigInteger tot = BigInteger.valueOf(500 * total);
        boolean[] testFactor = new boolean[F];
        boolean[][] factorMatrix = new boolean[L][F];
        BigInteger[] values = new BigInteger[L];
        BigInteger j = BigInteger.valueOf(1).add(val.multiply(BigInteger.valueOf(pos)));
        BigInteger k = j;
        FactorPair pair = null;
        while(pair == null) {
            long startTime = System.currentTimeMillis();
            int solutions = 0;
            while(solutions < L && !done) {
                BigInteger potentialFactor = generatePotentialFactor(values, j, k, solutions);
                if (k.compareTo(j) > 0) {
                    j = j.add(BigInteger.ONE);
                } else if (k.mod(val).equals(BigInteger.ZERO)) {
                    k = k.add(tot).subtract(BigInteger.valueOf(500));
                    j = BigInteger.valueOf(1);
                } else {
                    k = k.add(BigInteger.ONE);
                    j = BigInteger.valueOf(1);
                }
                //long nstart = System.nanoTime();
                boolean f = bSmoothFactors(testFactor, potentialFactor);
                //long nmiddle = System.nanoTime();
                //System.out.println("Time to check number: " + (nmiddle - nstart));
                if (f && validSolution(testFactor, factorMatrix, solutions)) {
                    for(int i = 0; i < F; i++) {
                        factorMatrix[solutions][i] = testFactor[i];
                    }
                    solutions++;
                }
                //long nend = System.nanoTime();
                //System.out.println("Time to validate and insert row: " + (nend - nmiddle));
            }
            long middleTime = System.currentTimeMillis();
            System.out.println("Time to create matrix: " + (middleTime - startTime));
            pair = new MatrixSolver(factorMatrix, values, N).solve();
            //long totalTime = System.currentTimeMillis();
            //System.out.println("Time to solve matrix: " + (totalTime - middleTime));

        }
        done = true;
        pair.print();
    }

    boolean validSolution(boolean[] testFactor, boolean[][] factorMatrix, int solutions) {
        for(int i = 0; i < solutions; i++) {
            for(int j = 0; j < F; j++) {
                if (testFactor[j] != factorMatrix[i][j]) {
                    break;
                }
                if (j == F - 1) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean bSmoothFactors(boolean[] testFactor, BigInteger n) {
        boolean nonTrivial = false;
        /// Nedan kod skulle kunna paraleliseras och sedan multipliceras ihop för att ta reda om talet är b smooth
        for(int i = 0; i < F; i++) { // F is the number of primes
            testFactor[i] = false;
            BigInteger prime = primes.get(i);
            while(n.mod(prime).equals(BigInteger.ZERO)) {
                n = n.divide(prime);
                testFactor[i] = !testFactor[i];
            }
            nonTrivial |= testFactor[i];
        }
        return n.equals(BigInteger.ONE) && nonTrivial;
    }

    boolean bSmoothFactors2(BigInteger n) {
        boolean[] testFactor = new boolean[F];
        ForkJoinPool.commonPool()
                .submit(() ->
                        primes.parallelStream()
                                .map(prime -> {
                                    int index = primes.indexOf(prime);
                                    BigInteger m = n;
                                    while (m.mod(prime).equals(BigInteger.valueOf(0))) {
                                        m = m.divide(prime);
                                        testFactor[index] = !testFactor[index];
                                    }
                                    return testFactor[index];
                                })
                ).join();

        return true; // TODO
    }

    BigInteger generatePotentialFactor(BigInteger[] values, BigInteger j, BigInteger k, int solutions) {
        BigInteger r = N.multiply(k).sqrt().add(j);
        values[solutions] = r;
        return r.multiply(r).mod(N);
    }

}
