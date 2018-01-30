/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.json;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.*;
import is203.JWTException;
import is203.JWTUtility;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import se.controller.*;
import se.dao.*;

/**
 * Json Bootstrap for file validation
 */
public class JsonBootstrap extends HttpServlet {
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 2;
    private static final String DATA_DIRECTORY = "data";
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 1024;
    public FileValidation fv = new FileValidation();
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
        JsonObject jsonObj = new JsonObject();
        JsonArray errArr = new JsonArray();
        String status = "success";
        RequestDispatcher view = request.getRequestDispatcher("JsonDisplay");
        //String username = (String)session.getAttribute("username");
        //check if there is any field
        
        if(request.getContentType() == null){
            status = "false";
            jsonObj.addProperty("status",status);
            errArr.add("Empty request retrieved");
            jsonObj.add("messages",errArr);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }
        
        ArrayList<String> filenames = new ArrayList<>();
        
        //Configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        
        //sets max memory size
        factory.setSizeThreshold(MAX_MEMORY_SIZE);
        
        //sets directory used to store files bigger than the max memory size
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        
        //Path + name of folder the file will be uploaded to
        String uploadFolder = getServletContext().getRealPath("") + File.separator + DATA_DIRECTORY;
        
        //create directory if it does not currently exist
        File uploadDir = new File(uploadFolder);
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }
        //create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        //set the max size of request
        upload.setSizeMax(MAX_REQUEST_SIZE);
        
        
        
        try{
            
            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
            while(iter.hasNext()){
                FileItem item = (FileItem) iter.next();
                if(item.isFormField()){
                    String token = item.getString();
                    try{
                        String username = JWTUtility.verify(token, sharedSecret);
                        if(!username.equals("admin")){
                            status = "error";
                            errArr.add("Non-admin User, Invalid admin token");
                        }
                    }catch(JWTException e){
                    status = "error";
                    errArr.add("invalid token");
                    }
                }
                else{     
                    File file = new File(item.getName());    
                    String fileName = file.getName();
                    //Check if there is a file
                    String errorMsg = fv.checkEmpty(fileName);
                    if(!errorMsg.equals("")){
                        errArr.add(errorMsg);
                        status = "error";
                    }
                    //Check if is a zip file
                    errorMsg = fv.checkExtension(fileName);
                    if(!errorMsg.equals("")){
                        errArr.add(errorMsg);
                        status = "error";
                    }
                    //Create a new filepath for the uploaded file
                    String filePath = uploadFolder + File.separator +fileName;
                    File uploadFile = new File(filePath);
                    
                    //Writes data into the disk
                    item.write(uploadFile);
                    
                    //Create new zip input stream to read zip content
                    ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));

                    //Get next entry in the zip file
                    ZipEntry ze = zis.getNextEntry();

                    byte[] buffer = new  byte[1024];
                    
                    //while zip has more content
                    while (ze != null){
                        //Get current file name
                        String entryName = ze.getName();
                        filenames.add(entryName);
                        
                        //Create directory for the new file
                        File newFile = new File(uploadFolder + File.separator + entryName);
                        FileOutputStream fos = new FileOutputStream(newFile);

                        //Transfer data from zip file to output file
                        int len;
                        while((len = zis.read(buffer)) > 0){
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                        zis.closeEntry();
                        //Get next entry
                        ze = zis.getNextEntry();
                    } 
                }
            }
            if(status.equals("error")){
                jsonObj.addProperty("status",status);
                jsonObj.add("messages",errArr);
                request.setAttribute("jsonObj",jsonObj);
                view.forward(request, response);
            }
            if(filenames.contains("demographics.csv") && filenames.contains("location-lookup.csv") && filenames.contains("location.csv")){
                //Define filepath
                String demFilePath = uploadFolder + File.separator + "demographics.csv";
                String locLookupFilePath = uploadFolder + File.separator + "location-lookup.csv";
                String locFilePath = uploadFolder + File.separator + "location.csv";

                //Forward filepath to bootstrap
                RequestDispatcher rd = request.getRequestDispatcher("JsonBootstrapFiles");

                request.setAttribute("demFilePath", demFilePath);
                request.setAttribute("locLookupFilePath", locLookupFilePath);
                request.setAttribute("locFilePath", locFilePath);
                request.setAttribute("folderFilePath", uploadFolder);

                rd.forward(request, response);
                return;
            }
            
            errArr.add("Zip contains invalid files.");
            status = "error";
            jsonObj.addProperty("status",status);
            jsonObj.add("messages",errArr);
            request.setAttribute("jsonObj",jsonObj);
            view.forward(request, response);
        }    catch (FileUploadException fue){
            throw new ServletException(fue);
        } catch (Exception ex){
            throw new ServletException(ex);
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
