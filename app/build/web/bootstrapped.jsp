<%-- 
    Document   : uploaded
    Created on : 29 Sep, 2017, 2:30:26 PM
    Author     : Swetha
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
        <title>JSP Page</title>
    </head>
    <body>
         <%@include file="navBar.jsp" %>
         Demographics:<br>
         Number of records added: <%= (Integer)session.getAttribute("demRowCount")%><br>
         Errors:<br>        
         <%
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
         %> 
    </p>
         Location-Lookup:<br>
         Number of records added: <%= (Integer)session.getAttribute("locLookupRowCount")%><br>
         Errors:<br>        
         <%
            TreeMap<Integer, ArrayList<String>> locLookupErrors = (TreeMap<Integer, ArrayList<String>>)session.getAttribute("locLookupErrors"); 
            if(locLookupErrors.size()!=0){
                for (int row: locLookupErrors.keySet()){
                    int key =row;
                    ArrayList<String> value = locLookupErrors.get(row);  
                    out.println("Line " + key + " : " + value + "<br>");  
                } 
            }else{
            
                out.println("No errors!");
            }
         %> 
    </p>
    Location:<br>
         Number of records added: <%= (Integer)session.getAttribute("locRowCount")%><br>
         Errors:<br>        
         <%
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
         %> 
    </p>
    </body>
</html>
