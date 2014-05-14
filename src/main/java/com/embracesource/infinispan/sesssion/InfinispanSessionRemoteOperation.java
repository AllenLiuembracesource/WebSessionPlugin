package com.embracesource.infinispan.sesssion;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.infinispan.client.hotrod.RemoteCache;

import com.embracesource.infinispan.sesssion.common.InfinispanSessionConstant;
import com.embracesource.infinispan.sesssion.util.InfinispanSessionUtil;

public class InfinispanSessionRemoteOperation {
	private RemoteCache<String, Object> remoteCache;

	public InfinispanSessionRemoteOperation(
			RemoteCache<String, Object> remoteCache) {
		this.remoteCache = remoteCache;
	}

	/**
	 * 存储用户唯一标识（userId=sessionId）
	 * 
	 * @param metaData
	 */
	public void putRemoteUserId(String userId, String sessionId) {
		String key = userId
				+ InfinispanSessionConstant.sessionDataKeyFlag.USERIDFALG
						.value(), value = sessionId;
		try {
			remoteCache.putAsync(key, value);
		} catch (Exception e) {
			// TODO 记录日志
			e.printStackTrace();
		}

	}

	/**
	 * 检查 用户唯一标识是否已存在
	 * 
	 * @param metaData
	 */
	public boolean existRemoteUserId(String userId) {
		String key = userId
				+ InfinispanSessionConstant.sessionDataKeyFlag.USERIDFALG
						.value();
		try {
			return remoteCache.containsKey(key);
		} catch (Exception e) {
			// TODO 记录日志
			return false;
		}
	}

	/**
	 * 检查 会话唯一标识是否已存在
	 * 
	 * @param metaData
	 */
	public boolean existRemoteSessionId(String sessionId) {
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.METADATAFALG
						.value();
		try {
			return remoteCache.containsKey(key);
		} catch (Exception e) {
			// TODO 记录日志
			return false;
		}
	}

	/**
	 * 存储session元数据至远程
	 * 
	 * @param metaData
	 */
	public void putRemoteMetaDatas(String sessionId,
			Map<String, Object> metaDatas) {
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.METADATAFALG
						.value();
		Object maxInternal = metaDatas
				.get(InfinispanSessionConstant.remoteSessionMetaDataAttrs.MAXINACTIVEINTERVAL
						.value());
		try {
			remoteCache.putAsync(key, metaDatas, InfinispanSessionUtil
					.strToInt(String.valueOf(maxInternal), 30),
					TimeUnit.MINUTES);
		} catch (Exception e) {
			// TODO 记录日志
			e.printStackTrace();
		}

	}

	/**
	 * 删除session元数据至远程
	 * 
	 * @param metaData
	 */
	public void removeRemoteMetaDatas(String sessionId) {
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.METADATAFALG
						.value();
		try {
			remoteCache.removeAsync(key);
		} catch (Exception e) {
			// TODO 记录日志
			e.printStackTrace();
		}

	}

	/**
	 * 获取session元数据至远程
	 * 
	 * @param originalSession
	 * @param validateAttr
	 * @return
	 */
	public Map<String, Object> getRemoteMetaDatas(String sessionId,
			boolean validateAttr) {
		if (sessionId == null) {
			// TODO 记录日志
			return null;
		}
		
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.METADATAFALG
						.value();
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> metaData = (Map<String, Object>) remoteCache
					.get(key);
//			if (validateAttr) {
//				@SuppressWarnings("unchecked")
//				Set<String> remoteAttrNames = (Set<String>) metaData
//						.get(InfinispanSessionConstant.remoteSessionMetaDataAttrs.ATTRNAMES
//								.value());
//				if (remoteAttrNames != null) {
//					for (String attr : remoteAttrNames) {
//						if (attr.endsWith(InfinispanSessionConstant.LARGEATTRKEYSUFFIX)
//								&& originalSession.getAttribute(attr) != InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE) {
//							originalSession
//									.setAttribute(
//											attr,
//											InfinispanSessionConstant.DEFAULTLARGEATTFLAGVALUE);
//						}
//					}
//				}
//			}
			return metaData;
		} catch (Exception e) {
			// TODO 记录日志
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 存储session attr 至远程
	 * 
	 * @param attrs
	 */
	public void putRemoteAttrs(String sessionId, Map<String, Object> attrs) {
		if (sessionId == null || attrs == null) {
			// TODO 记录日志
			return;
		}
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.ATTRFALG.value();
		try {
			if (sessionId != null && attrs != null) {
				remoteCache.putAsync(key, attrs);
			} else {
				// TODO 记录日志
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO 记录日志
		}
	}

	/**
	 * 获取session attr 至远程
	 * 
	 * @param attrs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getRemoteAttrs(String sessionId) {
		if (sessionId == null) {
			// TODO 记录日志
			return null;
		}
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.ATTRFALG.value();
		try {
			Map<String, Object> attrs = (Map<String, Object>) remoteCache
					.get(key);
			return attrs;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO 记录日志
			return null;
		}

	}

	/**
	 * 存储session large attr 至远程
	 * 
	 * @param largeAttrs
	 */
	public void putRemoteLargeAttrs(String sessionId,
			Map<String, Object> largeAttrs) {
		if (sessionId == null || largeAttrs == null) {
			// TODO 记录日志
			return;
		}
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.LARGEATTR
						.value();
		try {
			remoteCache.putAsync(key, largeAttrs);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO 记录日志
		}
	}

	/**
	 * 获取session large attr 至远程
	 * 
	 * @param largeAttr
	 */
	public Object getRemoteSingleLargeAttr(String sessionId, String attrName) {
		if (sessionId == null || attrName == null) {
			// TODO 记录日志
			return null;
		}
		String key = sessionId
				+ InfinispanSessionConstant.sessionDataKeyFlag.LARGEATTR
						.value() + attrName;
		try {
			return remoteCache.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO 记录日志
			return null;
		}

	}

}
