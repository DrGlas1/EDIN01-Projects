package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class QuadraticSieve {
    BigInteger N;
    int L;
    BigInteger B;
    int F;
    volatile boolean done = false;
    List<BigInteger> primes = new ArrayList<>();

    public QuadraticSieve(String filepath, BigInteger N) {
        this.N = N;
        this.L = 2000;
        this.B = BigInteger.valueOf(50000);
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
        BigInteger t = BigInteger.valueOf(total);
        boolean[] testFactor = new boolean[F];
        boolean[][] factorMatrix = new boolean[L][F];
        Set<String> existingFactors = new HashSet<>();
        BigInteger[] values = new BigInteger[L];
        BigInteger j = BigInteger.ONE;
        BigInteger k = BigInteger.valueOf(pos);
        FactorPair pair = null;
        while(pair == null && !done) {
            long startTime = System.currentTimeMillis();
            int solutions = 0;
            int p = 0;
            while(solutions < L) {
                BigInteger potentialFactor = generatePotentialFactor(values, j, k, solutions);
                if (k.compareTo(j) > 0) {
                    j = j.add(BigInteger.ONE);
                } else {
                    k = k.add(t);
                    j = BigInteger.ONE;
                }
                //System.out.println("k: " + k + " j: " + j);
                //long nstart = System.nanoTime();
                boolean f = bSmoothFactors(testFactor, potentialFactor);
                //long nmiddle = System.nanoTime();
                //System.out.println("Time to check number: " + (nmiddle - nstart));
                //System.out.println(Arrays.toString(testFactor));

                if (f && validSolution(testFactor, factorMatrix, solutions)) {
                    //System.out.println("Rejected numbers: " + p);
                    p = 0;
                    for(int i = 0; i < F; i++) {
                        factorMatrix[solutions][i] = testFactor[i];
                    }
                    solutions++;
                } else {
                    p++;
                }
                //long nend = System.nanoTime();
                //System.out.println("Time to validate and insert row: " + (nend - nmiddle));
            }
            long middleTime = System.currentTimeMillis();
            System.out.println("Time to create matrix: " + (middleTime - startTime));
//            try {
//                var solver = new MatrixSolver(factorMatrix, values, N);
//                solver.exportMatrixToFile(factorMatrix);
//                solver.solveFile();
//                solver.solve();
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//            pair = new FactorPair(BigInteger.ONE, BigInteger.ONE);
            pair = new MatrixSolver(factorMatrix, values, N).solve();
            long totalTime = System.currentTimeMillis();
            System.out.println("Time to solve matrix: " + (totalTime - middleTime));

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

    BigInteger generatePotentialFactor(BigInteger[] values, BigInteger j, BigInteger k, int solutions) {
        BigInteger r = N.multiply(k).sqrt().add(j);
        values[solutions] = r;
        return r.multiply(r).mod(N);
    }

}
