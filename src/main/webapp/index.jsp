<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<center >
	<span style="color: red;">
	   <%
	   if(request.getAttribute("message")!=null){%>
	   <%=request.getAttribute("message") %>
	   <%
	   }
	   %>
	</span>
	
	  
		<%
		Object islogin=session.getAttribute("islogin");
			if (islogin==null?false:islogin.toString() == "1") {
		%>

		<a href="showmessage.jsp">我要查看信息</a>---<a href="savemessage.jsp">我要添加信息</a>---<a href="removemessage.jsp">我要删除信息</a>---<a
			href="<%=request.getContextPath()%>/exit">我要退出</a>
		<%
			}
		%>
	</center>
	<center>
		<h1>Hello world,please login</h1>
	</center>
	<center>
		<div>
			<form action="<%=request.getContextPath()%>/login" method="post">
				<div align="center">
					User Name:<input name="username" type="text" /><br>
					<br>
					<br> Password:<input name="password" type="password" /><br>
					<br> <input type="submit" name="submitmessage">
					<div>
						<a></a>
					</div>
				</div>
			</form>
		</div>
	</center>
</body>
</html>
