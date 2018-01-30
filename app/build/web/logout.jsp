<%-- 
    Document   : logout
    Created on : 22 Sep, 2017, 5:19:20 PM
    Author     : ellpeeaxe
--%>

<%
    session.invalidate();
    response.sendRedirect("login.jsp");
%>

