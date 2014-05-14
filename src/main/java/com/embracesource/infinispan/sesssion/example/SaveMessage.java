package com.embracesource.infinispan.sesssion.example;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SaveMessage extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession();
		Enumeration attrs=req.getParameterNames();
		while(attrs.hasMoreElements()){
			session.setAttribute(String.valueOf(attrs.nextElement()), req.getParameter(String.valueOf(attrs.nextElement())));
		}
		req.setAttribute("message", "已添加");
		req.getRequestDispatcher("savemessage.jsp").forward(req, resp);
	}

}
