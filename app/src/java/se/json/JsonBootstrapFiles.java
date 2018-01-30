 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import se.controller.ReadCSV;
import se.controller.Validate;
import se.dao.LocationDAO;
import se.dao.LocationLookupDAO;
import se.dao.UserDAO;
import se.controller.*;

/**
 * JsonBootstrapofFiles for JSONoutput
 */
public class JsonBootstrapFiles extends HttpServlet {
    ReadCSV readCSV = new ReadCSV();
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
        //HttpSession session = request.getSession();
        //Clear Database
        UserDAO.deleteAll();
        LocationDAO.deleteAll();
        LocationLookupDAO.deleteAll();
        ReadCSV.demRowCount = 0;
        ReadCSV.locRowCount = 0;
        ReadCSV.locLookupRowCount = 0;
        
        JsonObject jsonObj = new JsonObject();
        JsonArray countArr = new JsonArray();
        String status = "error";
        RequestDispatcher view = request.getRequestDispatcher("JsonDisplay");
        
        //Retrieve FilePaths from UploadServlet
        String demFilePath = (String)request.getAttribute("demFilePath");
        String locLookupFilePath = (String)request.getAttribute("locLookupFilePath");
        String locFilePath = (String)request.getAttribute("locFilePath");
        String folderPath = (String)request.getAttribute("folderFilePath");
        
        // Use readCSV method to run through the file
        Validate.demErrors = new TreeMap<>();
        Validate.locErrors =new TreeMap<>();
        Validate.locLookupErrors = new TreeMap<>();
        Validate.verifiedLocations = new HashMap<>();
        TreeMap<Integer, ArrayList<String>> demErrors = readCSV.readDemographics(demFilePath,folderPath);
        TreeMap<Integer, ArrayList<String>> locLookupErrors = readCSV.readLocationLookup(locLookupFilePath, folderPath);
        TreeMap<Integer, ArrayList<String>> locErrors = readCSV.readLocation(locFilePath, folderPath);
        
        
        if(demErrors.isEmpty() && locLookupErrors.isEmpty() && locErrors.isEmpty()){
            status = "success";
        }
        JsonArray errorRowArr = new JsonArray();
        for(int row: demErrors.keySet()){
            int key = row;
            ArrayList<String> value = demErrors.get(row);
            JsonArray lineErrors = new JsonArray();
            for(String err : value){
                lineErrors.add(err);
            }
            JsonObject errors = new JsonObject();
            errors.addProperty("file","demographics.csv");
            errors.addProperty("line",key);
            errors.add("messages",lineErrors);
            errorRowArr.add(errors);
        }
        for(int row: locLookupErrors.keySet()){
            int key = row;
            ArrayList<String> value = locLookupErrors.get(row);
            JsonArray lineErrors = new JsonArray();
            for(String err : value){
                lineErrors.add(err);
            }
            JsonObject errors = new JsonObject();
            errors.addProperty("file","location-lookup.csv");
            errors.addProperty("line",key);
            errors.add("messages",lineErrors);
            errorRowArr.add(errors);
        }
        for(int row: locErrors.keySet()){
            int key = row;
            ArrayList<String> value = locErrors.get(row);
            JsonArray lineErrors = new JsonArray();
            for(String err : value){
                lineErrors.add(err);
            }
            JsonObject errors = new JsonObject();
            errors.addProperty("file","location.csv");
            errors.addProperty("line",key);
            errors.add("messages",lineErrors);
            errorRowArr.add(errors);
        }
        jsonObj.addProperty("status",status);
        JsonObject counts = new JsonObject();
        counts.addProperty("demographics.csv",readCSV.demRowCount);
        countArr.add(counts);
        counts = new JsonObject();
        counts.addProperty("location-lookup.csv",readCSV.locLookupRowCount);
        countArr.add(counts);
        counts = new JsonObject();
        counts.addProperty("location.csv",readCSV.locRowCount);
        countArr.add(counts);
        jsonObj.add("num-record-loaded",countArr);
        Validate.demErrors = null;
        Validate.locErrors = null;
        Validate.locLookupErrors = null;
        Validate.verifiedLocations = null;
        
        if(errorRowArr.size()!= 0){
            jsonObj.add("error",errorRowArr);
        }
        
        request.setAttribute("jsonObj",jsonObj);
        view.forward(request, response);
        
        
        
        FileUtils.deleteDirectory(new File(folderPath));
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
