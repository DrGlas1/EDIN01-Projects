package proj1;

import me.tongfei.progressbar.ProgressBar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QuadraticSieve {
    private final BigInteger N;             /* NUmber to factor */
    private final int L;                    /* Number of r values we check */
    private final int F;                    /* The number of primes less than B */
    private final boolean[][] matrix;       /* LxF Matrix containing the factors */
    private final boolean[][] records;      /* LxL Matrix which records the operations of the Gaussian elimination */
    private final boolean[] factorsOfR;     /* Factors of the current r^2 mod N */
    private final BigInteger[] values;      /* The r values */
    private final List<BigInteger> primes = new ArrayList<>();

    public QuadraticSieve(String filepath, BigInteger N) {
        this.N = N;
        this.L = 2000;
        int b = 1900;
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

    /**
     * Factors N and prints the factors
     */
    public void factor() {
        System.out.println("Finding factors of " + N);
        BigInteger j = BigInteger.ONE;
        BigInteger k = BigInteger.ONE;
        BigInteger lim = BigInteger.valueOf(L).sqrt().subtract(BigInteger.ONE);
        FactorPair pair = null;
        while(pair == null) {
            cleanRecords();
            generateMatrix(j, k, lim);
            solveMatrix();
            pair = testSolutions();
        }
        pair.print("The factors of " + N + " are: \n");
    }

    /**
     * Sets the diagonal of records to true and everything else to false
     */
    private void cleanRecords() {
        for(int i = 0; i < L; i++) {
            for(int p = 0; p < L; p++) {
                records[i][p] = p == i;
            }
        }
    }

    /**
     * Populates matrix with the factors mod 2 of the r values squared mod N
     * @param j Starting point for creating r values
     * @param k Starting point for creating r values
     * @param lim Limits the maximum value of j
     */
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

    /**
     * Checks if a number is B-smooth, i.e. has only prime factors less than B
     * @param n Number to check if B-smooth
     * @return true if n is B-smooth and where at least one factor doesn't have occured an even amount of times
     */
    boolean bSmoothFactors(BigInteger n) {
        /* If nonTrivial is false, then every factor appears an even amount of times */
        boolean nonTrivial = false;
        for(int i = 0; i < F; i++) {
            /* The i:th place of factorsOfR shows if n has an even or odd number of factors of the i:th prime */
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

    /**
     * Checks if the row to be inserted is already present
     * @param solutions The number of solutions that are already in matrix
     * @return true if a duplicate of factorsOfR is not in matrix, else false
     */
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

    /**
     * Performs Gaussian elimination on matrix to find the linearly dependant rows
     */
    public void solveMatrix() {
        ProgressBar pb = new ProgressBar("Solving matrix", F);
        pb.start();
        for(int i = 0; i < F; i++) {
            if (!matrix[i][i]) {
                for (int j = i + 1; j < L; j++) {
                    if (matrix[j][i]) {
                        pivot(i, j);
                        break;
                    }
                }
            }
            reduce(i);
            pb.step();
        }
        pb.stop();
    }

    /**
     * Tests all the linearly dependant rows and their linear combination according to the Quadratic Sieve algorithm.
     * lhs^2 = rhs^2 mod N
     * @return FactorPair containing two factors of N if found, else null
     */
    private FactorPair testSolutions() {
        List<Integer> indexOfSolutions = new ArrayList<>();
        for(int i = 0; i < L; i++) {
            if (validateRow(matrix[i])) {
                /* Test a linearly dependant row */
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
                FactorPair factor1 = retNonTrivialFactor(factor);
                if (factor1 != null) return factor1;
                indexOfSolutions.add(i);
            }
        }
        /* Test linear combinations of linearly dependant rows */
        for(int i = 0; i < indexOfSolutions.size(); i++) {
            for(int j = i + 1; j < indexOfSolutions.size(); j++) {
                BigInteger lhs = BigInteger.valueOf(1);
                BigInteger rhs = BigInteger.valueOf(1);
                for(int k = 0; k < L; k++) {
                    if (records[indexOfSolutions.get(i)][k] ^ records[indexOfSolutions.get(j)][k]) {
                        lhs = lhs.multiply(values[k]);
                        rhs = rhs.multiply(values[k].multiply(values[k]).mod(N));
                    }
                }
                lhs = lhs.mod(N);
                rhs = rhs.sqrt().mod(N);
                BigInteger factor = gcd(lhs.subtract(rhs),N);
                FactorPair factor1 = retNonTrivialFactor(factor);
                if (factor1 != null) return factor1;
            }
        }
        return null;
    }

    /**
     * Returns the factor if it is non-trivial, i.e. not equal to 1 or N
     * @param factor The factor to check
     * @return factor if it isn't trivial, else null
     */
    private FactorPair retNonTrivialFactor(BigInteger factor) {
        if (factor.compareTo(BigInteger.ONE) != 0 && factor.compareTo(N) != 0) {
            return new FactorPair(factor, N.divide(factor));
        }
        return null;
    }

    /**
     * Finds the GCD of two BigIntegers a and b
     * @param a First number to find a common factor
     * @param b Second number to find a common factor
     * @return BigInteger of GCD of a and b
     */
    BigInteger gcd(BigInteger a, BigInteger b) {
        while (b.intValue() != 0) {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        return a;
    }

    /**
     * Switches the rows i and j of matrix. Also switches the same rows of records
     * @param i First row to switch
     * @param j Second row to switch
     */
    private void pivot(int i, int j) {
        boolean[] temp = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = temp;
        temp = records[i];
        records[i] = records[j];
        records[j] = temp;
    }

    /**
     * Adds the i:th row mod 2 with all other rows that have true on the i:th position
     * Makes the i:th row the only row with true in the i:th position
     * @param i the row to add to other rows
     */
    private void reduce(int i) {
        for(int j = 0; j < L; j++) {
            if (i != j && matrix[j][i]) {
                addRow(i, j);
            }
        }
    }

    /**
     * Adds row i to row j mod 2. Also performs the same operation to records.
     * Because we know that all positions before the i:th are false in the i:th row,
     * we only need to do this from the i:th position to the end
     * @param i The row to add into another row
     * @param j The row to add into
     */
    private void addRow(int i, int j) {
        for(int k = i; k < F; k++) {
            matrix[j][k] ^= matrix[i][k];
        }
        for(int k = 0; k < L; k++) {
            records[j][k] ^= records[i][k];
        }
    }

    /**
     * Checks if all values of row is false, meaning the row was linearly dependant
     * @param row row to check
     * @return true if all elements of row are false, else false
     */
    private boolean validateRow(boolean[] row) {
        for(boolean val : row) {
            if (val) {
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

    public void print(String suffix) {
        System.out.println(suffix + factor1 + " " + factor2);
    }
}