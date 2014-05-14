package com.embracesource.infinispan.sesssion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.embracesource.infinispan.sesssion.helper.InfinispanSessionHelper;

public class InfinispanHttpServletRequest extends HttpServletRequestWrapper {
	private InfinispanSessionHelper infinispanSessionHelper;

	public InfinispanHttpServletRequest(HttpServletRequest request,
			InfinispanSessionHelper infinispanSessionHelper) {
		super(request);
		// TODO Auto-generated constructor stub
		this.infinispanSessionHelper = infinispanSessionHelper;
	}

	@Override
	public HttpSession getSession(boolean create) {
		// TODO Auto-generated method stub
		return new InfinispanWebSession(super.getSession(create),
				infinispanSessionHelper.getIsc(),
				infinispanSessionHelper.getIsro());
	}
}
