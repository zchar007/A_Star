package com.sept.exception;

import java.util.HashMap;

public class ExceptionNames {
	public static int defaultCode = -1;
	public static String defaultValue = "异常";
	private static HashMap<Integer, String> hmExceptionNames = new HashMap<Integer, String>();

	public ExceptionNames() {
		hmExceptionNames.put(Integer.valueOf(defaultCode), defaultValue);
	}

	public static String addExceptionName(Integer key, String value) throws Exception {
		synchronized (hmExceptionNames) {
			if (defaultCode == key.intValue()) {
				throw new Exception("无法修改defaultCode");
			}
			return (String) hmExceptionNames.put(key, value);
		}
	}

	public static void removeExceptionName(Integer key) throws Exception {
		synchronized (hmExceptionNames) {
			if (defaultCode == key.intValue()) {
				throw new Exception("无法删除defaultCode");
			}
			if (hmExceptionNames.containsKey(key))
				hmExceptionNames.remove(key);
		}
	}

	public static String getExceptionValue(Integer key) {
		synchronized (hmExceptionNames) {
			return (String) hmExceptionNames.get(key);
		}
	}
}