package com.embracesource.infinispan.sesssion.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class LoginServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession();
		session.setAttribute("username", req.getParameter("username"));
		session.setAttribute("password", req.getParameter("password"));
		session.setAttribute("islogin", "1");
		req.setAttribute("message", "已登录");
		req.getRequestDispatcher("index.jsp").forward(req, resp);
	}


}
