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
import java.util.Map.Entry;
import static jdk.nashorn.internal.objects.NativeArray.map;
import se.connection.ConnectionManager;

/**
 * Database Access Object to access database for retrieval of data for Top K functions
 */
public class topKDAO {
    private static final long ONE_MINUTE_IN_MILLIS = 60000;
    private static SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static ArrayList<String> sortedEmails = new ArrayList<>();
    public static int numPplBef = 0;
    public static int numPplAft = 0;
    
    /**
     * This method retrieves top k popular places
     * @param k             top k values to be checked
     * @param dateTime      date and time to be checked
     * @return              return TreeMap of record count and ArrayList of place and number of people at that place
     */
    public static TreeMap<Integer,ArrayList<String>> retrieveTopKPP(int k, String dateTime){
        TreeMap<Integer,ArrayList<String>> topKPP = new TreeMap<>();
        String dateTimeEnd = formatDate(dateTime);
        String dateTimeStart = getStartDate(dateTimeEnd);
        
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement
                                    ("SELECT COUNT(MACADDRESS) AS NUMPPL, SEMANTICPLACE FROM \n" +
                                    "(SELECT LATEST.MACADDRESS,LOCATIONID FROM\n" +
                                    "(SELECT MAX(TIMESTAMP) AS LTS, MACADDRESS FROM app.LOCATION \n" +
                                    "WHERE TIMESTAMP >= ? AND TIMESTAMP < ?\n" +
                                    "GROUP BY MACADDRESS) AS LATEST \n" +
                                    "LEFT OUTER JOIN APP.LOCATION L\n" +
                                    "ON LTS = TIMESTAMP AND LATEST.MACADDRESS = L.MACADDRESS) AS LATEST_LOCATION \n" +
                                    "LEFT OUTER JOIN  app.LOCATION_LOOKUP AS LL ON LATEST_LOCATION.LOCATIONID = LL.LOCATIONID\n" +
                                    "GROUP BY SEMANTICPLACE\n" +
                                    "ORDER BY NUMPPL DESC, semanticplace asc;");
            pstmt.setString(1, dateTimeStart);
            pstmt.setString(2, dateTimeEnd);
            
            ResultSet rs = pstmt.executeQuery();
            int countK = 1;
            while(rs.next()){
                ArrayList<String> kPlace = new ArrayList<>();
                String place = rs.getString(1);
                String count  = rs.getString(2);
                kPlace.add(count);
                kPlace.add(place);
                topKPP.put(countK, kPlace);
                //Add count and corresponding num of people and place
                countK++;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return topKPP;
    }
    
    /**
     * This method retrieves top k next places
     * @param kValue            top-k values to be checked           
     * @param semanticPlace     semantic place to be checked
     * @param dateTime          date and time to be checked
     * @return                  returns map of semantic place (Key) and the number of people (Value)
     */
      public static Map<String,Integer> retrieveTopKNP(int kValue, String semanticPlace, String dateTime) {
        //store users at location in timeframe
        ArrayList<String> macAddresses = new ArrayList<>();
        //semantic place, count
        HashMap<String,Integer> counter = new HashMap<>();
        
        Map<String,Integer> topKNP = new HashMap<>();

        //valid macaddress, semanticplace : stay more than 5 min
        HashMap<String,String> macSem = new HashMap<>();
        
        numPplBef = 0;
        numPplAft = 0;
        String firstWindowEnd = formatDate(dateTime);
        String firstWindowStart = getStartDate(firstWindowEnd);
        String secWindowStart = firstWindowEnd;
        String secWindowEnd = getTime(firstWindowEnd,15);
        
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement
                                        ("SELECT MACADDRESS FROM\n" +
                                         "(SELECT A.MACADDRESS,LOCATIONID FROM\n" +
                                         "(SELECT MAX(TIMESTAMP) AS LTS, MACADDRESS FROM LOCATION \n" +
                                         "WHERE TIMESTAMP >= ? AND TIMESTAMP < ? \n" +
                                         "GROUP BY MACADDRESS) AS A\n" +
                                         "LEFT OUTER JOIN APP.LOCATION L\n" +
                                         "ON LTS = TIMESTAMP AND A.MACADDRESS = L.MACADDRESS) AS B\n" +
                                         "LEFT OUTER JOIN LOCATION_LOOKUP LL\n" +
                                         "ON B.LOCATIONID = LL.LOCATIONID\n" +
                                         "WHERE SEMANTICPLACE = ?");
            pstmt.setString(1, firstWindowStart);
            pstmt.setString(2, firstWindowEnd);
            pstmt.setString(3, semanticPlace);
            
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()){
                numPplBef++;
                String macAddress = rs.getString(1);
                macAddresses.add(macAddress);
            }
            
             for(String macAdd: macAddresses){
                pstmt = conn.prepareStatement
                        ("SELECT TIMESTAMP, SEMANTICPLACE FROM LOCATION L\n" +
                        "LEFT OUTER JOIN LOCATION_LOOKUP LL\n" +
                        "ON L.LOCATIONID = LL.LOCATIONID\n" +
                        "WHERE TIMESTAMP >= ? AND TIMESTAMP < ?\n" +
                        "AND MACADDRESS= ? \n" +
                        "ORDER BY TIMESTAMP ASC");
                
                pstmt.setString(1, secWindowStart);
                pstmt.setString(2, secWindowEnd);
                pstmt.setString(3, macAdd);
                
                rs = pstmt.executeQuery();
                
                String prevSemPlace = "";
                String prevBegTime = "";
                
                while(rs.next()){
                    String currSemPlace = rs.getString(2);
                    String currBegTime = rs.getString(1);
                    
                    if(prevSemPlace.isEmpty() || prevBegTime.isEmpty()){
                        prevSemPlace = currSemPlace;
                        prevBegTime = currBegTime;
                    }

                    if(!prevSemPlace.equals(currSemPlace)){
                        if(compareDate(getTime(prevBegTime,5),currBegTime)<=0){
                            macSem.put(macAdd, prevSemPlace); 
                        }
                        prevSemPlace = currSemPlace;
                        prevBegTime = currBegTime;
                    }
                }
                if (!prevBegTime.isEmpty() && compareDate(getTime(prevBegTime,5), secWindowEnd)<=0){
                    macSem.put(macAdd, prevSemPlace);
                } 
            }
            numPplAft = macSem.size();   
            for(Map.Entry<String, String> entry : macSem.entrySet()){
                String macAdd = entry.getKey();
                String semPlace = macSem.get(macAdd);
                if(counter.containsKey(semPlace)){
                    counter.put(semPlace, counter.get(semPlace)+1);
                } else {
                    counter.put(semPlace, 1);
                }
            }
            
            topKNP = sortByComparator2(counter, false);
            /*
            System.out.println(macAddresses);
            for(Map.Entry<String, String> e : macSem.entrySet()){
                String key = e.getKey();
                String sem = macSem.get(key);
                System.out.println(key+" "+sem);
            }
            
            for(Map.Entry<String, Integer> e:counter.entrySet()){
                String sem = e.getKey();
                int count = counter.get(sem);
                System.out.println(sem + " " + count);
            }
            
            for(Map.Entry<String, Integer> e : topKNP.entrySet()){
                String sem = e.getKey();
                int count = counter.get(sem);
                System.out.println(sem+" "+count);
            }
            */
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return topKNP;
    }
                
    /**
     * This method returns top k companions
     * @param k             top k values to be checked
     * @param macAddress    macAddresses to be checked
     * @param dateTime      date and time to be checked
     * @return              return Map of Mac address and time spent with users
     */
    public static Map<String,Long> retrieveTopKC(int k, String macAddress, String dateTime){
        //places user has been
        TreeMap<Integer,String[]> userUpdates = new TreeMap<>();
        
        //people who have been with user
        TreeMap<Integer,String[]> userCompanions = new TreeMap<>();
        
        //Macaddress, time spent with user
        Map<String,Long> topKC = new HashMap<>();
        
        //Store sorted values
        Map<String, Long> sortedMapAsc = new HashMap<>();
        
        String dateTimeEnd = formatDate(dateTime);
        String dateTimeStart = getStartDate(dateTimeEnd);
        
        try{
            Connection conn = ConnectionManager.getConnection();
            //Retrieve updates from user
            PreparedStatement pstmt = conn.prepareStatement("SELECT TIMESTAMP, LOCATIONID FROM LOCATION \n" +
                                                            "WHERE TIMESTAMP >= ? AND TIMESTAMP < ? \n" +
                                                            "AND MACADDRESS = ? \n" +
                                                            "ORDER BY TIMESTAMP ");
            pstmt.setString(1, dateTimeStart);
            pstmt.setString(2, dateTimeEnd);
            pstmt.setString(3, macAddress);
            ResultSet rs = pstmt.executeQuery();
            int record = 1;
            while(rs.next()){
                //[locationID,startTime,endTime]
                String[] locStartEnd = new String[3];
                String locID = rs.getString(2);
                String startTime = rs.getString(1);
                //default ends after 5 mins
                String endTime = getTime(startTime,5);
                
                locStartEnd[0] = locID;
                locStartEnd[1] = startTime;
                if(compareDate(endTime,dateTimeEnd)>=0){
                    locStartEnd[2] = dateTimeEnd;
                } else {
                    locStartEnd[2] = endTime;
                }
                    
                //check if first entry
                if(userUpdates.size()==0){
                    //add record of location and time
                    userUpdates.put(record, locStartEnd);
                    record++;
                } else {
                    String[] prevRecord = userUpdates.get(record-1);
                    //if prev location is same as curr location
                    if(prevRecord[0].equals(locID)){
                        //check if if less than 5 min apart
                        if(compareDate(prevRecord[2],startTime)>=0){
                            //if prev end time after curr start time update endTime
                            prevRecord[2] = endTime;
                        } else{
                            //if prev end time before curr start time make new record
                            userUpdates.put(record, locStartEnd);
                            record++;
                        }
                    } else{
                        //if prev location is different from curr location check time difference
                        if(compareDate(prevRecord[2],startTime)>=0){
                            //if prev endTime after curr Start time, prev end time equals curr start time
                            prevRecord[2] = startTime;
                        } 
                        
                        //make new record
                        userUpdates.put(record, locStartEnd);
                        record++;
                    }
                }
            }

            
            //get the people in the same location at the same time
            for(Map.Entry<Integer, String[]> entry : userUpdates.entrySet()){
                int key = entry.getKey();
                String[] userLocStartEnd = userUpdates.get(key);
                String userLoc = userLocStartEnd[0];
                String userFromTime = getTime(userLocStartEnd[1],-5);
                String userToTime = userLocStartEnd[2];
                /*
                for(Map.Entry<Integer, String[]> e : userCompanions.entrySet()){
                    int key1 = e.getKey();
                    String[] array = userCompanions.get(key1);
                    System.out.println(array[0]+" "+array[1]+" "+array[2]);
                }
                System.out.println("break");
                */
                userCompanions.clear();
                
                pstmt = conn.prepareStatement("SELECT TIMESTAMP, MACADDRESS FROM LOCATION \n" +
                                              "WHERE TIMESTAMP >= ? AND TIMESTAMP < ? \n" +
                                              "AND TIMESTAMP >= ? AND TIMESTAMP < ? \n" +
                                              "AND MACADDRESS <> ? AND LOCATIONID = ? \n" +
                                              "ORDER BY MACADDRESS, TIMESTAMP;");
                pstmt.setString(1, dateTimeStart);
                pstmt.setString(2, dateTimeEnd);
                pstmt.setString(3, userFromTime);
                pstmt.setString(4, userToTime);
                pstmt.setString(5, macAddress);
                pstmt.setString(6, userLoc);
                rs = pstmt.executeQuery();
                
                record = 1;
                while(rs.next()){
                    //[macAddress,startTime,endTime]
                    String[] macStartEnd = new String[3];
                    String macAdd = rs.getString(2);
                    String startTime = rs.getString(1);
                    //default ends after 5 mins
                    String endTime = getTime(startTime,5);
                    
                    macStartEnd[0] = macAdd;
                    macStartEnd[1] = startTime;
                    if(compareDate(endTime,dateTimeEnd)>=0){
                        macStartEnd[2] = dateTimeEnd;
                    } else {
                        macStartEnd[2] = endTime;
                    }

                    userCompanions.put(record, macStartEnd);
                    record++;              
                }
                
                //calculate time spent with user
                for(Map.Entry<Integer, String[]> e : userCompanions.entrySet()){
                    int i = e.getKey();
                    String[] userMacStartEnd = userCompanions.get(i);
                    //get next location of companion within time window
                    pstmt = conn.prepareStatement("SELECT MIN(TIMESTAMP) FROM LOCATION \n" +
                                                      "WHERE TIMESTAMP > ? \n" +
                                                      "AND MACADDRESS = ? ");
                    pstmt.setString(1, userMacStartEnd[1]);
                    pstmt.setString(2, userMacStartEnd[0]); 
                    rs = pstmt.executeQuery();
                    
                    if(rs.next()){
                        String nextTime = rs.getString(1);
                        if(nextTime!=null && compareDate(nextTime,userMacStartEnd[2])<=0){
                            userMacStartEnd[2] = nextTime;
                        }
                    }
                    
                    String comMac = userMacStartEnd[0];
                    String comFromTime = userMacStartEnd[1];
                    String comToTime = userMacStartEnd[2];
                    
                    //overlap in seconds
                    userFromTime = userLocStartEnd[1];
                    long overlappedTime = (calculateOverlap(userFromTime,userToTime,comFromTime,comToTime)/ 1000);
                    if(overlappedTime != 0){
                        if(topKC.containsKey(comMac)){
                            topKC.put(comMac, topKC.get(comMac)+overlappedTime);
                        } else {
                            topKC.put(comMac, overlappedTime);
                        }
                    }
                } 
            }
            /*
            for(Map.Entry<Integer, String[]> e : userCompanions.entrySet()){
                    int key1 = e.getKey();
                    String[] array = userCompanions.get(key1);
                    System.out.println(array[0]+" "+array[1]+" "+array[2]);
                }
            System.out.println("break");
            */  
            sortedMapAsc = sortByComparator(topKC, false);
        
            sortedEmails.clear();

            for(Map.Entry<String, Long> e : sortedMapAsc.entrySet()){
                String macAdd = e.getKey();
                pstmt = conn.prepareStatement("SELECT EMAIL FROM DEMOGRAPHICS \n" +
                                              "WHERE MACADDRESS = ?");
                pstmt.setString(1, macAdd);
                rs = pstmt.executeQuery();
                if(rs.next()){
                    String email = rs.getString(1);
                    if(email != null){
                        sortedEmails.add(email);
                    } else {
                        sortedEmails.add(" ");
                    }
                } else {
                    sortedEmails.add(" ");
                }
                
            }
            
            conn.close();
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            
        }
        /*
        for(Map.Entry<Integer, String[]> e : userUpdates.entrySet()){
            int key = e.getKey();
            String[] array = userUpdates.get(key);
            System.out.println(array[0]+" "+array[1]+" "+array[2]);
        }
        
        
        for(Map.Entry<Integer, String[]> e : userCompanions.entrySet()){
            int key = e.getKey();
            String[] array = userCompanions.get(key);
            System.out.println(array[0]+" "+array[1]+" "+array[2]);
        }
        
        
        for(Map.Entry<String, Long> e : topKC.entrySet()){
            String key = e.getKey();
            Long time = topKC.get(key);
            System.out.println(key+" "+time);
        }
        */      
        return sortedMapAsc;
    }
    
    /** 
     * This method checks if the semantic place exists in the database
     * 
     * @param semanticPlace     semantic place to be checked
     * @return                  <code>true</code> if semantic place exist
     *                          <code>false</code> if semantic place does not exist
     */
    public static boolean checkOrigin(String semanticPlace){
        boolean retBool = false;
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT distinct(semanticplace) from location_lookup where semanticplace = ?");
            pstmt.setString(1, semanticPlace);
            ResultSet rs = pstmt.executeQuery();
            
            while(rs.next()){
                if(rs.getString(1).equals(semanticPlace)){
                    retBool = true;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return retBool;
    }
       /**
        * This method sorts the Map in order of "order"
        * @param unsortMap      unsorted Map to be compared with
        * @param order          order to be compared with
        * @return               returns the map of values (Long)
        */
       private static Map<String, Long> sortByComparator(Map<String, Long> unsortMap, final boolean order){

        List<Entry<String, Long>> list = new LinkedList<Entry<String, Long>>(unsortMap.entrySet());
 
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Long>>(){
            public int compare(Entry<String, Long> o1,Entry<String, Long> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
        for (Entry<String, Long> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    
       /**
        * 
        * @param unsortMap      unsorted Map to be compared with
        * @param order          order to be compared with
        * @return               returns the sorted map by value (integer)
        */
    public static Map<String, Integer> sortByComparator2(Map<String, Integer> unsortMap, final boolean order){

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>(){
            public int compare(Entry<String, Integer> o1,Entry<String, Integer> o2){
                if (order){
                    return o1.getValue().compareTo(o2.getValue());
                }else{
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
    /**
     * This method formats the date and time
     * @param dateTime      date and time to be check with
     * @return              returns the date and time in desired format
     */
    public static String formatDate(String dateTime){
        int indexOfT = dateTime.indexOf("T");
        String date = dateTime.substring(0,indexOfT);
        String time = dateTime.substring(indexOfT+1);
        String str = date + " " +time;
        if(time.length()<8){
            str+=":00";
        }
        Date timeStamp = null;
        try{
            timeStamp = format.parse(str);
        } catch (ParseException pe){
        }
        
        return format.format(timeStamp);
    }
    
    /**
     * This method retrieves the start date (15 minutes before dateTimeEnd)
     * @param dateTimeEnd       end date time to be checked with
     * @return                  returns the starting date and time
     */
    public static String getStartDate(String dateTimeEnd){
        Date timeStamp = null;
        Date startDate = null;
        try{
            timeStamp = format.parse(dateTimeEnd);
            Calendar date = Calendar.getInstance();
            date.setTime(timeStamp);
            long t = date.getTimeInMillis();
            //get time 15 before
            startDate = new Date(t-(15*ONE_MINUTE_IN_MILLIS));
            
        } catch (ParseException pe){
            
        }
        
        return format.format(startDate);
    }
    
    /**
     * This method retrieves the start date and time
     * @param currentTime       current time to check with
     * @param minutes           minutes to check with
     * @return                  returns time in the desired format
     */
    public static String getTime(String currentTime, int minutes){
        Date timeStamp = null;
        Date startDate = null;
        try{
            timeStamp = format.parse(currentTime);
            Calendar date = Calendar.getInstance();
            date.setTime(timeStamp);
            long t = date.getTimeInMillis();
            startDate = new Date(t+(minutes*ONE_MINUTE_IN_MILLIS));
            
        } catch (ParseException pe){
            
        }
        
        return format.format(startDate);
    }
    
    /**
     * This method shows the order of the two dates
     * @param time1         date and time to be checked with
     * @param time2         a different date and time to be checked with
     * @return              returns the order of the two different date and time
     */
    public static int compareDate(String time1, String time2){
        Date dTime1 = null;
        Date dTime2 = null;
        int order = 0;
        try{
            dTime1 = format.parse(time1);
            dTime2 = format.parse(time2);
            order = dTime1.compareTo(dTime2);
        } catch (ParseException pe){
            
        }
        return order;
    }
    
    /**
     * This method calculates the overlap between a user and his/her companion
     * @param userFromTime      user to check with from a specified time
     * @param userToTime        user to check with to a specified time
     * @param comFromTime       companion to check from to a specified time
     * @param comToTime         companion to check with to a specified time
     * @return                  returns the overlapping time between the user and companion
     */
    public static long calculateOverlap(String userFromTime,String userToTime,String comFromTime,String comToTime){
        Date userFrom = null;
        Date userTo = null;
        Date comFrom = null;
        Date comTo = null;
        long overlap = 0;
        try{
            userFrom = format.parse(userFromTime);
            userTo = format.parse(userToTime);
            comFrom = format.parse(comFromTime);
            comTo = format.parse(comToTime);
            
            //if no overlap
            if(userTo.before(comFrom) || comTo.before(userFrom)){
                return overlap;
            } 
            
            //full overlap
            if(comFrom.after(userFrom) && comTo.before(userTo)){
                overlap = comTo.getTime()-comFrom.getTime();
                return overlap;
            }
            
            if(userFrom.after(comFrom) && userTo.before(comTo)){
                overlap = userTo.getTime()-userFrom.getTime();
                return overlap;
            }
            
            if(userFrom.equals(comFrom) && userTo.equals(comTo)){
                overlap = userTo.getTime()-userFrom.getTime();
                return overlap;
            }
            
            
            //partial overlap
            if(userFrom.before(comFrom) && userTo.after(comFrom)){
                overlap = userTo.getTime()-comFrom.getTime();
                return overlap;
            }
            
            if(comFrom.before(userFrom) && comTo.after(userFrom)){
                overlap = comTo.getTime()-userFrom.getTime();
                return overlap;
            }
            
            //same start time
            if(comFrom.equals(userFrom)){
                
                if(comTo.before(userTo)){
                    overlap = comTo.getTime()-userFrom.getTime();
                    return overlap;
                } else{
                    overlap = userTo.getTime()-comFrom.getTime();
                    return overlap;
                }
            }
            
            //same end time
            if(comTo.equals(userTo)){
                
                if(comFrom.before(userFrom)){
                    overlap = comTo.getTime()-userFrom.getTime();
                    return overlap;
                } else{
                    overlap = userTo.getTime()-comFrom.getTime();
                    return overlap;
                }
            }
        } catch (ParseException pe){
            
        }
        
        return overlap;
    }
    
    /**
     * This method shows if the Mac address exists or not
     * @param macAddress        Mac address to be checked with
     * @return                  <code>true</code> if Mac address exists
     *                          <code>false</code> if Mac address does not exists
     */
    public static boolean macExists(String macAddress){
        boolean exist = false;
        try{
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement
                                        ("SELECT MACADDRESS FROM LOCATION WHERE MACADDRESS=?");
            pstmt.setString(1, macAddress);
            
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                exist = true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return exist;
    }
}
