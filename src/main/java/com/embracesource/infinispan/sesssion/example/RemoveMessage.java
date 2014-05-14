package com.embracesource.infinispan.sesssion.example;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RemoveMessage extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession();
	    session.removeAttribute("deleteAttr");
		req.setAttribute("message", "已删除");
		req.getRequestDispatcher("removemessage.jsp").forward(req, resp);
	}

}
