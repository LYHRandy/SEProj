<%-- 
    Document   : heatmap
    Created on : 14 Oct, 2017, 7:12:49 PM
    Author     : User
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
        <link rel="stylesheet" href="css/tables.css">
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
         <link rel="stylesheet" href="css/tables.css">

        <!-- //css files -->
        <!-- online-fonts -->

        <link href='http://fonts.googleapis.com/css?family=Great+Vibes' rel='stylesheet' type='text/css'>
        <link href="//fonts.googleapis.com/css?family=Coda:400,800&amp;subset=latin-ext" rel="stylesheet">
        <link href="//fonts.googleapis.com/css?family=Ubuntu:300,300i,400,400i,500,500i,700,700i&amp;subset=cyrillic,cyrillic-ext,greek,greek-ext,latin-ext" rel="stylesheet">
        <!-- //online-fonts -->
    </head>

    <body class = "heatmap">
        <%@include file="navBar.jsp" %>
        <div class="w3-content w3-padding" style="max-width:1564px">
            
            
             <div id="home" class="w3ls-banner"> 
                <!-- banner-text -->
                <div class="w3layouts-banner-top">
                    <div class="container">
                        <div class="agileits-banner-info">
                            <h9>HeatMap</h9>
                        </div>	
                        <div class="sap_tabs">	
                            <div id="horizontalTab" style="display: block; width: 100%; margin: 0px;">
                                <ul class="resp-tabs-list">
                                    <li class="resp-tab-item grid1"><span></i></span></li>

                                </ul>				  	 
                                <div class="resp-tabs-container">
                                    <div class="tab-1 resp-tab-content">
                                        <div class="facts">
                                            <!-- #################### START OF FORM #################### -->
                                            <form method="get" action="HeatmapServlet">
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
                                                    <div class="floor" >Floor:</div>                              
                                                    <span class="custom-dropdown custom-dropdown--white">
                                                        <select class="custom-dropdown__select custom-dropdown__select--white" name="floor">                                                                                                               
                                                            <option value="0">B1</option>
                                                             <option value="1">L1</option>
                                                             <option value="2">L2</option>
                                                             <option value="3">L3</option>
                                                             <option value="4">L4</option>
                                                             <option value="5">L5</option>
                                                         </select>
                                                    </span>

                                                <div class="reservation w3l-submit">
                                                    <ul>	
                                                        <div class="date_btn">
                                                            <input type="submit" value="View Heatmap"/>
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
            String errorMsg = (String)request.getAttribute("errorMsg");
                 if(errorMsg != null){
                    out.print("<font color=\"red\" size = 4><center>"+errorMsg+"</center></font>");
                }
        %>
        <!-- js-scripts -->			
        <!-- js -->
        <script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
        <script type="text/javascript" src="js/bootstrap.js"></script> <!-- Necessary-JavaScript-File-For-Bootstrap --> 
        <!-- //js -->
        <!-- whole frame -->
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
        <!-- //Easy Responsivetabs -->
        <!-- strat-date-piker -->
        <link rel="stylesheet" href="css/jquery-ui.css" />
        <script src="js/jquery-ui.js"></script>
        <script>
            function myFunction() {
                < input type = "datetime-local" id = "myLocalDate" value = "2014-01-02T11:42:00.510" >
            }
        </script>
        <!-- //End-date-piker -->
        <!-- Numscroller -->
        <script type="text/javascript" src="js/numscroller-1.0.js"></script>
        <!-- //Numscroller -->
        <!-- //js-scripts -->

         
        <%
            //retrieve results if results exist
            HashMap <Integer,String[]> heatMap = (HashMap<Integer, String[]>)request.getAttribute("heatMap");
            if(heatMap!=null){
                %>    
                <div style="margin:0 auto; text-align:center">  
                    <h10>Results</h10>
                </div>
               
                <div style=" text-align:center; bottom-margin:3em">
                    <%
                    out.println("Crowd density at "+(String)request.getAttribute("floor")+" at "+(String)request.getAttribute("date")+"<br>");
                    %>
                </div>
                
                 <%       
                if(heatMap.isEmpty()){
                    out.println("No Results!");
                }else{
                    out.println("<table border =1><tr><th>Semantic Place</th><th>No. of People</th><th>Crowd Density</th></tr>");
                    Iterator it = heatMap.entrySet().iterator();
                    for(int record : heatMap.keySet()){
                       String [] semCount = heatMap.get(record);
                       String semanticPlace = semCount[0];
                       int count = Integer.parseInt(semCount[1]);
                       String density = semCount[2];
                       out.println("<tr><td>"+ semanticPlace +"</td><td>"+count+"</td><td>"+density+"</td></tr>");
                    }
                    out.println("</table><br><br>"); 
                    out.println("<br><br>");
                    

                }
            }
        %>
    </body>
    
    
    
    
</html>
