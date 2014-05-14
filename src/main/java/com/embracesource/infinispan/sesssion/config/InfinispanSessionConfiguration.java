package com.embracesource.infinispan.sesssion.config;

import javax.servlet.FilterConfig;

import com.embracesource.infinispan.sesssion.util.InfinispanSessionUtil;

public class InfinispanSessionConfiguration {
	private static InfinispanSessionConfiguration isc;
	private static String infinispanPropertiesName = "hotrod-client.properties"; // infinispan属性配置文件
	private static int sessionMode = 0; // session 模型
	private static int attrLimitSize = 1000; // attr限制大小，当attr大小超过此值则单独存取,单位Bit
	private static String cacheName = "infinispan-session"; // 缓存名称
	private static int deadDetectMinSuccess = 3; // 集群死亡探测成功次数，大于或等于此数则认为集群正常运行
	private static int deadDetectMaxFail = 6; // 集群死亡探测失败次数，大于或等于此数则认为集群不可用
	private static int deadDetectIntervalTime = 5000;// 单位ms
	private static  int isLoginStore = 1;

	private InfinispanSessionConfiguration() {
	}

	public static InfinispanSessionConfiguration instance(
			FilterConfig filterConfig) {
		if (isc == null && filterConfig != null) {
			infinispanPropertiesName = filterConfig
					.getInitParameter("infinispanPropertiesName");
			cacheName=filterConfig
					.getInitParameter("cacheName");
			sessionMode = InfinispanSessionUtil.strToInt(
					filterConfig.getInitParameter("sessionMode"), 0);
			attrLimitSize = InfinispanSessionUtil.strToInt(
					filterConfig.getInitParameter("attrLimitSize"), 1000);
			deadDetectMinSuccess= InfinispanSessionUtil.strToInt(
					filterConfig.getInitParameter("deadDetectMinSuccess"), 3);
			deadDetectMaxFail= InfinispanSessionUtil.strToInt(
					filterConfig.getInitParameter("deadDetectMaxFail"), 6);
			deadDetectIntervalTime= InfinispanSessionUtil.strToInt(
					filterConfig.getInitParameter("deadDetectIntervalTime"), 5000);
			isLoginStore= InfinispanSessionUtil.strToInt(
					filterConfig.getInitParameter("isLoginStore"), 1);
		}
		return isc;

	}

	public String getInfinispanPropertiesName() {
		return infinispanPropertiesName;
	}

	public void setInfinispanPropertiesName(String infinispanPropertiesName) {
		InfinispanSessionConfiguration.infinispanPropertiesName = infinispanPropertiesName;
	}

	public int getSessionMode() {
		return sessionMode;
	}

	public void setSessionMode(int sessionMode) {
		InfinispanSessionConfiguration.sessionMode = sessionMode;
	}

	public int getAttrLimitSize() {
		return attrLimitSize;
	}

	public void setAttrLimitSize(int attrLimitSize) {
		InfinispanSessionConfiguration.attrLimitSize = attrLimitSize;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		InfinispanSessionConfiguration.cacheName = cacheName;
	}

	public int getDeadDetectMinSuccess() {
		return deadDetectMinSuccess;
	}

	public void setDeadDetectMinSuccess(int deadDetectMinSuccess) {
		InfinispanSessionConfiguration.deadDetectMinSuccess = deadDetectMinSuccess;
	}

	public int getDeadDetectMaxFail() {
		return deadDetectMaxFail;
	}

	public void setDeadDetectMaxFail(int deadDetectMaxFail) {
		InfinispanSessionConfiguration.deadDetectMaxFail = deadDetectMaxFail;
	}

	public int getDeadDetectIntervalTime() {
		return deadDetectIntervalTime;
	}

	public void setDeadDetectIntervalTime(int deadDetectIntervalTime) {
		InfinispanSessionConfiguration.deadDetectIntervalTime = deadDetectIntervalTime;
	}

	public static int getIsLoginStore() {
		return isLoginStore;
	}

	public static void setIsLoginStore(int isLoginStore) {
		InfinispanSessionConfiguration.isLoginStore = isLoginStore;
	}

}
