
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SLOCA</title>
        <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Open+Sans:600'>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/style1.css">
        <link href='http://fonts.googleapis.com/css?family=Great+Vibes' rel='stylesheet' type='text/css'>
    </head>
    <body class="login">
        <div class="login-wrap">
            <div class="login-html">
                <form name="login_form" method="post" action="LoginServlet">
                    <input id="tab-1" type="radio" name="tab" class="sign-in" checked><label for="tab-1" class="tab"> <h11>Log In</h11></label>
                    <input id="tab-2" type="radio" name="tab" class="sign-up"><label for="tab-2" class="tab"></label>
                    <div class="login-form">
                        <div class="sign-in-htm">
                            <div class="group">
                                <label for="user" class="label">USERNAME</label>
                                <input name="user" type="text" class="input">
                            </div>
                            <div class="group">
                                <label for="pass" class="label">PASSWORD</label>
                                <input name="pass" type="password" class="input" data-type="password">
                            </div>
                            <div class="group">
                                <input type="submit" class="button" value="SIGN IN">

                            </div>
                            <%
                                String errorMsg = (String) request.getAttribute("errorMsg");
                                if (errorMsg != null) {
                                    out.print("<font color=\"black\" size = 1>" + errorMsg + "</font>");
                                }
                            %>
                            <div class="hr"></div>

                        </div>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>