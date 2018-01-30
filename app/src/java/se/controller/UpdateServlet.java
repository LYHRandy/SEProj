/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FileUtils;
import se.dao.LocationDAO;
import se.dao.LocationLookupDAO;
import se.dao.UserDAO;

/**
 * The servlet that processes the file update for new files
 */
public class UpdateServlet extends HttpServlet {
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
        HttpSession session = request.getSession();   
        
        //reset row counts
        ReadCSV.demRowCount = 0;
        ReadCSV.locRowCount = 0;
        
        //Retrieve FilePaths from UploadServlet
        String demFilePath = (String)request.getAttribute("demFilePath");
        String locFilePath = (String)request.getAttribute("locFilePath");
        String folderPath = (String)request.getAttribute("folderFilePath");
        
        //initialise error maps
        Validate.demErrors = new TreeMap<>();
        Validate.locErrors =new TreeMap<>();
        Validate.verifiedLocations = new HashMap<>();
        
        //Use readCSV method to run through the file if files exist
        if(!demFilePath.isEmpty()){
            Validate.demErrors = new TreeMap<>();
            TreeMap<Integer, ArrayList<String>> demErrors = readCSV.readDemographics(demFilePath,folderPath);
            session.setAttribute("demRowCount", readCSV.demRowCount);
            session.setAttribute("demErrors", demErrors);
            Validate.demErrors = null;
        }
        if(!locFilePath.isEmpty()){
            Validate.locErrors =new TreeMap<>();
            TreeMap<Integer, ArrayList<String>> locErrors = readCSV.readUpdateLocation(locFilePath, folderPath);
            session.setAttribute("locRowCount", readCSV.locRowCount);
            session.setAttribute("locErrors", locErrors);
            Validate.locErrors = null;
        }
        
        response.sendRedirect("updated.jsp");
        
        try{
            FileUtils.deleteDirectory(new File(folderPath));
        } catch(IOException e){
            e.printStackTrace();
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
