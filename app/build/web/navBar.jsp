<%-- 
    Document   : navBar
    Created on : 22 Sep, 2017, 5:20:28 PM
    Author     : ellpeeaxe
--%>
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">

<div class="w3-top" style="position:relative">
    <div class="w3-bar w3-white w3-wide w3-padding w3-card-2">
        <a href="home.jsp" class="w3-bar-item w3-button"><b>SMU</b> SLOCA</a>
        <!? Float links to the right. Hide them on small screens ?>
        <div class="w3-left w3-hide-small">
            <a href="demographics.jsp" class="w3-bar-item w3-button">Demographics</a>
            <a href="heatmap.jsp" class="w3-bar-item w3-button">Heatmap</a>
            <div class="w3-dropdown-hover">
                <button class="w3-button">Top-k</button>
                <div class="w3-dropdown-content w3-bar-block w3-card-4">
                    <a href="topKPP.jsp" class="w3-bar-item w3-button">Top-k Popular Places</a>
                    <a href="TopKC.jsp" class="w3-bar-item w3-button">Top-k Companions</a>
                    <a href="topKNP.jsp" class="w3-bar-item w3-button">Top-k Next Places</a>
                </div>
            </div>
            <a href="agd.jsp" class="w3-bar-item w3-button">Groups</a>

        </div>
        <div class="w3-right w3-hide-small">
            <%
            
            if(session.getAttribute("status")==null){
                response.sendRedirect("login.jsp");
                return;
            }

            int status = (Integer)session.getAttribute("status");
            
            if(status == 2){
                out.println("<div class=\"w3-dropdown-hover\">" +
                            "<button class=\"w3-button w3-white\">Admin</button>" +
                            "<div class=\"w3-dropdown-content w3-bar-block w3-border\">\n" +
                            "<a href=\"bootstrap.jsp\" class=\"w3-bar-item w3-button\">Bootstrap</a>" +
                            "<a href=\"update.jsp\" class=\"w3-bar-item w3-button\">Update</a>" +
                            "</div></div>");
            } 
            %>
            <a href="logout.jsp" class="w3-bar-item w3-button">Logout</a>
        </div>
    </div>
</div>
