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
    BigInteger j = BigInteger.valueOf(1);
    BigInteger k = BigInteger.valueOf(1);
    boolean[] testFactor;
    BigInteger[] values;
    boolean[][] factorMatrix;
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
        this.testFactor = new boolean[F];
        this.factorMatrix = new boolean[L][F];
        this.values = new BigInteger[L];
    }

    public void factor() {
        FactorPair pair = null;
        while(pair == null) {
            long startTime = System.currentTimeMillis();
            int solutions = 0;
            while(solutions < L) {
                BigInteger potentialFactor = generatePotentialFactor(solutions);
                //long nstart = System.nanoTime();
                boolean f = bSmoothFactors(potentialFactor);
                //long nmiddle = System.nanoTime();
                //System.out.println("Time to check number: " + (nmiddle - nstart));
                if (f && validSolution(solutions)) {
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
        pair.print();
    }

    boolean validSolution(int solutions) {
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

    boolean bSmoothFactors(BigInteger n) {
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

    BigInteger generatePotentialFactor(int solutions) {
        BigInteger r = N.multiply(k).sqrt().add(j);
        values[solutions] = r;
        incrementJK();
        return r.multiply(r).mod(N);
    }


    void incrementJK() {
        if (k.compareTo(j) > 0) {
            j = j.add(BigInteger.ONE);
        } else {
            k = k.add(BigInteger.ONE);
            j = BigInteger.valueOf(1);
        }
    }
}
