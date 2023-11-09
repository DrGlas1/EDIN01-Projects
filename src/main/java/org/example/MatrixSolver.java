package org.example;
import java.util.LinkedList;
import java.util.List;

public class MatrixSolver {
    int n;
    int m;
    int[] values;
    boolean[][] matrix;
    boolean[][] records;
    public MatrixSolver(boolean[][] matrix, int[] values) {
        this.matrix = matrix;
        this.n = matrix.length;
        this.m = matrix[0].length;
        this.records = new boolean[n][n];
        for(int i = 0; i < n; i++) {
            records[i][i] = true;
        }
        this.values = values;
    }


    public List<boolean[]> solve() {
        List<boolean[]> res = new LinkedList<>();
        for(int i = 0; i < matrix[i].length; i++) {
            if (!matrix[i][i]) {
                for(int j = i + 1; j < n; j++) {
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
                res.add(records[i]);
            }
        }

        return res;
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
