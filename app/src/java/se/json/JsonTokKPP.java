/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;

import com.google.gson.*;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import javax.servlet.RequestDispatcher;
import se.dao.*;

/**
 * JsonTopKPP output
 */
public class JsonTokKPP extends HttpServlet {

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
        String kVal = request.getParameter("k");
        //String username = (String)session.getAttribute("username");
        RequestDispatcher view = request.getRequestDispatcher("JsonDisplay");
        
        JsonObject jsonObj = new JsonObject();
        ArrayList<String> errArr = new ArrayList<String>();
        String status = "success";
        
        String token = request.getParameter("token");
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
        
        if(dateTime == null){
            status = "error";
            errArr.add("missing date");
        }
        else if(dateTime.equals("")){
            status = "error";
            errArr.add("blank date");
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
        
        int kValue = 0;
        if(kVal == null || kVal.isEmpty()){
            kValue = 3;
        }
        else{
            try{
                kValue = Integer.parseInt(kVal);
                if(kValue >10 || kValue<1){
                    status = "error";
                    errArr.add("invalid k");
                }
            }catch(NumberFormatException nfe){
                status = "error";
                errArr.add("invalid k");  
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
        TreeMap<Integer,ArrayList<String>> topKPP = topKDAO.retrieveTopKPP(kValue, dateTime);
        JsonArray resultArr = new JsonArray();
        
        if(topKPP == null){
            jsonObj.add("results",resultArr);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
        
        int rank = 1;
        int row = 1;
        
        while(row<=topKPP.size() && rank <= kValue && rank <= topKPP.size()){
            JsonObject results = new JsonObject();
            ArrayList<String> currRow = topKPP.get(row);
            results.addProperty("rank", rank);
            results.addProperty("semantic-place",currRow.get(0));
            results.addProperty("count",Integer.parseInt(currRow.get(1)));
            int nextRowNum = row + 1;
            ArrayList<String> nextRow = topKPP.get(nextRowNum);
            resultArr.add(results);
            while(nextRow!=null && nextRow.get(1).equals(currRow.get(1))){
                results = new JsonObject();
                results.addProperty("rank", rank);
                results.addProperty("semantic-place",nextRow.get(0));
                results.addProperty("count",Integer.parseInt(nextRow.get(1)));
                nextRow = topKPP.get(++nextRowNum);
                resultArr.add(results);
            }
            row = nextRowNum;
            rank++;
        }
        
        jsonObj.add("results",resultArr);
        request.setAttribute("jsonObj",jsonObj);
        view.forward(request, response);
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
