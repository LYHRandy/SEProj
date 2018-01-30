/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

 */
package se.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.Collections.list;
import java.util.Map.Entry;
import static javafx.scene.input.KeyCode.T;
import static jdk.nashorn.internal.objects.NativeArray.map;
import se.connection.ConnectionManager;
import static se.dao.topKDAO.compareDate;
import static se.dao.topKDAO.getTime;

/**
 * Database Access Object to access database for retrieval for Automatic Group Detection function
 */
public class agdDAO{

    private static SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static int numPpl = 0;
    
    /**
     * Checks if macaddress is already in the map
     * 
     * @param macAdd MacAddress that is needed to be checked
     * @param mappedMac Map containing of Key : Named Group, Value : MacAddress
     * @return TreeMap of MacAddresses mapped to their own specific group
     */
    public static TreeMap<String,String> checkMacInMap(String macAdd,TreeMap<String,String> mappedMac){
        if(!mappedMac.containsValue(macAdd)){
            mappedMac.put(mappedMac.size()+1+"",macAdd);
        }
        return mappedMac;
    }
    
    /**
     * Get the map key from value
     * 
     * @param tm TreeMap containing mapping of name and macAddress
     * @param value macAddress to matched with key of tm
     * @return Key : MacAddress of TreeMap, returns null if macAddress not present as a key
     */
    public static String getKeyFromValue(TreeMap<String,String> tm, String value) {
        for (String s : tm.keySet()) {
            if (tm.get(s).equals(value)) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * retrieves valid AGD groups
     * 
     * @param dateTime Date, Time input from user
     * @return Map of Groups, macAddresses in groups and time they were together
     */
    public static Map<ArrayList<String>,TreeMap<String,Integer>> retrieveAGD(String dateTime){
        
        //Start & End DateTime
        String dateTimeEnd = topKDAO.formatDate(dateTime);
        String dateTimeStart = topKDAO.getStartDate(dateTimeEnd);
        
        //get number of people in timeframe
        numPpl = getNumPpl(dateTimeEnd);
        
        //<record, [locMacStartEnd]> of current user
        TreeMap<Integer,String[]> userUpdates = new TreeMap<>();
        
        /*
          Only keeps users that spent more than 12 mins in time frame
          <record, userupdates>
        */
        TreeMap<Integer,TreeMap<Integer,String[]>> checkedUpdates = new TreeMap<>();
        
        //Mapping, MacAddress
        TreeMap<String,String> mappedMac = new TreeMap<>();
        
        try{
            Connection conn = ConnectionManager.getConnection();
            //Retrieve updates from user
            PreparedStatement pstmt = conn.prepareStatement("SELECT TIMESTAMP, MACADDRESS, LOCATIONID FROM LOCATION \n" +
                                                            "WHERE TIMESTAMP >= ? AND TIMESTAMP < ? \n" +
                                                            "ORDER BY MACADDRESS, TIMESTAMP ASC");
            pstmt.setString(1, dateTimeStart);
            pstmt.setString(2, dateTimeEnd);
            ResultSet rs = pstmt.executeQuery();
            int checkedRecord = 1;
            int record = 1;
            while(rs.next()){
                //[locationID,macAdd,startTime,endTime]
                String[] locMacStartEnd = new String[4];
                String macAdd = rs.getString(2);
                String locID = rs.getString(3);
                String startTime = rs.getString(1);
                //default ends after 5 mins
                String endTime = getTime(startTime,5);
                
                locMacStartEnd[0] = locID;
                mappedMac = checkMacInMap(macAdd,mappedMac);
                String mappedMacAdd = getKeyFromValue(mappedMac,macAdd);
                locMacStartEnd[1] = mappedMacAdd;
                locMacStartEnd[2] = startTime;
                if(compareDate(endTime,dateTimeEnd)>=0){
                    locMacStartEnd[3] = dateTimeEnd;
                } else {
                    locMacStartEnd[3] = endTime;
                }
                //check if first entry
                if(userUpdates.isEmpty()){
                    //add record of location and time
                    userUpdates.put(record, locMacStartEnd);
                    record++;
                } else {
                    String[] prevRecord = userUpdates.get(record-1);
                    //if prev location is same as curr location and same user
                    if(prevRecord[0].equals(locID) && prevRecord[1].equals(mappedMacAdd)){
                        //check if less than 5 min apart
                        if(compareDate(prevRecord[3],startTime)>=0){
                            //if prev end time after curr start time update endTime
                            prevRecord[3] = endTime;
                        } else{
                            //if prev end time before curr start time make new record
                            userUpdates.put(record, locMacStartEnd);
                            
                            record++;
                        }
                    } else if (prevRecord[1].equals(mappedMacAdd)){
                        //if prev location is different from curr location check time difference
                        if(compareDate(prevRecord[3],startTime)>=0){
                            //if prev endTime after curr Start time, prev end time equals curr start time
                            prevRecord[3] = startTime;
                        } 
                        //make new record
                        userUpdates.put(record, locMacStartEnd);
                        record++;
                    } else {
                        //different user
                        long count = 0;
                        for(Map.Entry<Integer,String[]> e : userUpdates.entrySet()){
                            int rec = e.getKey();
                            String [] locMacStartEndArr = userUpdates.get(rec);
                            try{
                                Date start = format.parse(locMacStartEndArr[2]);
                                Date end = format.parse(locMacStartEndArr[3]);
                                count += (end.getTime() - start.getTime());
                            } catch(ParseException pe){
                                pe.printStackTrace();
                            }
                        }
                        //if more than 12 minutes
                        if(count >= 720000){
                            checkedUpdates.put(checkedRecord++, userUpdates);
                        }
                        record = 1;
                        userUpdates = new TreeMap<>(); 
                        userUpdates.put(record, locMacStartEnd);
                        record++;
                    }
                }
            }
            //check if last user spent more than 12 mins
            long count = 0;
            for(Map.Entry<Integer,String[]> e : userUpdates.entrySet()){
                int rec = e.getKey();
                String [] locMacStartEndArr = userUpdates.get(rec);
                try{
                    Date start = format.parse(locMacStartEndArr[2]);
                    Date end = format.parse(locMacStartEndArr[3]);
                    count += (end.getTime() - start.getTime());
                } catch(ParseException pe){
                    pe.printStackTrace();
                }
            }
            if(count >= 720000){
                checkedUpdates.put(checkedRecord++, userUpdates);
            }
        }catch(SQLException e){
            e.printStackTrace();
        } finally {
        }
        
        //Map all the updates into a big hash table of <locationID+TimeStamp, Users at location>
        TreeMap<String,ArrayList<String>> bigHashTable = bigHashTable(checkedUpdates);
       
        //Compute all the permutations and time spent at each location <permutations, <locationID, Time>>
        Map<String,TreeMap<String,Integer>> bigHashTablePermutations = bigHashTablePermutations(bigHashTable);

        //filter out groups that spent more than 12 minutes together
        Map<ArrayList<String>,TreeMap<String,Integer>> validGroups = validateGroups(bigHashTablePermutations,mappedMac);

        //filter out repeat groups
        Map<ArrayList<String>,TreeMap<String,Integer>> finalGroups = new HashMap<>();
        for(Map.Entry<ArrayList<String>,TreeMap<String,Integer>> e : validGroups.entrySet()){
            ArrayList<String> key = e.getKey();
            boolean noDuplicate = true;
            for(Map.Entry<ArrayList<String>,TreeMap<String,Integer>> e1 : validGroups.entrySet()){
                ArrayList<String> key1 = e1.getKey();
                if(!(key.containsAll(key1) && key1.containsAll(key))){
                    if(key1.containsAll(key)){
                        noDuplicate = false;
                        break;
                    }
                }
            }
            if(noDuplicate){
                finalGroups.put(e.getKey(),e.getValue());
            }
        }
        return finalGroups;
    }  
    
    /**
     * Maps the valid users into a table by locationID and individual seconds
     * 
     * @param checkedUpdates    map of valid users that spent more than 12 mins in timeframe
     * @return TreeMap of valid users by locationID and Individual seconds user spent at the place
     */
    public static TreeMap<String,ArrayList<String>> bigHashTable(TreeMap<Integer,TreeMap<Integer,String[]>> checkedUpdates){
        // bigHashTable variables
        //<locID+","+timeStamp,Macaddresses>
        TreeMap<String,ArrayList<String>> bigHT = new TreeMap<>();
        
        //for each entry in validated updates
        for(Map.Entry<Integer,TreeMap<Integer,String[]>> e : checkedUpdates.entrySet()){
            int key = e.getKey();
            TreeMap<Integer,String[]> userUpdates = checkedUpdates.get(key);
            //retrieve time spent by user in each location
            for(Map.Entry<Integer,String[]> ent : userUpdates.entrySet()){
                int k = ent.getKey();
                String [] locMacStartEnd = userUpdates.get(k);
                try{
                    Date currTime = format.parse(locMacStartEnd[2]);
                    Date end = format.parse(locMacStartEnd[3]);
                    Calendar date = Calendar.getInstance();
                    date.setTime(currTime);
                
                    while(currTime.before(end)){
                        String locTime = locMacStartEnd[0]+","+format.format(currTime);
                        if(!bigHT.containsKey(locTime)){
                            ArrayList<String> macAddKeys = new ArrayList<>();
                            macAddKeys.add(locMacStartEnd[1]);
                            bigHT.put(locTime, macAddKeys);
                        } else {
                            ArrayList<String> macAddKeys = bigHT.get(locTime);
                            macAddKeys.add(locMacStartEnd[1]);
                            bigHT.put(locTime, macAddKeys);
                            }
                        date.setTime(currTime);
                        long t = date.getTimeInMillis();
                        currTime = new Date(t+1000);
                    }
                } catch (ParseException pe){
                    pe.printStackTrace();;
                }
            }
        }
        
        return bigHT;
    }
    
    
    /**
     * Retrieves number of people in timeframe
     * 
     * @param dateTime Date, Time specified by user
     * @return number of people in the location 15mins within Date Time specified
     */
    public static int getNumPpl(String dateTime){
        
        //Start & End DateTime
        String dateTimeStart = topKDAO.getStartDate(dateTime);
        
        //counter
        int count = 0;

        try{
            Connection conn = ConnectionManager.getConnection();

            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(DISTINCT MACADDRESS) FROM LOCATION \n" +
                                                            "WHERE TIMESTAMP >= ? AND TIMESTAMP < ? \n"
                                                            );
            pstmt.setString(1, dateTimeStart);
            pstmt.setString(2, dateTime);
            ResultSet rs = pstmt.executeQuery();    
            
            if(rs.next()){
                count = Integer.parseInt(rs.getString(1));     
            }    
        }catch(SQLException e){
            e.printStackTrace();
        } finally {
            
        }    
        return count;
    }
    
    /**
     * Generates all permutations of size less than or equals to 2 in given number of elements
     * 
     * @param numElements   number of elements
     * @return permutations of the different possible combination of groups
     */
    public static Set<String> generatePermutations(int numElements){
        Set<String> perms = new HashSet<>();
        int [] indices;
        //min size of permutation = 2
        int kComb = 2;
        while(numElements >= kComb){
            CombinationGenerator x = new CombinationGenerator (numElements, kComb);  
            while (x.hasMore()) {
                StringBuilder sb = new StringBuilder();
                indices = x.getNext();
                for (int i = 0; i < indices.length; i++) {
                    sb.append(indices[i]+",");
                }
                perms.add(sb.toString());
            }
            kComb++;
        }
        return perms;
    }
    
    /**
     * Computes the possible permutations for each record in bogHashTable
     * 
     * @param bht   big hash table
     * @return computes the possible permutations of specific records of users in their different groups
     */
    public static Map<String,TreeMap<String,Integer>> bigHashTablePermutations(Map<String,ArrayList<String>> bht){

        // Iterate through big hash table
        Iterator it = bht.entrySet().iterator();
        
        //numElements, permutations
        TreeMap<Integer,Set<String>> permutations = new TreeMap<>();
        
        // Map<Permuations,locTime> bigHashTablePermutations
        Map<String,TreeMap<String,Integer>> mp =  new HashMap<>();
        
        //  Extract locTime , arraylist of macAddressID
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //String locTime = (String) pair.getKey(); 
            ArrayList<String> macAddressIDList = (ArrayList<String>) pair.getValue();
            int numElements = macAddressIDList.size();
            //System.out.println(numElements);
            String key = (String)pair.getKey();
            String[] locTime = key.split(",");
            if(!permutations.containsKey(numElements)){
                Set<String> perms = generatePermutations(numElements);
                permutations.put(numElements,perms);
            }
            Set<String> permutation = permutations.get(numElements);

            for(String perm : permutation){
                String thisPermutation = "";
                String[] currPerm = perm.split(",");
                for(String thisPerm : currPerm){
                    thisPermutation+=(macAddressIDList.get(Integer.parseInt(thisPerm)))+",";
                }
                if(mp.containsKey(thisPermutation)){
                    TreeMap<String,Integer> locSeconds = mp.get(thisPermutation);
                    if(locSeconds.containsKey(locTime[0])){
                        locSeconds.put(locTime[0],locSeconds.get(locTime[0])+1);
                        mp.put(thisPermutation, locSeconds);
                    }else{
                        locSeconds.put(locTime[0], 1);
                        mp.put(thisPermutation, locSeconds);
                    }
                }else{
                    TreeMap<String,Integer> locSeconds = new TreeMap<>();
                    locSeconds.put(locTime[0], 1);
                    mp.put(thisPermutation, locSeconds);
                }
                
            }
            
        }

        return mp;
    }
    
    /**
     * Computes the possible permutations for each record in bogHashTable
     * 
     * @param bigHashTablePermutations   All possible combination of groups in groups for 720seconds or more
     * @param mappedMac macAddress mapped to a specific number
     * @return groups with email addresses for users who are students
     */
    private static Map<ArrayList<String>, TreeMap<String, Integer>> validateGroups(Map<String, TreeMap<String, Integer>> bigHashTablePermutations, TreeMap<String,String> mappedMac) {
        //ArrayList of [email,macadd], HashMap<locID, seconds
        Map<ArrayList<String>, TreeMap<String, Integer>> groups = new HashMap<>();
        
        //check how long each permutation spent together
        for(Map.Entry<String,TreeMap<String,Integer>> e : bigHashTablePermutations.entrySet()){
            int totalTime = 0;
            String users = e.getKey();
            TreeMap<String,Integer> locCount = bigHashTablePermutations.get(users);
            for(Map.Entry<String,Integer> e1 : locCount.entrySet()){
                String locID = e1.getKey();
                int count = locCount.get(locID);
                totalTime += count;
            }
            
            //if more than 720 seconds
            if(totalTime >= 720){
                String[] macIDs = users.split(",");
                ArrayList<String> macAdds = new ArrayList<>();
                for(String macID : macIDs){
                    macAdds.add(mappedMac.get(macID));
                }
                ArrayList<String> emailMacAdd = new ArrayList<>();
                for(String macAdd : macAdds){
                    if(!macAdd.isEmpty()){
                        String email = "";
                        try{   

                            Connection conn = ConnectionManager.getConnection();
                            PreparedStatement pstmt = conn.prepareStatement("SELECT EMAIL FROM DEMOGRAPHICS \n" +
                                                                            "WHERE MACADDRESS = ? \n"
                                                                            );
                            pstmt.setString(1, macAdd);
                            ResultSet rs = pstmt.executeQuery();
                            if(rs.next()){
                                email = rs.getString(1);
                            }
                            conn.close();
                        } catch(SQLException sqle){
                            sqle.printStackTrace();
                        }
                        String eMac = email+","+macAdd;
                        emailMacAdd.add(eMac);
                    }
                }
                
                groups.put(emailMacAdd, locCount);
            }
        }
        return groups;
    }
    
    
}
          
