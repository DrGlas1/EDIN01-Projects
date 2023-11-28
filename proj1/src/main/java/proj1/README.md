## Quadratic Sieve
The code is a simple implementation of the Quadratic Sieve algorithm for factoring large integers, for a project in the course EDIN01.
It assumes that the integer to be factorized has 2 prime factors, but repeated application of the sieve will find the rest of the factors.

### Run the code
The `QuadraticSieve` class takes two parameters, the first is a file path to a text file containing primes up to `B`, separated by a comma. 
The second argument is the integer to be factorized, represented as a java `BigInteger`. 
The sizes of `L` and `B` are hardcoded to be suitable to factor a number on the order of 10^25.

The code contains one external dependency, a progress bar that tracks how far along the algorithm is.
Remove this to run the code standalone.

