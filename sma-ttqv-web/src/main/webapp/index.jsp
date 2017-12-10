<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<html>
  <head>
    <link type="text/css" rel="stylesheet" href="stylesheets/main.css" />
    <title>Séb M Utilities</title>
  </head>

<body>
<h2>sma-ttqv-web</h2>

<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
%>
<p>Hello, <%= user.getNickname() %>! (You can
<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
    } else {
%>
<p>Hello!
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
to include your name with greetings you post.</p>
<%
    }
%>
<!-- <p>
<a href="guestbook">GuestbookServlet</a>
</p>
<p>
<a href="addtrace.jsp">Add Trace...</a>
</p>-->
<p>
<a href="screen/convertOziMapToTtqv.jsp">Convert OZI Cal file To TTQV...</a>
</p>
<p>
<a href="screen/uploadQu4.jsp">Upload QU4...</a>
</p>
</body>
</html>
