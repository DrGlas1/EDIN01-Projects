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
    List<Integer> primes = new ArrayList<>();
    int F;

    public QuadraticSieve(String filepath, int N) {
        this.N = N;
        this.L = (int)Math.round(Math.exp(Math.sqrt(Math.log(N) * Math.log(Math.log(N)))));
        this.B = (int)Math.round(Math.pow(L, 1.0 / Math.sqrt(2)));
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
    }

    boolean[] bSmoothFactors(int n) {
        boolean[] factors = new boolean[F];
        for(int i = 0; i < F; i++) {
            int prime = primes.get(i);
            while(n % prime == 0) {
                n /= prime;
                factors[i] = !factors[i];
            }
        }
        if (n > 1) {
            return null;
        }
        return factors;
    }
}
