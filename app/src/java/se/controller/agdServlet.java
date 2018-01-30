/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.dao.agdDAO;
import se.dao.topKDAO;

/**
 * The servlet that does processing for the Automatic Group Detection
 */
@WebServlet(name = "agdServlet", urlPatterns = {"/agdServlet"})


public class agdServlet extends HttpServlet {

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
        
        //retrieve parameters
        String date = request.getParameter("date");
        String time = request.getParameter("time");
        
        //validate date
        if(date.isEmpty() || time.isEmpty() || date==null || time==null){
                request.setAttribute("errorMsg", "Please fill in date");
                RequestDispatcher view =request.getRequestDispatcher("TopKC.jsp");
                view.forward(request,response);
                return;
        }
        
        //format date
        String dateTime = date+"T"+time;
        
        //retrieve valid groups
        Map<ArrayList<String>,TreeMap<String,Integer>> agd = agdDAO.retrieveAGD(dateTime);
        
        request.setAttribute("dateTime",dateTime);
        request.setAttribute("numPpl",agdDAO.numPpl);
        request.setAttribute("agd",agd);
        RequestDispatcher view =request.getRequestDispatcher("agd.jsp");
        view.forward(request,response);
        
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
