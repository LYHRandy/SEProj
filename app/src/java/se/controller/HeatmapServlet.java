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
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.dao.HeatmapDAO;

/**
 * The servlet that processes the data for the heatmap function
 */
public class HeatmapServlet extends HttpServlet {

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
        
            //Retrieve user's heatmap inputs
            String date = request.getParameter("date");
            String time = request.getParameter("time");
            String floor = request.getParameter("floor");
            
            //Validates floor input
            int floorNum = -1;
            String strFloor = "";
            if(floor == null){
                request.setAttribute("errorMsg", "Floor missing");
                RequestDispatcher view =request.getRequestDispatcher("heatmap.jsp");
                view.forward(request,response);
                return;
            } else if (floor.isEmpty()){
                request.setAttribute("errorMsg", "Floor blank");
                RequestDispatcher view =request.getRequestDispatcher("heatmap.jsp");
                view.forward(request,response);
                return;
            }else{
                try{
                    floorNum = Integer.parseInt(floor);
                    if(floorNum < 0 || floorNum > 5){
                        request.setAttribute("errorMsg", "Floor invalid");
                        RequestDispatcher view =request.getRequestDispatcher("heatmap.jsp");
                        view.forward(request,response);
                        return;
                    }
                }catch(NumberFormatException nfe){
                    request.setAttribute("errorMsg", "Floor invalid");
                    RequestDispatcher view =request.getRequestDispatcher("heatmap.jsp");
                    view.forward(request,response);
                    return;
                }
            }
            
            //Converts input to floor string
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
            
            //Check for input error(s)
            if(date.isEmpty() || time.isEmpty() || date==null || time==null){
                request.setAttribute("errorMsg", "Please fill in date");
                RequestDispatcher view =request.getRequestDispatcher("heatmap.jsp");
                view.forward(request,response);
                return;
            }
            String dateTime = date+"T"+time;
            HashMap<Integer,String[]> heatMap = HeatmapDAO.retrievebyQuery(strFloor,dateTime);
            request.setAttribute("heatMap", heatMap);
            request.setAttribute("floor", strFloor);
            request.setAttribute("date", dateTime);
            RequestDispatcher view =request.getRequestDispatcher("heatmap.jsp");
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
