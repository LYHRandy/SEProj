/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;

import com.google.gson.*;import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.util.*;
import javax.servlet.RequestDispatcher;
import se.dao.*;
/**
 * JSONAGD output
 */
public class JsonAGD extends HttpServlet {

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
        //HttpSession session = request.getSession();
        String sharedSecret = "SEG4T4PXsHrLcNsK";
        
        String dateTime = request.getParameter("date");
        String token = request.getParameter("token");
        //String username = (String)session.getAttribute("username");
        RequestDispatcher view = request.getRequestDispatcher("JsonDisplay");
        
        JsonObject jsonObj = new JsonObject();
        ArrayList<String> errArr = new ArrayList<>();
        String status = "success";

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
        
        if(!dateTime.contains("T")){
            status = "error";
            errArr.add("invalid date");
        }
        if(dateTime != null && dateTime.contains("T")){
            String date = dateTime.replace("T"," ");
            SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        
        Map<ArrayList<String>,TreeMap<String,Integer>> agd = agdDAO.retrieveAGD(dateTime);
        
        JsonArray groupsArr = new JsonArray();
        
        int numPeople = agdDAO.getNumPpl(topKDAO.formatDate(dateTime));
        
        jsonObj.addProperty("total-users",numPeople);
        jsonObj.addProperty("total-groups",agd.size());
        
        Map<String,JsonObject> sortBySize = new TreeMap<String,JsonObject>(Collections.reverseOrder());
        int tracker = 1;
        String members = "";
        
        Set<ArrayList<String>> strKey = agd.keySet();
        
        ArrayList<ArrayList<String>> keyList = new ArrayList<>();
        
        for(ArrayList<String> sList : strKey){
            keyList.add(sList);
        }
        
        Collections.sort(keyList,new EmailMacAddComparator());
        
        for(int i = 0; i < agd.size(); i++){
            JsonObject groupObj = new JsonObject();
            ArrayList<String> key = keyList.get(i);
            groupObj.addProperty("size",key.size());
            JsonArray memArr = new JsonArray();
            TreeMap<String,Integer> locCount = agd.get(key);
            for(String str : key){
                String [] eMac = str.split(",");
                JsonObject mem = new JsonObject();
                members += eMac[0];
                mem.addProperty("email",eMac[0]);
                mem.addProperty("mac-address", eMac[1]);
                memArr.add(mem);
            }
            JsonArray locArr = new JsonArray();
            int totalTime = 0;
            for(Map.Entry<String, Integer> el : locCount.entrySet()){
                String locID = el.getKey();
                int count = locCount.get(locID);
                JsonObject locations = new JsonObject();
                locations.addProperty("location",locID);
                locations.addProperty("time-spent", count);
                locArr.add(locations);
                totalTime += count;
            }
            groupObj.addProperty("total-time-spent",totalTime);
            groupObj.add("members",memArr);
            groupObj.add("locations",locArr);
            sortBySize.put(""+key.size()+totalTime+tracker+members,groupObj);
            tracker++;
            members = "";
        }
       
        for(Map.Entry<String,JsonObject> e : sortBySize.entrySet()){
            String key = e.getKey();
            JsonObject groupObj = sortBySize.get(key);
            groupsArr.add(groupObj);
        }
        jsonObj.add("groups",groupsArr);
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
