<%=request.getRemoteAddr()%>

<%
    if (request.getServerPort() == 443)
        response.sendRedirect("/oa/main.lp");
    else
        response.sendRedirect("/oa/index.lp");
%>