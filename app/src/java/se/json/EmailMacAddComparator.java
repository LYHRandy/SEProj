/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;
import java.util.*;


/**
 * To sort output for AGDJson
 */

public class EmailMacAddComparator implements Comparator<ArrayList<String>>{
    @Override
     public int compare(ArrayList<String> sList, ArrayList<String> sList2){
        if(sList.size() > sList2.size()){
            return 1;
        }
        else if(sList.size() < sList2.size()){
            return -1;
        }
        for(int i=0; i < sList.size(); i++){
            String str = sList.get(i);
            String str2 = sList2.get(i);
            String eMac[] = str.split(",");
            String eMac2[] = str2.split(",");
            
            if(eMac[0].compareTo(eMac2[0])!=0){
                return eMac2[0].compareTo(eMac[0]);
            }
            if(eMac[1].compareTo(eMac2[1])!=0){
                return eMac2[1].compareTo(eMac[1]);
            }
        }
        return 0;
    }
}

