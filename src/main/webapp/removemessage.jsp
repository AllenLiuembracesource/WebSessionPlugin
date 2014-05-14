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
<a href="showmessage.jsp">我要查看信息</a>
<h1>Hello world,remove message in below</h1>
<form action="">
attrName:<select name="deleteAttr">
<% Enumeration test=session.getAttributeNames();
while(test.hasMoreElements()){
	String attr=(String)test.nextElement();
	if(attr=="username"||attr=="password"||attr=="islogin"){
		continue;
	}
	%>
	<option><%=attr%></option>
	<%
    }
%>
</select>
<input name="delete" type="submit">
</form>
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