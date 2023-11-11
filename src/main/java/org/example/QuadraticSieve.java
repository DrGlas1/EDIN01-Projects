package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QuadraticSieve {
    BigInteger N;
    int L;
    int B;
    int F;
    int j = 1;
    int k = 1;
    boolean[] testFactor;
    BigInteger[] values;
    boolean[][] factorMatrix;
    List<Integer> primes = new ArrayList<>();

    public QuadraticSieve(String filepath, BigInteger N) {
        this.N = N;
        this.L = 1000;//(int)Math.round(Math.exp(Math.sqrt(Math.log(N) * Math.log(Math.log(N)))));
        this.B = 500;//(int)Math.round(Math.pow(L, 1.0 / Math.sqrt(2)));
        System.out.println(L + " " + B);
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] primeStrings = line.split(",");
                for (String primeStr : primeStrings){
                    int prime = Integer.parseInt(primeStr);
                    if (prime >= B) {
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
            int solutions = 0;
            while(solutions < L) {
                BigInteger potentialFactor = generatePotentialFactor(solutions);
                boolean f = bSmoothFactors(potentialFactor);
                if (f && validSolution(solutions)) {
                    if (F >= 0) System.arraycopy(testFactor, 0, factorMatrix[solutions], 0, F);
                    solutions++;
                }
                cleanup();
            }
            System.out.println("Solving new matrix");
            pair = new MatrixSolver(factorMatrix, values, N).solve();
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
        for(int i = 0; i < F; i++) {
            int prime = primes.get(i);
            while(n.mod(BigInteger.valueOf(prime)).equals(BigInteger.valueOf(0))) {
                n = n.divide(BigInteger.valueOf(prime));
                testFactor[i] = !testFactor[i];
            }
            nonTrivial |= testFactor[i];
        }
        return n.equals(BigInteger.valueOf(1)) && nonTrivial;
    }

    BigInteger generatePotentialFactor(int solutions) {
        BigInteger r = N.multiply(BigInteger.valueOf(k)).sqrt().add(BigInteger.valueOf(j));
        values[solutions] = r;
        incrementJK();
        return r.multiply(r).mod(N);
    }

    void cleanup() {
        for(int i = 0; i < F; i++) {
            testFactor[i] = false;
        }
    }

    void incrementJK() {
        if (j < k) {
            j++;
        } else {
            k++;
            j = 1;
        }
    }
}
