package com.embracesource.infinispan.sesssion.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class InfinispanSessionUtil {
	/**
	 * 计算对象的大小，单位为byte
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static int objectToByteArray(Object obj) throws IOException {
		ByteArrayOutputStream bo = null;
		ObjectOutputStream oo = null;
		byte[] bytes = null;
		try {
			bo = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			bytes = bo.toByteArray();
		} finally {
			bo.close();
			oo.close();
		}
		return bytes != null ? bytes.length : 0;
	}

	public static long strToLong(String str, long defatulNum) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			// TODO 记录日志
			return defatulNum;
		}
	}

	public static int strToInt(String str, int defatulNum) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			// TODO 记录日志
			return defatulNum;
		}
	}
}
