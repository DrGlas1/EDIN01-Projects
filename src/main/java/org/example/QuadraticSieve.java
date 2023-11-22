package org.example;

import me.tongfei.progressbar.ProgressBar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class QuadraticSieve {
    private final boolean[][] matrix;
    private final boolean[][] records;
    private final boolean[] factorsOfR;
    private final BigInteger[] values;
    private final BigInteger N;
    private final int L;
    private final int F;
    private final List<BigInteger> primes = new ArrayList<>();

    public QuadraticSieve(String filepath, BigInteger N) {
        this.N = N;
        this.L = 1024;
        int b = 1014;
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] primeStrings = line.split(",");
                for (String primeStr : primeStrings){
                    var prime = new BigInteger(primeStr);
                    if (prime.compareTo(BigInteger.valueOf(b)) >= 0) {
                        break;
                    }
                    primes.add(prime);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.F = primes.size();
        this.matrix = new boolean[L][F];
        this.records = new boolean[L][L];
        this.values = new BigInteger[L];
        this.factorsOfR = new boolean[F];
    }

    public void factor() {
        BigInteger j = BigInteger.ONE;
        BigInteger k = BigInteger.ONE;
        BigInteger lim = BigInteger.valueOf(L).sqrt().subtract(BigInteger.ONE);
        FactorPair pair = null;
        while(pair == null) {
            for(int i = 0; i < L; i++) {
                for(int p = 0; p < L; p++) {
                    records[i][p] = p == i;
                }
            }
            generateMatrix(j, k, lim);
            solveMatrix();
            pair = testSolutions();
        }
        pair.print();
    }

    void generateMatrix(BigInteger j, BigInteger k, BigInteger lim) {
        int solutions = 0;
        ProgressBar pb = new ProgressBar("Creating matrix", L);
        pb.start();
        while(solutions < L) {
            BigInteger r = N.multiply(k).sqrt().add(j);
            BigInteger n = r.multiply(r).mod(N);
            if (lim.compareTo(j) == 0) {
                j = BigInteger.ONE;
                k = k.add(BigInteger.ONE);
            } else {
                j = j.add(BigInteger.ONE);
            }
            boolean f = bSmoothFactors(n);
            if (f && validSolution(solutions)) {
                pb.step();
                System.arraycopy(factorsOfR, 0, matrix[solutions], 0, F);
                values[solutions] = r;
                solutions++;
            }
        }
        pb.stop();
    }

    boolean bSmoothFactors(BigInteger n) {
        boolean nonTrivial = false;
        for(int i = 0; i < F; i++) {
            factorsOfR[i] = false;
            BigInteger prime = primes.get(i);
            while(n.mod(prime).equals(BigInteger.ZERO)) {
                n = n.divide(prime);
                factorsOfR[i] = !factorsOfR[i];
            }
            nonTrivial |= factorsOfR[i];
        }
        return n.equals(BigInteger.ONE) && nonTrivial;
    }

    boolean validSolution(int solutions) {
        for(int i = 0; i < solutions; i++) {
            for(int j = 0; j < F; j++) {
                if (factorsOfR[j] != matrix[i][j]) {
                    break;
                }
                if (j == F - 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public void solveMatrix() {
        ProgressBar pb = new ProgressBar("Solving matrix", F);
        pb.start();
        for(int i = 0; i < F; i++) {
            if (!matrix[i][i]) {
                for (int j = i + 1; j < L; j++) {
                    if (matrix[j][i]) {
                        switchRows(i, j);
                        break;
                    }
                }
            }
            reduce(i);
            pb.step();
        }
        pb.stop();
    }

    private FactorPair testSolutions() {
        for(int i = 0; i < L; i++) {
            if (validateRow(matrix[i])) {
                BigInteger lhs = BigInteger.valueOf(1);
                BigInteger rhs = BigInteger.valueOf(1);
                for(int j = 0; j < L; j++) {
                    if (records[i][j]) {
                        lhs = lhs.multiply(values[j]);
                        rhs = rhs.multiply(values[j].multiply(values[j]).mod(N));
                    }
                }
                lhs = lhs.mod(N);
                rhs = rhs.sqrt().mod(N);
                BigInteger factor = gcd(lhs.subtract(rhs),N);
                if (factor.intValue() != 1) {
                    return new FactorPair(factor, N.divide(factor));
                }

            }
        }
        return null;
    }

    BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.intValue() == 0) return a;
        return gcd(b, a.mod(b));
    }

    private void switchRows(int i, int j) {
        boolean[] temp = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = temp;
        temp = records[i];
        records[i] = records[j];
        records[j] = temp;
    }

    private void reduce(int i) {
        for(int j = 0; j < L; j++) {
            if (i != j && matrix[j][i]) {
                addRow(i, j);
            }
        }
    }

    private void addRow(int i, int j) {
        for(int k = i; k < F; k++) {
            matrix[j][k] ^= matrix[i][k];
        }
        for(int k = 0; k < L; k++) {
            records[j][k] ^= records[i][k];
        }
    }

    private boolean validateRow(boolean[] row) {
        for(boolean val : row) {
            if (val){
                return false;
            }
        }
        return true;
    }
}
