<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<center>	<%
		Object islogin=session.getAttribute("islogin");
			if (islogin==null?false:islogin.toString() == "1") {
		%>
<a href="<%=request.getContextPath()%>/exit">我要退出</a>
<a href="savemessage.jsp">我要添加信息</a>
<a href="removemessage.jsp">我要删除信息</a>
<a href="removemessage.jsp">刷新</a>
<h1>Hello world,show message in below</h1>

<% Enumeration test=session.getAttributeNames();
while(test.hasMoreElements()){
	String attr=(String)test.nextElement();
	%>
 <div><%=attr%>:<%=session.getAttribute(attr)%></div>
	<%
    }
%>
	<%
}else{
	%>
	<h1>对不起，你还没登录，<a href="index.jsp">我要登录</a></h1>
	<%
}
%>
</center>
</body>
</html>