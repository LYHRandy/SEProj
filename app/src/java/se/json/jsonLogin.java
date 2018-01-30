/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;

import com.google.gson.*;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.dao.UserDAO;

/**
 * JsonLogin output
 */
public class jsonLogin extends HttpServlet {

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
        
        //Retrieve user's heatmap inputs
        String username = request.getParameter("username");
        String password = request.getParameter("password");
            
        JsonObject jsonObj = new JsonObject();
        String state = "error";
        String sharedSecret = "SEG4T4PXsHrLcNsK";
        String token = null;
        JsonArray jsonArr = new JsonArray();
        
        int status = 0;
        if(username == null){
            jsonArr.add("invalid username/password");
            
        }
        if(password == null){
            jsonArr.add("invalid username/password");
        }
        if(username != null || password != null){
            status = UserDAO.checkUser(username.trim(),password.trim());
            if(status!=0){
                state = "success";
                token = JWTUtility.sign(sharedSecret, username);
            }
            jsonObj.addProperty("status",state);
            if(token != null){
                jsonObj.addProperty("token",token);
            }
            else{
                jsonArr.add("invalid username/password");
            }
            //session.setAttribute("jsonObj",jsonObj);
            //session.setAttribute("token",token);
            //session.setAttribute("username",username);
            //response.sendRedirect("jsonPage.jsp");
        }
        
        RequestDispatcher view = request.getRequestDispatcher("JsonDisplay");
        if(state.equals("error")){
            jsonObj.add("messages",jsonArr);
        }
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
        //processRequest(request, response);
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
