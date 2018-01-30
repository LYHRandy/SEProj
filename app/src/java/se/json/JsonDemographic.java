/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.dao.demographicDAO;

/**
 * JsonDemographic output
 */
public class JsonDemographic extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        String sharedSecret = "SEG4T4PXsHrLcNsK";
        //HttpSession session = request.getSession();
        String dateTime = request.getParameter("date");
        String order = request.getParameter("order");
        String token = request.getParameter("token");
        //String username = (String)session.getAttribute("username");
        RequestDispatcher view = request.getRequestDispatcher("JsonDisplay");
        
        JsonObject jsonObj = new JsonObject();
        ArrayList<String> errArr = new ArrayList<String>();
        String status = "success";

        String[] queries = order.split(",");
        String queryOne = "";
        String queryTwo = "";
        String queryThree = "";
        int numQueries = 0;
        int arrSize = queries.length;
        
        if(order == null){
            status = "error";
            errArr.add("missing order");
        } else if(order.equals("")){
            status = "error";
            errArr.add("blank order");
        }
        
        if(dateTime == null){
            status = "error";
            errArr.add("missing date");
        }
        else if(dateTime.equals("")){
            status = "error";
            errArr.add("blank date");
        }
        
        if(token == null){
            status = "error";
            errArr.add("invalid token");
        }
        else if(token.equals("")){
            status = "error";
            errArr.add("invalid token");
        } else {
            try{
                JWTUtility.verify(token, sharedSecret);
            } catch(JWTException e){
                status = "error";
                errArr.add("invalid token");
            }
        }
        
        if(status.equals("error")){
            Collections.sort(errArr);
            JsonArray errors = new JsonArray();
            for(String str : errArr){
                errors.add(str);
            }
            jsonObj.addProperty("status",status);
            jsonObj.add("messages",errors);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
        
        if(order != null && !order.equals("")){
            queries = order.split(",");
            arrSize = queries.length;
        
            if(arrSize<1 || arrSize >3){
                status = "error";
                errArr.add("invalid order");
            }

            if(arrSize == 1){
                if(!(queries[0].equals("year") || queries[0].equals("gender") || queries[0].equals("school"))){
                    status = "error";
                    errArr.add("invalid order");
                }
                queryOne = queries[0];
                numQueries = 1;
            }

            if(arrSize == 2){
                if(!(queries[0].equals("year") || queries[0].equals("gender") || queries[0].equals("school")) ||
                   !(queries[1].equals("year") || queries[1].equals("gender") || queries[1].equals("school"))){
                    status = "error";
                    errArr.add("invalid order");
                }
                queryOne = queries[0];
                queryTwo = queries[1];
                numQueries = 2;
            }

            if(arrSize == 3){
                if(!(queries[0].equals("year") || queries[0].equals("gender") || queries[0].equals("school")) ||
                   !(queries[1].equals("year") || queries[1].equals("gender") || queries[1].equals("school")) ||
                   !(queries[2].equals("year") || queries[2].equals("gender") || queries[2].equals("school"))){
                    status = "error";
                    errArr.add("invalid order");
                }
                queryOne = queries[0];
                queryTwo = queries[1];
                queryThree = queries[2];
                numQueries = 3;
            }

            if(status.equals("success") && arrSize >=2){
                if(queryOne.equals(queryTwo) || queryTwo.equals(queryThree) || queryThree.equals(queryOne)){
                    status = "error";
                    errArr.add("invalid order");
                }
            }
        }
        
        if(!dateTime.contains("T")){
            status = "error";
            errArr.add("invalid date");
        }
        
        if(dateTime != null && dateTime.contains("T")){
            String date = dateTime.replace("T"," ");
            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setLenient(false);
            try{
                format.parse(date);
            }catch(ParseException pe){
                status = "error";
                errArr.add("invalid date");
            }
        }
        
        jsonObj.addProperty("status",status);
        if(status.equals("error")){
            Collections.sort(errArr);
            JsonArray errors = new JsonArray();
            for(String str : errArr){
                errors.add(str);
            }
            jsonObj.add("messages",errors);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
        LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,Integer>>> demoMap = demographicDAO.queryBy(dateTime, queryOne, queryTwo, queryThree);
        if(numQueries == 1){
            JsonArray arrOne = new JsonArray();
            for(Map.Entry<String,LinkedHashMap<String,LinkedHashMap<String,Integer>>> e : demoMap.entrySet()){
                JsonObject results = new JsonObject();
                String key1 = e.getKey();
                String[] splitKey = key1.split(",");
                
                if(queryOne.equals("year")){
                    String field = splitKey[0];
                    int field1 = Integer.parseInt(field);
                    results.addProperty(queryOne, field1);
                }else{
                    String field1 = splitKey[0];
                    results.addProperty(queryOne, field1);
                }
                int count1 = Integer.parseInt(splitKey[1]);
                results.addProperty("count", count1);
                arrOne.add(results);
            }
            jsonObj.add("breakdown", arrOne);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
        if(numQueries == 2){
            JsonArray arrOne = new JsonArray();
            for(Map.Entry<String,LinkedHashMap<String,LinkedHashMap<String,Integer>>> e : demoMap.entrySet()){
                JsonObject results = new JsonObject();
                String key1 = e.getKey();
                String[] splitKey = key1.split(",");
                if(queryOne.equals("year")){
                    String field = splitKey[0];
                    int field1 = Integer.parseInt(field);
                    results.addProperty(queryOne, field1);
                }else{
                    String field1 = splitKey[0];
                    results.addProperty(queryOne, field1);
                }
                int count1 = Integer.parseInt(splitKey[1]);
                results.addProperty("count", count1);
                
                LinkedHashMap<String,LinkedHashMap<String,Integer>> secondQuery = demoMap.get(key1);
                JsonArray arrTwo = new JsonArray();
                for(Map.Entry<String,LinkedHashMap<String,Integer>> e2 : secondQuery.entrySet()){
                    JsonObject results2 = new JsonObject();
                    String key2 = e2.getKey();
                    String[] splitKey2 = key2.split(",");
                    if(queryTwo.equals("year")){
                    String field = splitKey2[0];
                    int field2 = Integer.parseInt(field);
                    results2.addProperty(queryTwo, field2);
                }else{
                    String field2 = splitKey2[0];
                    results2.addProperty(queryTwo, field2);
                }
                    int count2 = Integer.parseInt(splitKey2[1]);
                    results2.addProperty("count", count2);
                    arrTwo.add(results2);
                }
                results.add("breakdown", arrTwo);
                arrOne.add(results);  
            }
            jsonObj.add("breakdown", arrOne);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
        if(numQueries == 3){
            JsonArray arrOne = new JsonArray();
            for(Map.Entry<String,LinkedHashMap<String,LinkedHashMap<String,Integer>>> e : demoMap.entrySet()){
                JsonObject results = new JsonObject();
                String key1 = e.getKey();
                String[] splitKey = key1.split(",");
                if(queryOne.equals("year")){
                    String field = splitKey[0];
                    int field1 = Integer.parseInt(field);
                    results.addProperty(queryOne, field1);
                }else{
                    String field1 = splitKey[0];
                    results.addProperty(queryOne, field1);
                }
                int count1 = Integer.parseInt(splitKey[1]);
                results.addProperty("count", count1);
                
                LinkedHashMap<String,LinkedHashMap<String,Integer>> secondQuery = demoMap.get(key1);
                JsonArray arrTwo = new JsonArray();
                for(Map.Entry<String,LinkedHashMap<String,Integer>> e2 : secondQuery.entrySet()){
                    JsonObject results2 = new JsonObject();
                    String key2 = e2.getKey();
                    String[] splitKey2 = key2.split(",");
                    if(queryTwo.equals("year")){
                        String field = splitKey[0];
                        int field2 = Integer.parseInt(field);
                        results2.addProperty(queryTwo, field2);
                    }else{
                        String field2 = splitKey2[0];
                        results2.addProperty(queryTwo, field2);
                    }
                    int count2 = Integer.parseInt(splitKey2[1]);
                    results2.addProperty("count", count2);
                    
                    LinkedHashMap<String,Integer> thirdQuery = secondQuery.get(key2);
                    JsonArray arrThree = new JsonArray();
                    for(Map.Entry<String,Integer> e3 : thirdQuery.entrySet()){
                        JsonObject results3 = new JsonObject();
                        String key3 = e3.getKey();
                        if(queryThree.equals("year")){
                            int field3 = Integer.parseInt(key3);
                            results3.addProperty(queryThree, field3);
                        }else{
                            results3.addProperty(queryThree, key3);
                        }
                        int count3 = thirdQuery.get(key3);
                        results3.addProperty("count", count3);
                        arrThree.add(results3);
                    }
                    results2.add("breakdown", arrThree);
                    arrTwo.add(results2);
                }
                results.add("breakdown", arrTwo);
                arrOne.add(results);  
            }
            jsonObj.add("breakdown", arrOne);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
