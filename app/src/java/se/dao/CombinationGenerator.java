/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.dao;
import java.math.BigInteger;
/**
 * Combination Generator used to permutate combinations for AGD
 */
public class CombinationGenerator {
    private int[] a;
    private int n;
    private int r;
    private BigInteger numLeft;
    private BigInteger total;

    /**
     * Compute permutations of size r within n elements
     * 
     * @param n     number of elements
     * @param r     permutation size
     */
    public CombinationGenerator (int n, int r) {
	//if permutation size larger than num elements
        if (r > n) {
            throw new IllegalArgumentException ();
	}
        
        //if less than one elements
	if (n < 1) {
            throw new IllegalArgumentException ();
	}
        
	this.n = n;
	this.r = r;
	a = new int[r];
        
	BigInteger nFact = getFactorial (n);
	BigInteger rFact = getFactorial (r);
	BigInteger nminusrFact = getFactorial (n - r);
	total = nFact.divide (rFact.multiply (nminusrFact));
	reset ();
    }
    
    /**
     * resets the array
     */
    public void reset () {
	for (int i = 0; i < a.length; i++) {
            a[i] = i;
	}
            numLeft = new BigInteger (total.toString ());
    }
    
    /**
     * returns number of combinations left
     * @return number of combinations left
     */
    public BigInteger getNumLeft () {
	return numLeft;
    }
    
    /**
     * returns true if there are still combinations left
     * @return true if there are still combinations left
     */
    public boolean hasMore () {
	return numLeft.compareTo (BigInteger.ZERO) == 1;
    }
    
    /**
     * returns total number of permutations
     * @return total number of permutations
     */
    public BigInteger getTotal () {
	return total;
    }
    
    /**
     * compute factorial
     * @param n totalNum of people within location
     * @return n!
     */
    private static BigInteger getFactorial (int n) {
	BigInteger fact = BigInteger.ONE;
	for (int i = n; i > 1; i--) {
            fact = fact.multiply (new BigInteger (Integer.toString (i)));
	}
	
        return fact;
    }
    
    /**
     * Generate next combination (algorithm from Rosen p. 286)
     * @return combination of macAddresses within a single group
     */
        
    public int[] getNext () {
        if (numLeft.equals (total)) {
            numLeft = numLeft.subtract (BigInteger.ONE);
            return a;
        }
        int i = r - 1;
        while (a[i] == n - r + i) {
        i--;
        }
        a[i] = a[i] + 1;
        for (int j = i + 1; j < r; j++) {
            a[j] = a[i] + j - i;
        }
        numLeft = numLeft.subtract (BigInteger.ONE);
        return a;
    }
}
