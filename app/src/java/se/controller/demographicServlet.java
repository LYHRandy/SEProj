/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import se.dao.demographicDAO;
/**
 * The servlet that does processing for breakdown by year and gender
 */
public class demographicServlet extends HttpServlet {

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
        
        //Retrieve user's demographic query inputs
        String date = request.getParameter("date");
        String time = request.getParameter("time");
        String queryOne = request.getParameter("query1");
        String queryTwo = request.getParameter("query2");
        String queryThree = request.getParameter("query3");
        int numQueries = 0;
        String query = "";
        
        //Checks for user input error(s)
        if(date.isEmpty() || time.isEmpty()){
            request.setAttribute("errorMsg","Please key in the date and time");
            RequestDispatcher view =request.getRequestDispatcher("demographics.jsp");
            view.forward(request,response);
            return;
        }
        
        //if there are no fields input
        if(queryOne.isEmpty()){
            request.setAttribute("errorMsg", "Please select the first query order");
            RequestDispatcher view =request.getRequestDispatcher("demographics.jsp");
            view.forward(request,response);
            return;
        }
        
        //append queries
        query += queryOne;
        
        //keep track of number of queries
        numQueries++;
        
        //if there is no queryTwo but there is queryThree
        if(queryTwo.isEmpty() && !queryThree.isEmpty()){
            request.setAttribute("errorMsg", "Please select the queries in order");
            RequestDispatcher view =request.getRequestDispatcher("demographics.jsp");
            view.forward(request,response);
            return;
        }
        
        if(!queryTwo.isEmpty()){
            //if queries have same value
            if(queryOne.equals(queryTwo) || queryTwo.equals(queryThree) || queryThree.equals(queryOne)){
                request.setAttribute("errorMsg", "Queries cannot have same values");
                RequestDispatcher view =request.getRequestDispatcher("demographics.jsp");
                view.forward(request,response);
                return;
            }
            query+=(","+queryTwo);
            numQueries++;
        }
        
        if(!queryThree.isEmpty()){
            query+=(","+queryThree);
            numQueries++;
        }
        
        //format date
        String datetime = date+"T"+time;
        
        //retrieve demographic breakdown results
        LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,Integer>>> demoMap = demographicDAO.queryBy(datetime, queryOne, queryTwo, queryThree);
        
        request.setAttribute("dateTime",datetime);
        request.setAttribute("numQueries",numQueries);
        request.setAttribute("total",demographicDAO.totalNo);
        request.setAttribute("query", query);
        request.setAttribute("demoMap",demoMap);
        RequestDispatcher view =request.getRequestDispatcher("demographics.jsp");
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
