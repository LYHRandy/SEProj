/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.controller;

import java.io.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import java.util.*;
import javax.servlet.http.*;

/**
 * The class containing common methods for validation of the different files
 */
public class FileValidation {
    
    /**
     * Checks if file type is multipart
     * 
     * @param request request contents to be check
     * @return        if request is a multipart content  
     */
    public boolean fileMultiPart(HttpServletRequest request){
        return ServletFileUpload.isMultipartContent(request);
    }
    
    /**
     * Check if file is empty
     * 
     * @param fileName  file to be checked
     * @return          error message
     */
    public String checkEmpty(String fileName){
        fileName = fileName.trim();
        String msg = "";
        if(fileName.isEmpty()){
            msg = "No File Present";
        }
        return msg;
    }
    /**
     * Check if extension is .zip
     * 
     * @param filename  file to be checked
     * @return          error message
     */
    public String checkExtension(String filename){
        String extension = filename.substring(filename.lastIndexOf(".")+1, filename.length());
        String msg = "";
        if(!extension.equals("zip")){
            msg = "Invalid File Extension (zip Only)";
        }
        return msg;
    }
    
    
}
