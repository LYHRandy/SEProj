<%-- 
    Document   : agd
    Created on : 14 Oct, 2017, 3:32:04 PM
    Author     : Carine
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!DOCTYPE html>
<html>
    <head>
        <!-- Meta tag Keywords -->
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="keywords" content="Tourism web template, Bootstrap Web Templates, Flat Web Templates, Android Compatible web template, Smartphone Compatible web template, free webdesigns for Nokia, Samsung, LG, SonyEricsson, Motorola web design" />
        <script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false);
            function hideURLbar(){ window.scrollTo(0,1); } </script>
        <!--// Meta tag Keywords -->
        <!-- css files -->
        <link rel="stylesheet" href="css/bootstrap1.css"> <!-- Bootstrap-Core-CSS -->
        <link rel="stylesheet" href="css/style1.css" type="text/css" media="all" /> <!-- Style-CSS --> 
        <link rel="stylesheet" href="css/font-awesome.css"> <!-- Font-Awesome-Icons-CSS -->
        <link rel="stylesheet" href="css/tables.css"> <!-- Font-Awesome-Icons-CSS -->
        <!-- //css files -->
        <!-- online-fonts -->
        <link href='http://fonts.googleapis.com/css?family=Great+Vibes' rel='stylesheet' type='text/css'>
        <link href="//fonts.googleapis.com/css?family=Coda:400,800&amp;subset=latin-ext" rel="stylesheet">
        <link href="//fonts.googleapis.com/css?family=Ubuntu:300,300i,400,400i,500,500i,700,700i&amp;subset=cyrillic,cyrillic-ext,greek,greek-ext,latin-ext" rel="stylesheet">
        <!-- //online-fonts -->
    </head>
    <body class = "agd">

        <%@include file="navBar.jsp" %> 

        

        <div class="main-agile">        
            <div id="home" class="w3ls-banner"> 
                <!-- banner-text -->
                <div class="w3layouts-banner-top">
                    <div class="container">
                        <div class="agileits-banner-info">
                            <h9>Automatic Group Detection</h9>
                        </div>	
                        <div class="sap_tabs">	
                            <div id="horizontalTab" style="display: block; width: 100%; margin: 0px;">
                                <ul class="resp-tabs-list">
                                    <li class="resp-tab-item grid1"><span></i></span></li>

                                </ul>				  	 
                                <div class="resp-tabs-container">
                                    <div class="tab-1 resp-tab-content">
                                        <div class="facts">
                                            <form method="post" action="agdServlet">
                                                <div class="reservation">

                                                    <div class="two">Date:</div>
                                                    <div class="one">Time:</div>

                                                    <div class="book_date">
                                                        <!-- date -->
                                                        <%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
                                                        <%  Date date = new Date();%>
                                                        <input type="date" name="date" size="25"/>
                                                        <!-- time -->
                                                        <span class="glyphicon" aria-hidden="true"></span>                                                     
                                                        <input type="time" name="time" size="25" step="1"/>
                                                    </div>					
                                                </div>
                                                <div class="reservation w3l-submit">
                                                    <ul>	
                                                        <div class="date_btn">
                                                            <input type="submit" value="Submit" />
                                                        </div>
                                                    </ul>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>			
        </div>
        <%            
            String errorMsg = (String) request.getAttribute("errorMsg");
            if (errorMsg != null) {
                out.print("<font color=\"red\" size = 4><center>"+errorMsg+"</center></font>");
            }
        %>
        <!— js-scripts —>      
        <!— js —>
        <script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
        <script type="text/javascript" src="js/bootstrap.js"></script> <!— Necessary-JavaScript-File-For-Bootstrap —> 
        <!— //js —>
        <!— whole frame —>
        <script src="js/easyResponsiveTabs.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(document).ready(function () {
                $('#horizontalTab').easyResponsiveTabs({
                    type: 'default', //Types: default, vertical, accordion           
                    width: 'auto', //auto or any width like 600px
                    fit: true   // 100% fit in a container
                });
            });
        </script>
        <!— //Easy Responsivetabs —>
        <!— strat-date-piker —>
        <link rel="stylesheet" href="css/jquery-ui.css" />
        <script src="js/jquery-ui.js"></script>
        <script>
            function myFunction() {
                < input type = "datetime-local" id = "myLocalDate" value = "2014-01-02T11:42:00.510" >
        }
        </script>
        <!— //End-date-piker —>
        <!— Numscroller —>
        <script type="text/javascript" src="js/numscroller-1.0.js"></script>
        <!— //Numscroller —>
        <!— //js-scripts —>


        <%
            Map<ArrayList<String>, TreeMap<String, Integer>> agd = (Map <ArrayList<String>, TreeMap<String,Integer>>)request.getAttribute("agd");
            if (agd != null) {
        %>
        <div style="margin:0 auto; text-align:center">  
            <h10>Results</h10>
        </div>
        <div style=" text-align:center; bottom-margin:3em">
            <%
                    out.println("Groups detected in SIS on " + (String) request.getAttribute("dateTime"));
                    out.println("<br>Number of users in SIS: " + (Integer) request.getAttribute("numPpl"));
                    out.println("<br><br>Total groups: " + agd.size());

                    for (Map.Entry<ArrayList<String>, TreeMap<String, Integer>> e: agd.entrySet()) {

                        ArrayList<String> key = e.getKey();
                        out.println("<br><br>Group size: " + key.size());
                        TreeMap<String, Integer> locCount = agd.get(key);
                        out.println("<br>Members:");
                        out.println("<table border=1><tr><th>Email</th><th>MacAddress</th></tr>");
                        for (String str : key) {
                            String[] eMac = str.split(",");
                            out.println("<tr><td>" + eMac[0] + "</td><td>" + eMac[1] + "</td></tr>");
                        }
                        out.println("</table>");
                        out.println("Locations:");
                        out.println("<table border=1><tr><th>Location ID</th><th>Time Spent</th></tr>");
                        int totalTime = 0;
                        for (Map.Entry<String, Integer> e1 : locCount.entrySet()) {
                            String locID = e1.getKey();
                            int count = locCount.get(locID);
                            out.println("<tr><td>" + locID + "</td><td>" + count + "</td></tr>");
                            totalTime += count;
                        }
                        out.println("</table>");
                        out.println("Total time spent together :" + totalTime);
                        out.println("<br><br>");
                    }
                }
            %>
    </body>
</html>
