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
        BigInteger factor = BigInteger.valueOf(L).sqrt().subtract(BigInteger.ONE);
        while(pair == null) {
            long startTime = System.currentTimeMillis();
            int solutions = 0;
            while(solutions < L) {
                BigInteger potentialFactor = generatePotentialFactor(values, j, k, solutions);
                if (factor.compareTo(j) == 0) {
                    j = BigInteger.ONE;
                    k = k.add(BigInteger.ONE);
                } else {
                    j = j.add(BigInteger.ONE);
                }
                boolean f = bSmoothFactors(testFactor, potentialFactor);
                if (f && validSolution(testFactor, factorMatrix, solutions)) {
                    if (F >= 0) System.arraycopy(testFactor, 0, factorMatrix[solutions], 0, F);
                    solutions++;
                }
            }
            long middleTime = System.currentTimeMillis();
            System.out.println("Time to create matrix: " + (middleTime - startTime));
            try {
                var solver = new MatrixSolver(factorMatrix, values, N, F);
                solver.exportMatrixToFile(factorMatrix);
                var possibleSolutions = solver.solveFile();
                pair = solver.findFactorPairFromSolution(possibleSolutions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long totalTime = System.currentTimeMillis();
            System.out.println("Time to solve matrix: " + (totalTime - middleTime));
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
