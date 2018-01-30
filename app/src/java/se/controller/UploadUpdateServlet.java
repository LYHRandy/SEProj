/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * The servlet that does unziping and processing for new files to update the database
 */
public class UploadUpdateServlet extends HttpServlet {
    private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 2;
    private static final String DATA_DIRECTORY = "data";
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 1024;
    private FileValidation fv = new FileValidation();
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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UploadUpdateServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UploadUpdateServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        //processRequest(request, response);
                ArrayList<String> filenames = new ArrayList<>();
        //Checks if is a multipart
        if(!fv.fileMultiPart(request)){
            return;
        }
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
                
                if(!item.isFormField()){
                    File file = new File(item.getName());    
                    String fileName = file.getName();
                    //Check if there is a file
                    String errorMsg = fv.checkEmpty(fileName);
                    if(!errorMsg.equals("")){
                        request.setAttribute("errorMsg", errorMsg);
                        RequestDispatcher view = request.getRequestDispatcher("update.jsp");
                        view.forward(request,response);
                        return;
                    }
                    //Check if is a zip file
                    errorMsg = fv.checkExtension(fileName);
                    if(!errorMsg.equals("")){
                        request.setAttribute("errorMsg", errorMsg);
                        RequestDispatcher view = request.getRequestDispatcher("update.jsp");
                        view.forward(request,response);
                        return;
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
            
            if((filenames.contains("demographics.csv") || filenames.contains("location.csv"))){
                //Define filepath
                String demFilePath = "";
                String locFilePath = "";
                
                if(filenames.contains("demographics.csv")){
                    demFilePath = uploadFolder + File.separator + "demographics.csv";
                }
                
                if(filenames.contains("location.csv")){
                    locFilePath = uploadFolder + File.separator + "location.csv";
                }
                //Forward filepath to bootstrap
                RequestDispatcher view = request.getRequestDispatcher("UpdateServlet");

                request.setAttribute("demFilePath", demFilePath);
                request.setAttribute("locFilePath", locFilePath);
                request.setAttribute("folderFilePath", uploadFolder);

                view.forward(request, response);
                return;
            }    
            String errorMsg = "Zip contains invalid files.";
            request.setAttribute("errorMsg", errorMsg);
            RequestDispatcher view = request.getRequestDispatcher("update.jsp");
            view.forward(request,response);
            
        } catch (FileUploadException fue){
            throw new ServletException(fue);
        } catch (Exception ex){
            throw new ServletException(ex);
        } 
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
