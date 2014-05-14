package com.embracesource.infinispan.sesssion.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.embracesource.infinispan.sesssion.InfinispanHttpServletRequest;
import com.embracesource.infinispan.sesssion.config.InfinispanSessionConfiguration;
import com.embracesource.infinispan.sesssion.helper.InfinispanSessionHelper;

public class WebShareSessionJboss51Filter implements Filter {
	private InfinispanSessionHelper infinispanSessionHelper;

	public void init(FilterConfig filterConfig) throws ServletException {
//		if (infinispanSessionHelper == null) {
//			infinispanSessionHelper = new InfinispanSessionHelper(
//					InfinispanSessionConfiguration.instance(filterConfig));
//			infinispanSessionHelper.init();
//		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		InfinispanHttpServletRequest infinispanHttpServletRequest = null;
		if (!infinispanSessionHelper.isAvailableCluster()) {
			chain.doFilter(httpServletRequest, response);
		} else if (!validateNewSession(httpServletRequest)) {// 校验是否为新的会话
			infinispanHttpServletRequest = new InfinispanHttpServletRequest(
					httpServletRequest, infinispanSessionHelper);
			try {
				infinispanSessionHelper.enter(infinispanHttpServletRequest);
				chain.doFilter(infinispanHttpServletRequest, response);

			} finally {
				infinispanSessionHelper.exit(infinispanHttpServletRequest);
			}

		} else {
			infinispanHttpServletRequest = new InfinispanHttpServletRequest(
					httpServletRequest, infinispanSessionHelper);
			chain.doFilter(infinispanHttpServletRequest, response);
		}
	}

	public void destroy() {
		// TODO Auto-generated method stub
		if (infinispanSessionHelper != null) {
			infinispanSessionHelper.destroy();
			infinispanSessionHelper = null;
		}
	}

	private boolean validateNewSession(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getRequestedSessionId() == null ? false
				: true;
	}

}
