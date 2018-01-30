<%-- 
    Document   : updated
    Created on : 25 Oct, 2017, 12:31:24 PM
    Author     : ellpeeaxe
--%>

<%@page import="java.util.TreeMap"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <link rel="stylesheet" href="css/style1.css" type="text/css" media="all" /> <!-- Style-CSS --> 
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
         <%@include file="navBar.jsp" %>
         <%
            if(session.getAttribute("demRowCount")!=null){
                out.println("Demographics:<br>");
                out.println("Number of records added: " + (Integer)session.getAttribute("demRowCount") + "<br>");
                out.println("Errors:<br>");        

                TreeMap<Integer, ArrayList<String>> demErrors = (TreeMap<Integer, ArrayList<String>>)session.getAttribute("demErrors"); 
                if(demErrors.size()!=0){
                    for (int row: demErrors.keySet()){
                        int key =row;
                        ArrayList<String> value = demErrors.get(row);  
                        out.println("Line " + key + " : " + value + "<br>");  
                    } 
                }else{
                    out.println("No errors!");
                }
            }
         %> 
    </p>
        <%
            if(session.getAttribute("locRowCount")!=null){
                out.println("Location:<br>");
                out.println("Number of records added: " + (Integer)session.getAttribute("locRowCount") + "<br>");
                out.println("Errors:<br>");        

                TreeMap<Integer, ArrayList<String>> locErrors = (TreeMap<Integer, ArrayList<String>>)session.getAttribute("locErrors"); 
                if(locErrors.size()!=0){
                    for (int row: locErrors.keySet()){
                        int key =row;
                        ArrayList<String> value = locErrors.get(row);  
                        out.println("Line " + key + " : " + value + "<br>");  
                    } 
                }else{
                    out.println("No errors!");
                }
            }
         %> 
    </p>
    </body>
</html>
