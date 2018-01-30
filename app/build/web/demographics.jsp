<%-- 
    Document   : demographics
    Created on : 11 Oct, 2017, 12:28:39 PM
    Author     : pigs_
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
    <body class = "demographics">

        <%@include file="navBar.jsp" %> 
        <div class="main-agile"> 
            
            <div id="home" class="w3ls-banner"> 
                <!-- banner-text -->
                <div class="w3layouts-banner-top">
                    <div class="container">
                        <div class="agileits-banner-info">
                            <h9>Demographic</h9>
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
                                            <form method="get" action="demographicServlet">
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
                                                <div class="space">
                                                    <div class="block" >Query 1:</div>
                                                    <div class="block">Query 2:</div>
                                                    <div class="block">Query 3:</div>
                                                </div>


                                                <div class="query_fill group" >           
                                                    <span class="custom-dropdown custom-dropdown--white">
                                                        <select class="custom-dropdown__select custom-dropdown__select--white" name="query1">                                                                                                               
                                                            <option value="">Choose here</option>
                                                            <option value="Year">Year</option>
                                                            <option value="Gender">Gender</option>
                                                            <option value="school">School</option>
                                                        </select>
                                                    </span>
                                                    <span class="custom-dropdown custom-dropdown--white">
                                                        <select class="custom-dropdown__select custom-dropdown__select--white" name="query2">                                                                                                               
                                                            <option value="">Choose here</option>
                                                            <option value="Year">Year</option>
                                                            <option value="Gender">Gender</option>
                                                            <option value="school">School</option>
                                                        </select>
                                                    </span>
                                                    <span class="custom-dropdown custom-dropdown--white">
                                                        <select class="custom-dropdown__select custom-dropdown__select--white" name="query3">                                                                                                               
                                                            <option value="">Choose here</option>
                                                            <option value="Year">Year</option>
                                                            <option value="Gender">Gender</option>
                                                            <option value="school">School</option>
                                                        </select>
                                                    </span>
                                                </div>
                                                <div class="reservation w3l-submit">
                                                    <ul>	
                                                        <div class="date_btn">
                                                            <input type="submit" value="Check Demographics"/>
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
        <%      String errorMsg = (String) request.getAttribute("errorMsg");
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

        <%
            //Retrieve results
            LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Integer>>> demoMap = (LinkedHashMap < String, LinkedHashMap<String, LinkedHashMap<String, Integer
            >>>) request.getAttribute("demoMap");
            //if results exist, display results
            if (demoMap != null) {
        %>
        <div style="margin:0 auto; text-align:center">  
            <h10>Results</h10>
        </div>
        <div style=" text-align:center; bottom-margin:3em">
            <%
                out.println("Breakdown by " + (String) request.getAttribute("query") + " at SIS on " + (String) request.getAttribute("dateTime") + "<br>");
            %>
        </div>
        <%
            String[] queries = ((String) request.getAttribute("query")).split(",");
            if (demoMap.isEmpty()) {
        %>
        <div style="margin:0 auto; text-align:center; font-weight:bold">
            <%
                out.println("No Results!");
            %>
        </div>
        <%
                } else {
                    int numQuery = (Integer) request.getAttribute("numQueries");
                    int totalNo = (Integer) request.getAttribute("total");

                    if (numQuery == 1) {
                        out.println("<table border=1><tr><th>" + queries[0] + "</th><th>Count</th><th>Percentage</th></tr>");
                        for(Map.Entry<String, LinkedHashMap<String, LinkedHashMap<String, Integer>>> e : demoMap.entrySet()) {
                            //String equals "query,count"
                            String key1 = e.getKey();
                            String[] splitKey = key1.split(",");
                            String field1 = splitKey[0];
                            String count1 = splitKey[1];
                            double percentage = Double.parseDouble(count1) / (double) totalNo * 100;
                            //check whether to round up or down
                            int percTest = (int) percentage;
                            if (percentage - percTest >= 0.5) {
                                percTest += 1;
                            }
                            out.println("<tr><td>" + field1 + "</td><td>" + count1 + "</td><td>" + percTest + "</td></tr>");
                        }

                    }
                    if (numQuery == 2) {
                        out.println("<table border=1><tr><th>" + queries[0] + "</th><th>Count</th><th>Percentage</th><th>" + queries[1] + "</th><th>Count</th><th>Percentage</th></tr>");
                        for(Map.Entry<String, LinkedHashMap<String, LinkedHashMap<String, Integer>>> e : demoMap.entrySet()) {
                            //String equals "query,count"
                            String key1 = e.getKey();
                            String[] splitKey = key1.split(",");
                            String field1 = splitKey[0];
                            String count1 = splitKey[1];

                            LinkedHashMap<String, LinkedHashMap<String, Integer>> secondQuery = demoMap.get(key1);
                            int rowSpan = secondQuery.size();
                            double percentage = Double.parseDouble(count1) / (double) totalNo * 100;
                            //check whether to round up or down
                            int percTest = (int) percentage;
                            if (percentage - percTest >= 0.5) {
                                percTest += 1;
                            }
                            out.println("<tr><td rowspan=\"" + rowSpan + "\">" + field1 + "</td><td rowspan=\"" + rowSpan + "\">" + count1 + "</td>" + "<td rowspan=\"" + rowSpan + "\">" + percTest + "</td>");
                            boolean first = true;
                            for (Map.Entry<String, LinkedHashMap<String, Integer>> e2 : secondQuery.entrySet()) {
                                //String equals "query,count"
                                String key2 = e2.getKey();
                                String[] splitKey2 = key2.split(",");
                                String field2 = splitKey2[0];
                                String count2 = splitKey2[1];
                                percentage = Double.parseDouble(count2) / (double) totalNo * 100;
                                //check whether to round up or down
                                percTest = (int) percentage;
                                if (percentage - percTest >= 0.5) {
                                    percTest += 1;
                                }
                                if (first) {
                                    out.println("<td>" + field2 + "</td><td>" + count2 + "</td><td>" + percTest + "</td></tr>");
                                    first = false;
                                } else {
                                    out.println("<tr><td>" + field2 + "</td><td>" + count2 + "</td><td>" + percTest + "</td></tr>");
                                }
                            }
                        }
                    }
                    if (numQuery == 3) {
                        out.println("<table border=1><tr><th>" + queries[0] + "</th><th>Count</th><th>Percentage</th><th>" + queries[1] + "</th><th>Count</th><th>Percentage</th><th>" + queries[2] + "</th><th>Count</th><th>Percentage</th></tr>");
                        for(Map.Entry<String, LinkedHashMap<String, LinkedHashMap<String, Integer>>> e : demoMap.entrySet()) {
                            String key1 = e.getKey();
                            //String equals "query,count"
                            String[] splitKey = key1.split(",");
                            String field1 = splitKey[0];
                            String count1 = splitKey[1];

                            LinkedHashMap<String, LinkedHashMap<String, Integer>> secondQuery = demoMap.get(key1);
                            double percentage1 = Integer.parseInt(count1) / (double) totalNo * 100;
                            //check whether to round up or down
                            int percTest1 = (int) percentage1;
                            if (percentage1 - percTest1 >= 0.5) {
                                percTest1 += 1;
                            }
                            boolean oneFirst = true;

                            for(Map.Entry<String, LinkedHashMap<String, Integer>> e2 : secondQuery.entrySet()) {
                                //String equals "query,count"
                                String key2 = e2.getKey();
                                String[] splitKey2 = key2.split(",");
                                String field2 = splitKey2[0];
                                String count2 = splitKey2[1];
                                double percentage2 = Integer.parseInt(count2) / (double) totalNo * 100;
                                //check whether to round up or down
                                int percTest2 = (int) percentage2;
                                if (percentage2 - percTest2 >= 0.5) {
                                    percTest2 += 1;
                                }
                                LinkedHashMap<String, Integer> thirdQuery = secondQuery.get(key2);
                                int rowSpan2 = thirdQuery.size();
                                int rowSpan = 0;
                                for(Map.Entry<String, LinkedHashMap<String, Integer>> ee : secondQuery.entrySet()) {
                                    String keykey = ee.getKey();
                                    LinkedHashMap<String, Integer> third = secondQuery.get(keykey);
                                    rowSpan += third.size();
                                }

                                boolean twoFirst = true;

                                for (Map.Entry<String, Integer> e3 : thirdQuery.entrySet()) {
                                    String field3 = e3.getKey();
                                    int count3 = thirdQuery.get(field3);

                                    double percentage3 = (count3) / (double) totalNo * 100;
                                    //check whether to round up or down
                                    int percTest3 = (int) percentage3;
                                    if (percentage3 - percTest3 >= 0.5) {
                                        percTest3 += 1;
                                    }
                                    if (oneFirst && twoFirst) {
                                        out.println("<tr><td rowspan=\"" + rowSpan + "\">" + field1 + "</td><td rowspan=\"" + rowSpan + "\">" + count1 + "</td><td rowspan=\"" + rowSpan + "\">" + percTest1
                                                + "</td><td rowspan=\"" + rowSpan2 + "\">" + field2 + "</td><td rowspan=\"" + rowSpan2 + "\">" + count2 + "</td><td rowspan=\"" + rowSpan2 + "\">" + percTest2
                                                + "</td><td>" + field3 + "</td><td>" + count3 + "</td><td>" + percTest3 + "</td></tr>");
                                        oneFirst = false;
                                        twoFirst = false;
                                    } else if (!oneFirst && twoFirst) {
                                        out.println("<tr><td rowspan=\"" + rowSpan2 + "\">" + field2 + "</td><td rowspan=\"" + rowSpan2 + "\">" + count2
                                                + "</td><td rowspan=\"" + rowSpan2 + "\">" + percTest2 + "</td><td>" + field3 + "</td><td>" + count3 + "</td><td>" + percTest3 + "</td></tr>");
                                        twoFirst = false;
                                    } else if (!oneFirst && !twoFirst) {
                                        out.println("<tr><td>" + field3 + "</td><td>" + count3 + "</td><td>" + percTest3 + "</td></tr>");
                                    }
                                }
                            }
                        }
                    }

                    out.println("</table>");
                    out.println("<br><br>");
                }
            }
        %>    

    </body>
</html>