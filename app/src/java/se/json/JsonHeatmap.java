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
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.dao.HeatmapDAO;

/**
 * JsonHeatmap output
 */
public class JsonHeatmap extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        String sharedSecret = "SEG4T4PXsHrLcNsK";
        //HttpSession session = request.getSession();
        String floor = request.getParameter("floor");
        String dateTime = request.getParameter("date");
        //String username = (String)session.getAttribute("username");
        RequestDispatcher view = request.getRequestDispatcher("JsonDisplay");
        
        JsonObject jsonObj = new JsonObject();
        ArrayList<String> errArr = new ArrayList<>();
        String status = "success";

        int floorNum = -1;
        String strFloor = "";
        if(floor == null){
            status = "error";
            errArr.add("missing floor");
        } else if (floor.isEmpty()){
            status = "error";
            errArr.add("blank floor");
        }
        
        if(dateTime == null){
            status = "error";
            errArr.add("missing date");
        }
        else if(dateTime.equals("")){
            status = "error";
            errArr.add("blank date");
        }
        
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
        
        if(floor!=null && !floor.isEmpty()){
            try{
                floorNum = Integer.parseInt(floor);
                if(floorNum < 0 || floorNum > 5){
                    status = "error";
                    errArr.add("invalid floor");
                }
            }catch(NumberFormatException nfe){
                status = "error";
                errArr.add("invalid floor");
            }
        }
        
        switch(floorNum){
            case 0:
                strFloor = "B1";
                break;
            case 1:
                strFloor = "L1";
                break;
            case 2:
                strFloor = "L2";
                break;
            case 3:
                strFloor = "L3";
                break;
            case 4:
                strFloor = "L4";
                break;
            case 5:
                strFloor = "L5";
                break;
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
        
        HashMap<Integer,String[]> heatmap = HeatmapDAO.retrievebyQuery(strFloor,dateTime);
        JsonArray resultArr = new JsonArray();
        
        if(heatmap.isEmpty()){
            jsonObj.add("heatmap",resultArr);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
        // Key > Semantic Place, Value > Array[ no.of people, density ]

        for (int record: heatmap.keySet()){  
            JsonObject results = new JsonObject();
            String[] semNumDen = heatmap.get(record);
            
            String semPlace = semNumDen[0];
            int numPpl = Integer.parseInt(semNumDen[1]);
            int density = Integer.parseInt(semNumDen[2]);
            
            results.addProperty("semantic-place", semPlace);
            results.addProperty("num-people",numPpl);
            results.addProperty("crowd-density",density);
            
            resultArr.add(results);
        }
        jsonObj.add("heatmap",resultArr);
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
