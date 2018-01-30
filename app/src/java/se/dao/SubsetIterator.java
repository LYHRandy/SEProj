package se.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * For faster iteration through the different list types. Mainly used for AGD
 */
public class SubsetIterator<E> {
    private final List<E> set;
    private final int max;
    private int index;
    
    /**
     * 
     * @param originalList      original list to be checked with
     */
    public SubsetIterator(List<E> originalList) {
        set = originalList;
        max = (1 << set.size());
        index = 0;
    }
    
    /**
     * 
     * @return <code>true</code> if have next token
     *         <code>false</code> if does not have next token
     */
    public boolean hasNext() {
        return index < max;
    }
    
    /**
     * 
     * @return      List of elements
     */
    public List<E> next() {
        List<E> newSet = new ArrayList<E>();
        int flag = 1;      
        for (E element : set) {
            if ((index & flag) != 0) {
                newSet.add(element);
            }
            flag <<= 1;
        }
        ++index;
        return newSet;
    }
    

}