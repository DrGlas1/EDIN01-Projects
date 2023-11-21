package org.example;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatrixSolver {
    int n;
    int m;
    int numberOfPrimes;
    BigInteger N;
    BigInteger[] values;
    boolean[][] matrix;
    boolean[][] records;
    public MatrixSolver(boolean[][] matrix, BigInteger[] values, BigInteger N, int numberOfPrimes) {
        this.matrix = matrix;
        this.n = matrix.length;
        this.m = matrix[0].length;
        this.N = N;
        this.records = new boolean[n][n];
        for(int i = 0; i < n; i++) {
            records[i][i] = true;
        }
        this.values = values;
        this.numberOfPrimes = numberOfPrimes;
    }


    public FactorPair solve() {
        for(int i = 0; i < matrix.length && i < matrix[0].length; i++) {
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
        List<Integer> indexOfSolutions = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            if (validateRow(matrix[i])) {
                indexOfSolutions.add(i);

            }
        }
        for(int i = 0; i < indexOfSolutions.size(); i++) {
            BigInteger lhs = BigInteger.valueOf(1);
            BigInteger rhs = BigInteger.valueOf(1);
            for(int j = 0; j < n; j++) {
                if (records[indexOfSolutions.get(i)][j]) {
                    lhs = lhs.multiply(values[j]);
                    rhs = rhs.multiply(values[j].multiply(values[j]).mod(N));
                }
            }
            FactorPair factor = getFactorPair(lhs, rhs);
            if (factor != null) return factor;
            for(int j = i + 1; j < indexOfSolutions.size(); j++) {
                lhs = BigInteger.valueOf(1);
                rhs = BigInteger.valueOf(1);
                for(int k = 0; k < n; k++) {
                    if (records[indexOfSolutions.get(i)][k] ^ records[indexOfSolutions.get(j)][k]) {
                        lhs = lhs.multiply(values[k]);
                        rhs = rhs.multiply(values[k].multiply(values[k]).mod(N));
                    }
                }
                factor = getFactorPair(lhs, rhs);
                if (factor != null) return factor;
            }
        }
        return null;
    }

    private FactorPair getFactorPair(BigInteger lhs, BigInteger rhs) {
        lhs = lhs.mod(N);
        rhs = rhs.mod(N);
        BigInteger factor = gcd(lhs.subtract(rhs),N);
        if (factor.compareTo(BigInteger.ONE) != 0) {
            return new FactorPair(factor, N.divide(factor));
        }
        return null;
    }

    BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) == 0) return a;
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
        for(int j = 0; j < n; j++) {
            if (i != j && matrix[j][i]) {
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

    public void exportMatrixToFile(boolean[][] matrix) {
        try {
            var filePath = "./src/main/java/org/example/matrix.txt";
            clearFile(filePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(matrix.length + " " + matrix[0].length);
            writer.newLine();

            for (boolean[] booleans : matrix) {
                for (boolean aBoolean : booleans) {
                    var nbr = aBoolean ? 1 : 0;
                    writer.write(nbr + " ");
                }
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean[][] solveFile() throws IOException, InterruptedException {
        clearFile("./src/main/java/org/example/solutions2.txt");
        Runtime.getRuntime().exec("./src/main/java/org/example/a.out ./src/main/java/org/example/matrix.txt ./src/main/java/org/example/solutions2.txt").waitFor();
        //Thread.sleep(5000);
        return readSolutionFromFile2();

    }

    private boolean[][] readSolutionFromFile2() throws FileNotFoundException {
        var filePath = "./src/main/java/org/example/solutions2.txt";
        var file = new File(filePath);
        var reader = new Scanner(file);

        int numRows = reader.nextInt();
        reader.nextLine();

        boolean[][] solutionMatrix = new boolean[numRows][values.length];

        int i = 0;
        while (reader.hasNextLine()) {
            try {
                String line = reader.nextLine();
                String[] rowValues = line.trim().split(" ");
                for (int j = 0; j < rowValues.length; j++) {
                    solutionMatrix[i][j] = rowValues[j].equals("1");
                }
            } catch (Exception e) {
                System.out.println("error at i:" + i);
                e.printStackTrace();
            }
            i++;
        }

        reader.close();

        return solutionMatrix;

    }

    private boolean[][] readSolutionFromFile() {
        var filePath = "./src/main/java/org/example/solutions2.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int numRows = Integer.parseInt(br.readLine().trim());
            boolean[][] matrix = new boolean[numRows][];
            for (int i = 0; i < numRows; i++) {
                try {
                    String line = br.readLine();
                    String[] rowValues = line.trim().split(" ");
                    matrix[i] = new boolean[rowValues.length];
                    for (int j = 0; j < rowValues.length; j++) {
                        matrix[i][j] = rowValues[j].equals("1");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            br.close();
            clearFile(filePath);
            return matrix;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void clearFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FactorPair findFactorPairFromSolution(boolean[][] possibleSolutions) {
        int i = 0;
        for (boolean[] possibleSolution : possibleSolutions) {
            BigInteger lhs = BigInteger.ONE;
            BigInteger rhs = BigInteger.ONE;
            for (int j = 0; j < n; j++) {
                if (possibleSolution[j]) {
                    lhs = lhs.multiply(values[j]);
                    rhs = rhs.multiply(values[j].multiply(values[j]).mod(N));
                }
            }
            if (i == 0) {
                System.out.println(lhs.mod(N) + " " + rhs.mod(N));
                i++;
            }
            FactorPair factor = getFactorPair(lhs, rhs);
            if (factor != null) return factor;
        }
        return null;
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
