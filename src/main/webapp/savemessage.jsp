<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<center>
	<span style="color: red;">
	   <%
	   if(request.getAttribute("message")=="已添加"){%>
	  <script type="text/javascript">
        alert("已添加");
	  </script>
	   <%
	   }
	   %>
	</span>
	<%
		Object islogin=session.getAttribute("islogin");
			if (islogin==null?false:islogin.toString() == "1") {
		%>
<a href="<%=request.getContextPath()%>/exit">我要退出</a>---<a href="showmessage.jsp">我要查看信息</a>---<a href="removemessage.jsp">我要删除信息</a>
<h1>Hello world,add message in below</h1></center>
<center>
<form action="<%=request.getContextPath()%>/save" method="post">
<div align="center">
attr:<input name="attr" type="text"/><br><br><br>
value:<input name="value" type="text" /><br><br>
<input type="submit" name="submitmessage">
</div>
</form>

	<%
}else{
	%>
	<h1>对不起，你还没登录，<a href="index.jsp">我要登录</a></h1>
	<%
}%>
</center>

</body>
</html>