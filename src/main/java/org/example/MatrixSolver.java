package org.example;
import java.math.BigInteger;

public class MatrixSolver {
    int n;
    int m;
    BigInteger N;
    BigInteger[] values;
    boolean[][] matrix;
    boolean[][] originalMatrix;
    boolean[][] records;
    public MatrixSolver(boolean[][] matrix, BigInteger[] values, BigInteger N) {
        this.matrix = matrix;
        this.n = matrix.length;
        this.m = matrix[0].length;
        this.N = N;
        this.originalMatrix = new boolean[n][m];
        for(int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i],0, this.originalMatrix[i], 0, m);
        }
        this.records = new boolean[n][n];
        for(int i = 0; i < n; i++) {
            records[i][i] = true;
        }
        this.values = values;
    }


    public FactorPair solve() {
        for(int i = 0; i < matrix[i].length; i++) {
            if (!matrix[i][i]) {
                for (int j = i + 1; j < n; j++) {
                    if (matrix[j][i]) {
                        switchRows(i, j);
                        break;
                    }
                }
            }
            reduce(i);
        }

        for(int i = 0; i < n; i++) {
            if (validateRow(matrix[i])) {
                BigInteger lhs = BigInteger.valueOf(1);
                BigInteger rhs = BigInteger.valueOf(1);
                for(int j = 0; j < n; j++) {
                    if (records[i][j]) {
                        lhs = lhs.multiply(values[j]);
                        rhs = rhs.multiply(values[j].multiply(values[j]).mod(N));
                    }
                }
                lhs = lhs.mod(N);
                rhs = rhs.mod(N);
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
        for(int j = i + 1; j < n; j++) {
            if (matrix[j][i]) {
                addRow(i, j);
            }
        }
    }

    private void addRow(int i, int j) {
        for(int k = i; k < matrix[i].length; k++) {
            matrix[j][k] ^= matrix[i][k];
        }
        for(int k = 0; k < n; k++) {
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

    public static void addRow(boolean[] row1, boolean[] row2) {
        for(int i = 0; i < row1.length; i++) {
            row1[i] ^= row2[i];
        }
    }

    public static boolean validSolution(boolean[][] matrix, boolean[] sol) {
        boolean[] res = new boolean[matrix[0].length];
        for(int i = 0; i < matrix.length; i++) {
            if (sol[i]) {
                addRow(res, matrix[i]);
            }
        }

        for(boolean val : res) {
            if (val){
                return false;
            }
        }
        return true;
    }
}

class FactorPair {
    BigInteger factor1;
    BigInteger factor2;
    public  FactorPair(BigInteger f1, BigInteger f2) {
        this.factor1 = f1;
        this.factor2 = f2;
    }

    public void print() {
        System.out.println(factor1 + " " + factor2);
    }
}
