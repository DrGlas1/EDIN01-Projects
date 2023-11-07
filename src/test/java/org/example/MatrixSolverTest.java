package org.example;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatrixSolverTest {

    @Test
    void testSmallMatrixSolutions() {
        boolean[][] matrix  = new boolean[][]{
                {true, true},
                {true, false},
                {false, true},
        };
        boolean[][] matrix_copy = copyBooleanArray(matrix);
        MatrixSolver solver = new MatrixSolver(matrix);
        List<boolean[]> solutions = solver.solve();

        for(boolean[] solution : solutions) {
            assertTrue(solver.validSolution(solution, matrix_copy), "Given solution doesn't solve xA = 0");
        }
    }

    @Test
    void testMediumMatrixSolutions() {
        boolean[][] matrix  = new boolean[][]{
                {false, true, true},
                {false, true, false},
                {true, false, false},
                {true, true, true},
                {false, false, true}
        };
        boolean[][] matrix_copy = copyBooleanArray(matrix);
        MatrixSolver solver = new MatrixSolver(matrix);
        List<boolean[]> solutions = solver.solve();

        for(boolean[] solution : solutions) {
            assertTrue(solver.validSolution(solution, matrix_copy), "Given solution doesn't solve xA = 0");
        }
    }

    @Test
    void testLargeMatrixSolutions() {
        boolean[][] matrix = new boolean[][]{
                {true, true, false, true, false, false, true, false, false, false},
                {false, false, false, false, false, true, false, false, false, false},
                {true, false, false, false, true, false, true, false, false, false},
                {false, true, false, true, true, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, true, true},
                {true, false, true, true, false, false, true, false, false, false},
                {false, false, false, false, false, true, true, true, false, false},
                {true, false, false, false, false, false, true, false, false, false},
                {true, false, true, false, true, false, false, false, true, false},
                {true, false, true, false, false, false, false, false, true, false},
                {true, false, false, false, false, false, false, true, false, false},
                {true, true, false, true, false, false, false, true, false, false},
        };
        boolean[][] matrix_copy = copyBooleanArray(matrix);
        MatrixSolver solver = new MatrixSolver(matrix);
        List<boolean[]> solutions = solver.solve();

        for(boolean[] solution : solutions) {
            assertTrue(solver.validSolution(solution, matrix_copy), "Given solution doesn't solve xA = 0");
        }
    }

    @Test
    void testRandomMatrix() {
        boolean[][] matrix = createBooleanMatrix(20, 10);
        boolean[][] matrix_copy = copyBooleanArray(matrix);
        MatrixSolver solver = new MatrixSolver(matrix);
        List<boolean[]> solutions = solver.solve();

        for(boolean[] solution : solutions) {
            assertTrue(solver.validSolution(solution, matrix_copy), "Given solution doesn't solve xA = 0");
        }

        matrix = createBooleanMatrix(30, 10);
        matrix_copy = copyBooleanArray(matrix);
        solver = new MatrixSolver(matrix);
        solutions = solver.solve();

        for(boolean[] solution : solutions) {
            assertTrue(solver.validSolution(solution, matrix_copy), "Given solution doesn't solve xA = 0");
        }
    }

    boolean[][] createBooleanMatrix(int n, int m) {
        boolean[][] matrix = new boolean[n][m];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                double r = Math.random();
                if (r > 0.5) {
                    matrix[i][j] = true;
                }
            }
        }
        return matrix;
    }

    boolean[][] copyBooleanArray(boolean[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;

        boolean[][] copy = new boolean[n][m];

        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, m);
        }

        return copy;
    }
}