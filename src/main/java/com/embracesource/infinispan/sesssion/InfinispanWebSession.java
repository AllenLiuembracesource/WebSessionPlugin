package com.embracesource.infinispan.sesssion;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.log4j.Logger;

import com.embracesource.infinispan.sesssion.common.InfinispanSessionConstant;
import com.embracesource.infinispan.sesssion.config.InfinispanSessionConfiguration;
import com.embracesource.infinispan.sesssion.util.InfinispanSessionUtil;

public class InfinispanWebSession implements HttpSession, Serializable {
	Logger log = Logger.getLogger(InfinispanWebSession.class);
	private static final long serialVersionUID = 5180280145006283508L;
	private HttpSession httpSession; // 上下文session
	private InfinispanSessionConfiguration isc;
	private InfinispanSessionRemoteOperation isro;
	private Map<String, Object> attrs; // 标记属性
	private Map<String, Object> largeAttrs; // 标记size比较大的属性

	public InfinispanWebSession(HttpSession httpSession,
			InfinispanSessionConfiguration isc,
			InfinispanSessionRemoteOperation isro) {
		super();
		this.httpSession = httpSession;
		this.isc = isc;
		this.isro = isro;
	}

	public long getCreationTime() {
		// TODO Auto-generated method stub
		return httpSession.getCreationTime();
	}

	public String getId() {
		// TODO Auto-generated method stub
		return httpSession.getId();
	}

	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return httpSession.getLastAccessedTime();
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return httpSession.getServletContext();
	}

	public void setMaxInactiveInterval(int interval) {
		// TODO Auto-generated method stub
		httpSession.setMaxInactiveInterval(interval);
	}

	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return httpSession.getMaxInactiveInterval();
	}

	@SuppressWarnings("deprecation")
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return httpSession.getSessionContext();
	}

	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		Object value = httpSession.getAttribute(name);
		if (value == null) {
			value = httpSession.getAttribute(name
					+ InfinispanSessionConstant.LARGEATTRKEYSUFFIX);
			if (String.valueOf(value) == InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE) {
				// 从远程获取
				value = isro.getRemoteSingleLargeAttr(this.getId(), name);
				if (value != null) {
					httpSession.setAttribute(name, value);
				}
			}
		}
		return value;
	}

	@SuppressWarnings("deprecation")
	public Object getValue(String name) {
		// TODO Auto-generated method stub
		Object value = httpSession.getAttribute(name);
		if (value == null) {
			value = httpSession.getValue(name
					+ InfinispanSessionConstant.LARGEATTRKEYSUFFIX);
			if (String.valueOf(value) == InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE) {
				// 从远程获取
				value = isro.getRemoteSingleLargeAttr(this.getId(), name);
				if (value != null) {
					httpSession.putValue(name, value);
				}
			}
		}
		return value;
	}

	/**
	 * 注意：这个方法获取的数据存在数据不完整的风险
	 */
	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return httpSession.getAttributeNames();
	}

	/**
	 * 注意：这个方法获取的数据存在数据不完整的风险
	 */
	@SuppressWarnings("deprecation")
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return httpSession.getValueNames();
	}

	public void setLocalAttribute(String name, Object value) {
		httpSession.setAttribute(name, value);
	}

	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		httpSession.setAttribute(name, value);
		if (name != null && value != null
				&& !value.equals(httpSession.getAttribute(name))) {
			int vSize = 0;
			try {
				vSize = InfinispanSessionUtil.objectToByteArray(value);
			} catch (IOException e) {
				// TODO 记录日志
				vSize = isc.getAttrLimitSize() + 1;
			}
			if (vSize > isc.getAttrLimitSize()) {
				largeAttrs.put(name, value);
				httpSession.setAttribute(name
						+ InfinispanSessionConstant.LARGEATTRKEYSUFFIX,
						InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE);
				attrs.put(name + InfinispanSessionConstant.LARGEATTRKEYSUFFIX,
						InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE);

			} else {
				attrs.put(name, value);
			}

		}

	}

	@SuppressWarnings("deprecation")
	public void putValue(String name, Object value) {
		// TODO Auto-generated method stub
		httpSession.putValue(name, value);
		if (name != null && value != null
				&& !value.equals(httpSession.getValue(name))) {
			int vSize = 0;
			try {
				vSize = InfinispanSessionUtil.objectToByteArray(value);
			} catch (IOException e) {
				// TODO 记录日志
				vSize = isc.getAttrLimitSize() + 1;
			}
			if (vSize > isc.getAttrLimitSize()) {
				largeAttrs.put(name, value);
				httpSession.putValue(name
						+ InfinispanSessionConstant.LARGEATTRKEYSUFFIX,
						InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE);
				attrs.put(name + InfinispanSessionConstant.LARGEATTRKEYSUFFIX,
						InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE);

			} else {
				attrs.put(name, value);
			}

		}

	}

	public void removeLocalAttribute(String name) {
		httpSession.removeAttribute(name);
	}

	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
		httpSession.removeAttribute(name);
		httpSession.removeAttribute(name
				+ InfinispanSessionConstant.LARGEATTRKEYSUFFIX);
		if (name != null) {
			attrs.put(name, "");
			if (httpSession.getAttribute(name
					+ InfinispanSessionConstant.LARGEATTRKEYSUFFIX) == InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE) {
				attrs.put(name + InfinispanSessionConstant.LARGEATTRKEYSUFFIX,
						"");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void removeValue(String name) {
		// TODO Auto-generated method stub
		httpSession.removeValue(name);
		httpSession.removeValue(name
				+ InfinispanSessionConstant.LARGEATTRKEYSUFFIX);
		if (name != null) {
			attrs.put(name, "");
			attrs.put(name + InfinispanSessionConstant.LARGEATTRKEYSUFFIX, "");
		}
	}

	public void invalidateLocal() {
		httpSession.invalidate();
	}

	public void invalidate() {
		// TODO Auto-generated method stub
		httpSession.invalidate();
		// 校验是否清除远程数据
		if (validateRemote(httpSession)) {
			isro.removeRemoteMetaDatas(httpSession.getId());
		}

	}

	/**
	 * 校验远程session是否应该清除
	 * 
	 * @param httpSession
	 * @return
	 */
	private boolean validateRemote(HttpSession httpSession) {
		// TODO Auto-generated method stub
		Map<String, Object> metaData = isro.getRemoteMetaDatas(httpSession.getId(),
				false);
		if (metaData != null) {
			long remoteLastAccessedTime = Long
					.parseLong(String.valueOf(metaData
							.get(InfinispanSessionConstant.remoteSessionMetaDataAttrs.LASTACCESSEDTIME
									.value())));
			return remoteLastAccessedTime > httpSession.getLastAccessedTime() ? false
					: true;
		}
		return false;
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return httpSession.isNew();
	}

}
