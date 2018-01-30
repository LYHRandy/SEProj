<%-- 
    Document   : topK
    Created on : 13 Oct, 2017, 11:59:18 AM
    Author     : Swetha
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

        <!-- //css files -->
        <!-- online-fonts -->
        <link href='http://fonts.googleapis.com/css?family=Great+Vibes' rel='stylesheet' type='text/css'>
        <link href="//fonts.googleapis.com/css?family=Coda:400,800&amp;subset=latin-ext" rel="stylesheet">
        <link href="//fonts.googleapis.com/css?family=Ubuntu:300,300i,400,400i,500,500i,700,700i&amp;subset=cyrillic,cyrillic-ext,greek,greek-ext,latin-ext" rel="stylesheet">
        <!-- //online-fonts -->
    </head>


    <body class = "topkpp">

        <%@include file="navBar.jsp" %>

        <div class="w3-content w3-padding" style="max-width:1564px">
            
            <div id="home" class="w3ls-banner"> 
                <!-- banner-text -->
                <div class="w3layouts-banner-top">
                    <div class="container">
                        <div class="agileits-banner-info">
                            <h9>Top K Popular Places</h9>
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
                                            <form method="get" action="TopKPPServlet">
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

                                                <div class="kValue">K Value:</div>                              
                                                <span class="custom-dropdown custom-dropdown--white">
                                                    <select class="custom-dropdown__select custom-dropdown__select--white" name="kValue">                                                                                                               
                                                        <option value="3">-Select-</option>
                                                        <%
                                                            for (int i = 1; i <= 10; i++) {
                                                                out.println("<option value='" + i + "'>" + i + "</option>");
                                                            }
                                                        %>
                                                    </select>
                                                </span>

                                                <div class="reservation w3l-submit">
                                                    <ul>	
                                                        <div class="date_btn">
                                                            <input type="submit" value="Search"/>
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
        <   <%
            //Retrieve results if results exist
            TreeMap<Integer, ArrayList<String>> topKPP = (TreeMap<Integer, ArrayList<String>>) request.getAttribute("topKPP");
            if (topKPP != null) {
        %>
        <div style="margin:0 auto; text-align:center">  
            <h10>Results</h10>
        </div>
        <div style=" text-align:center; bottom-margin:3em">
            <%
                int k = (Integer) request.getAttribute("kValue");
                out.println("</p>Top " + (Integer) request.getAttribute("kValue") + " Popular Places on " + (String) request.getAttribute("dateTime") + "<br>");
            %>
        </div>
        <%
            if (topKPP.isEmpty()) {
        %>
        <div style="margin:0 auto; text-align:center; font-weight:bold">
            <%
                out.println("No Results!");
            %>
        </div>
        <%
                } else {
                    out.println("<table border =1><tr><th>Rank</th><th>Semantic Place</th><th>No. People</th></tr>");
                    int rank = 1;
                    int row = 1;
                    while (row <= topKPP.size() && (rank <= k && rank <= topKPP.size())) {
                        //print rank
                        out.println("<tr><td>" + rank + "</td>");

                        //get current row
                        ArrayList<String> currRow = topKPP.get(row);

                        //print current semantic place
                        out.println("<td>" + currRow.get(0));

                        //get next row
                        int nextRowNum = row + 1;
                        ArrayList<String> nextRow = topKPP.get(nextRowNum);

                        //while next row is not null and same number of ppl
                        while (nextRow != null && nextRow.get(1).equals(currRow.get(1))) {
                            //print next location
                            out.println(", " + nextRow.get(0));
                            //update next row to check
                            nextRow = topKPP.get(++nextRowNum);
                        }
                        row = nextRowNum;

                        //print number of ppl
                        out.println("</td><td>" + currRow.get(1) + "</td></tr>");
                        rank++;
                    }
                    out.println("</table>");
                    out.println("<br><br>");
                }
            }
        %>
    </div>
</body>
</html>