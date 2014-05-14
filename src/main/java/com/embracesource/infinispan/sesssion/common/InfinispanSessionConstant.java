package com.embracesource.infinispan.sesssion.common;

public class InfinispanSessionConstant {
	public static String LARGEATTRKEYSUFFIX = "_largeKey";
	public static String DEFAULTLARGEATTFLAGVALUE = "1"; // default large attr key's flag value
	public static String USERLOGINID="userId@login.flag";
	public static enum EnableSessionMode {
		LOCALCACHE(0), REMOTECACHE(1), LRBOTHCACHE(2);
		private final int mode;

		private EnableSessionMode(int mode) {
			this.mode = mode;
		}

		public int value() {
			return mode;
		}
	}

//	public static enum ConfigPropertieNames {
//		SESSIONMODE("sessionMode"), ATTRLIMITSIZE("attrLimitSize"), INFINISPANPSESSIONROPERTIES(
//				"infinispanSessionProperties"), INFINISPANCACHENAME("cacheName"), DEADDETECTMINSUCCESS(
//				"deadDetectMinSuccess"), DEADDETECTMAXFAIL("deadDetectMaxFail"), DEADDETECTINTERVALTIME(
//				"deadDetectIntervalTime"), ISLOGINSTORE("isLoginStore");
//		private final String parameter;
//
//		private ConfigPropertieNames(String parameter) {
//			this.parameter = parameter;
//		}
//
//		public String value() {
//			return parameter;
//		}
//	}

	public static enum sessionDataKeyFlag {
		ATTRFALG("_@a."), LARGEATTR("_@la."), METADATAFALG("_@md."), USERIDFALG(
				"_@ui.");
		private String flag;

		private sessionDataKeyFlag(String flag) {
			this.flag = flag;
		}

		public String value() {
			return this.flag;
		}
	}

	public static enum remoteSessionMetaDataAttrs {
		ATTRNAMES("attrNames"), CURRENTSESSIONID("currentSessionId"),CURRENTUSERID("currentUserId"), CLIENTLOGINIP(
				"clientLoginIp"), LASTACCESSEDTIME("lastAccessedTime"),MAXINACTIVEINTERVAL("maxInactiveInterval");
		private String name;

		private remoteSessionMetaDataAttrs(String name) {
			this.name = name;
		}

		public String value() {
			return this.name;
		}
	}
}
