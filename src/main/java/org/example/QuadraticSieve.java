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
        this.L = 1024;
        this.B = BigInteger.valueOf(1014);
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

    public void factor() {
        boolean[] testFactor = new boolean[F];
        boolean[][] factorMatrix = new boolean[L][F];
        BigInteger[] values = new BigInteger[L];
        BigInteger j = BigInteger.ONE;
        BigInteger k = BigInteger.ONE;
        FactorPair pair = null;
        while(pair == null) {
            int solutions = 0;
            while(solutions < L && !done) {
                BigInteger potentialFactor = generatePotentialFactor(values, j, k, solutions);
                boolean f = bSmoothFactors(testFactor, potentialFactor);
                if (f && validSolution(testFactor, factorMatrix, solutions)) {
                    if (F >= 0) System.arraycopy(testFactor, 0, factorMatrix[solutions], 0, F);
                    solutions++;
                }
            }
            pair = new MatrixSolver(factorMatrix, values, N).solve();
        }
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
        for(int i = 0; i < F; i++) {
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
