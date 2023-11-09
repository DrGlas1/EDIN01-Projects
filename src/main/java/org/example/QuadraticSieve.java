package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuadraticSieve {
    int N;
    int L;
    int B;
    int F;
    int j = 1;
    int k = 1;
    boolean[] testFactor;
    int[] values;
    boolean[][] factorMatrix;
    List<Integer> primes = new ArrayList<>();

    public QuadraticSieve(String filepath, int N) {
        this.N = N;
        this.L = 12;//(int)Math.round(Math.exp(Math.sqrt(Math.log(N) * Math.log(Math.log(N)))));
        this.B = 30; //(int)Math.round(Math.pow(L, 1.0 / Math.sqrt(2)));
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
        this.values = new int[L];
    }

    public MatrixSolver generateMatrix() {
        int solutions = 0;
        while(solutions < L) {
            int potentialFactor = generatePotentialFactor(solutions);
            boolean f = bSmoothFactors(potentialFactor);
            if (f && validSolution(solutions)) {
                if (F >= 0) System.arraycopy(testFactor, 0, factorMatrix[solutions], 0, F);
                solutions++;
            }
            cleanup();
        }
        for(int val : values) {
            System.out.print(val + " ");
        }
        return new MatrixSolver(factorMatrix, values);
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

    boolean bSmoothFactors(int n) {
        boolean nonTrivial = false;
        for(int i = 0; i < F; i++) {
            int prime = primes.get(i);
            while(n % prime == 0) {
                n /= prime;
                testFactor[i] = !testFactor[i];
            }
            nonTrivial |= testFactor[i];
        }
        return n == 1 && nonTrivial;
    }

    int generatePotentialFactor(int solutions) {
        int r = (int)Math.floor(Math.sqrt(k * N)) + j;
        values[solutions] = r;
        incrementJK();
        return r * r % N;
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
