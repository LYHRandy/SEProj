/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import se.controller.ReadCSV;
import se.controller.Validate;

/**
 * JsonUpdateFiles output
 */
public class JsonUpdateFiles extends HttpServlet {
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
        HttpSession session = request.getSession();   
        
        ReadCSV.demRowCount = 0;
        ReadCSV.locRowCount = 0;
        
        JsonObject jsonObj = new JsonObject();
        JsonArray countArr = new JsonArray();
        String status = "error";
        
        //Retrieve FilePaths from UploadServlet
        String demFilePath = (String)request.getAttribute("demFilePath");
        String locFilePath = (String)request.getAttribute("locFilePath");
        String folderPath = (String)request.getAttribute("folderFilePath");
        TreeMap<Integer, ArrayList<String>> demErrors = new TreeMap<>();
        TreeMap<Integer, ArrayList<String>> locErrors = new TreeMap<>();
        
        // Use readCSV method to run through the file
        JsonArray errorRowArr = new JsonArray();
        if(!demFilePath.isEmpty()){
            Validate.demErrors = new TreeMap<>();
            demErrors = readCSV.readDemographics(demFilePath,folderPath);
            
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
                errors.add("message",lineErrors);
                errorRowArr.add(errors);
            }
            //session.setAttribute("demRowCount", readCSV.demRowCount);
            //session.setAttribute("demErrors", demErrors);
            Validate.demErrors = null;
        }
        if(!locFilePath.isEmpty()){
            Validate.locErrors =new TreeMap<>();
            locErrors = readCSV.readUpdateLocation(locFilePath, folderPath);
            //session.setAttribute("locRowCount", readCSV.locRowCount);
            //session.setAttribute("locErrors", locErrors);
             for(int row: locErrors.keySet()){
                int key = row;
                ArrayList<String> value = locErrors.get(row);
                JsonArray lineErrors = new JsonArray();
                for(String err : value){
                    lineErrors.add(err);
                }
                JsonObject errors = new JsonObject();
                errors.addProperty("file","demographics.csv");
                errors.addProperty("line",key);
                errors.add("message",lineErrors);
                errorRowArr.add(errors);
            }
            Validate.locErrors = null;
        }
        
        if(demErrors.isEmpty() && locErrors.isEmpty()){
            status = "success";
        }
        
        JsonObject counts = new JsonObject();
        counts.addProperty("demographics.csv",readCSV.demRowCount);
        countArr.add(counts);
        counts = new JsonObject();
        counts.addProperty("location.csv",readCSV.locRowCount);
        countArr.add(counts);
        jsonObj.addProperty("status",status);
        jsonObj.add("num-record-loaded",countArr);

        session.setAttribute("jsonObj",jsonObj);
        response.sendRedirect("jsonPage.jsp");
        
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
