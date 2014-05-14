package com.embracesource.infinispan.sesssion.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.session.ManagerBase;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.infinispan.client.hotrod.impl.operations.PingOperation;

import com.embracesource.infinispan.sesssion.InfinispanSessionRemoteOperation;
import com.embracesource.infinispan.sesssion.InfinispanWebSession;
import com.embracesource.infinispan.sesssion.cache.InfinispanRemoteCache;
import com.embracesource.infinispan.sesssion.common.InfinispanSessionConstant;
import com.embracesource.infinispan.sesssion.config.InfinispanSessionConfiguration;

public class InfinispanSessionHelper {
	private static InfinispanSessionHelper infinispanSessionHelper;
	private InfinispanSessionConfiguration isc;
	private InfinispanRemoteCache infinispanCacheRemote = null;
	private AtomicBoolean isAvailableCluster = new AtomicBoolean(true);
	private Thread deadDetectThread = null;
	private InfinispanSessionRemoteOperation isro = null;
	private ThreadLocal<Map<String,Object>> remoteMetaDataMap = new ThreadLocal<Map<String,Object>>();

	private InfinispanSessionHelper(InfinispanSessionConfiguration isc) {
		this.isc = isc;
	}

	public static InfinispanSessionHelper instance(
			InfinispanSessionConfiguration isc) {
		if (infinispanSessionHelper == null) {
			infinispanSessionHelper = new InfinispanSessionHelper(isc);
			infinispanSessionHelper.init();
		}
		return infinispanSessionHelper;
	}

	/**
	 * 判断用户是否已经登录其他地点登录
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean isLogined(String userId, HttpSession session) {
		session.setAttribute(InfinispanSessionConstant.USERLOGINID, userId);
		return infinispanSessionHelper.isro.existRemoteUserId(userId);
	}

	public synchronized void init() {
		if (infinispanCacheRemote == null) {
			this.infinispanCacheRemote = new InfinispanRemoteCache(
					isc.getCacheName(), isc.getInfinispanPropertiesName());
			infinispanCacheRemote.init();
		}
		if (deadDetectThread != null) {
			deadDetectThread = this
					.deadDetectCluster((RemoteCacheImpl<String, Object>) infinispanCacheRemote
							.getRemoteCache());
		}
		if (isro == null) {
			isro = new InfinispanSessionRemoteOperation(
					infinispanCacheRemote.getRemoteCache());
		}

		deadDetectThread.start();
	}

	public InfinispanSessionRemoteOperation getIsro() {
		return isro;
	}

	@SuppressWarnings("deprecation")
	public void destroy() {
		if (infinispanCacheRemote != null) {
			infinispanCacheRemote.destroy();
		}
		if (deadDetectThread != null) {
			deadDetectThread.stop();
		}
	}

	/**
	 * 生成sessionId
	 * 
	 * @param servletRequest
	 * @param remoteCache
	 * @return
	 * @throws Exception
	 */
	public String generateSessionId(HttpServletRequest servletRequest,
			RemoteCache<String, Object> remoteCache) throws Exception {
		ManagerBase managerBase = getManagerBase(servletRequest);
		Method method = getGenerateSessionIdtMethod(managerBase);
		String sessionId = String.valueOf(method.invoke(managerBase));
		// 校验sessionId是否已经存在
		while (existSessionId(sessionId, remoteCache)) {
			sessionId = String.valueOf(method.invoke(managerBase));
		}
		return sessionId;
	}

	/**
	 * 创建session通过指定的sessionId
	 * 
	 * @param servletRequest
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	private HttpSession creatSession(HttpServletRequest servletRequest,
			String sessionId) throws Exception {
		ManagerBase managerBase = getManagerBase(servletRequest);
		Session session = managerBase.createSession(sessionId);
		managerBase.add(session);
		return session.getSession();
	}

	public void enter(HttpServletRequest httpServletRequest) {
		// 校验本地和远程是否有此会话
		synchronized (httpServletRequest.getRequestedSessionId()) {
			boolean isExistLocal = validateExistSessionForLocal(httpServletRequest
					.getSession(false));
			boolean isExistRemote = validateExistSessionForRemote(httpServletRequest
					.getRequestedSessionId());
			if (!isExistLocal && isExistRemote) {
				try {
					// 根据已有的会话标识，在本地创建会话
					this.creatSession(httpServletRequest,
							httpServletRequest.getRequestedSessionId());
					// 将远程相应会话信息同步至本地
					HttpSession session = httpServletRequest.getSession();
					Map<String, Object> attrs = isro.getRemoteAttrs(session
							.getId());
					if (session instanceof InfinispanWebSession) {
						recoverSessionAttrs(attrs, session);
					}
				} catch (Exception e) {
					// TODO 记录日志
				}
			} else if (isExistLocal && isExistRemote) {
				// 校验本会话是否已被踢出
				if (validateOutedSession(httpServletRequest.getSession())) {
					// 销毁本地session
					httpServletRequest.getSession(false).invalidate();
				} else if (validateDataUpdated(httpServletRequest
						.getSession(false))) {// 校验本地session和远程session数据是否一致
					// 若本地远程session数据更新则将远程数据同步至本地
					// TODO
				}
			}
		}
	}

	private boolean validateDataUpdated(HttpSession session) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateOutedSession(HttpSession session) {
		String loalUserId=String.valueOf(session.getAttribute(InfinispanSessionConstant.USERLOGINID));
		Map<String,Object> metaData=remoteMetaDataMap.get();
		String remoteUserId=(String) metaData.get(InfinispanSessionConstant.remoteSessionMetaDataAttrs.CURRENTUSERID.value());
		
		return false;
	}

	private void recoverSessionAttrs(Map<String, Object> recoverAttrs,
			HttpSession session) {
		InfinispanWebSession iwSession = (InfinispanWebSession) session;
		Map<String,Object> metaData=remoteMetaDataMap.get();
		@SuppressWarnings("unchecked")
		Set<String> attrNames =(Set<String>) metaData.get(InfinispanSessionConstant.remoteSessionMetaDataAttrs.ATTRNAMES.value()) ;
		if (attrNames != null && recoverAttrs != null) {
			for (String attr : attrNames) {
				Object value = recoverAttrs.get(attr);
				if (value != null) {
					iwSession.setLocalAttribute(attr, value);
				} else if (attr
						.endsWith(InfinispanSessionConstant.LARGEATTRKEYSUFFIX)) {
					iwSession.setLocalAttribute(attr,
							InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE);
				}

			}

		} else if (attrNames == null && recoverAttrs != null) {
			for (Map.Entry<String, Object> entry : recoverAttrs.entrySet()) {
				if (entry.getValue() != null) {
					iwSession.setLocalAttribute(entry.getKey(),
							entry.getValue());
				}

			}

		}
	}

	private boolean validateExistSessionForLocal(HttpSession session) {
		return session!=null?true:false;
	}

	private boolean validateExistSessionForRemote(String sessionId) {
		Map<String, Object> remoteMetadata = isro.getRemoteMetaDatas(
				sessionId, true);
		if (remoteMetadata != null) {
			remoteMetaDataMap
					.set(remoteMetadata);
			return true;
		}
		return false;
	}

	public void exit(HttpServletRequest httpServletRequest) {
		// 校验远程是否有此会话
		synchronized (httpServletRequest.getSession(false)) {
			if (!validateExistSessionForLocal(httpServletRequest
					.getSession(false))) {
				// 将本地会话信息同步至远程
				// TODO
			}
			// 将本地session元数据同步至远程
			// TODO
			// 校验Attrs是否有更新
			if (validateDataUpdated(httpServletRequest.getSession(false))) {
				// 将session Attr数据同步至远程
				// TODO
			}
			// 校验large Attrs是否有更新
			if (validateSessionLargeAttrsUpdated(httpServletRequest
					.getSession())) {
				// 将session large Attr数据同步至远程
				// TODO
			}
			remoteMetaDataMap.remove();
		}

	}

	private boolean validateSessionLargeAttrsUpdated(HttpSession session) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 集群死亡探测
	 * 
	 * @param remoteCacheImpl
	 */
	private Thread deadDetectCluster(
			final RemoteCacheImpl<String, Object> remoteCacheImpl) {
		final long sleepTime = isc.getDeadDetectIntervalTime();

		return new Thread(new Runnable() {
			public void run() {
				int success = 0;
				int fail = 0;
				PingOperation.PingResult ps = remoteCacheImpl.ping();
				while (true) {
					try {
						Thread.sleep(sleepTime < 0 ? 5000 : sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (ps.equals(PingOperation.PingResult.SUCCESS)) {
						fail = 0;
						if (success++ >= isc.getDeadDetectMinSuccess()) {
							isAvailableCluster.set(true);
						}
						;
					} else {
						success = 0;
						if (fail++ > isc.getDeadDetectMaxFail()) {
							isAvailableCluster.set(false);
						}
					}
				}

			}
		});

	}

	@SuppressWarnings("unused")
	@Deprecated
	private String doGenerateSessionId(HttpServletRequest servletRequest)
			throws Exception {
		String sessionId = null;
		ManagerBase managerBase = getManagerBase(servletRequest);
		Method method = getGenerateSessionIdtMethod(managerBase);
		sessionId = String.valueOf(method.invoke(managerBase));
		return sessionId;
	}

	@SuppressWarnings("rawtypes")
	private ManagerBase getManagerBase(HttpServletRequest servletRequest)
			throws Exception {
		ManagerBase managerBase = null;
		if (servletRequest instanceof RequestFacade) {
			RequestFacade requestFacade = (RequestFacade) servletRequest;
			Class cls = requestFacade.getClass();
			Field rf = cls.getDeclaredField("request");
			rf.setAccessible(true);
			Request request_ = (Request) rf.get(requestFacade);
			Class rcls = request_.getClass();
			Field raf = rcls.getDeclaredField("random");
			raf.setAccessible(true);
			@SuppressWarnings("unused")
			Random random = (Random) raf.get(request_);
			Field cf = rcls.getDeclaredField("context");
			cf.setAccessible(true);
			StandardContext sc = (StandardContext) cf.get(request_);
			Class scls = sc.getClass();
			Field mf = scls.getSuperclass().getDeclaredField("manager");
			mf.setAccessible(true);
			managerBase = (ManagerBase) mf.get(sc);
		}
		return managerBase;
	}

	@SuppressWarnings("rawtypes")
	private Method getGenerateSessionIdtMethod(ManagerBase managerBase)
			throws Exception {
		Class mcls = managerBase.getClass();
		@SuppressWarnings("unchecked")
		Method gmtd = mcls.getDeclaredMethod("generateSessionId");
		gmtd.setAccessible(true);
		return gmtd;
	}

	private boolean existSessionId(String sessionId,
			RemoteCache<String, Object> remoteCache) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAvailableCluster() {
		return isAvailableCluster.get();
	}

	public InfinispanSessionConfiguration getIsc() {
		return isc;
	}

}
