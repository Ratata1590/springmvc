package com.ratata.nativeQueryRest.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LockUtil {
	private static boolean lockFlag = false;

	public static Map<String, Boolean> lockList = new HashMap<String, Boolean>();

	public static boolean isLockFlag() {
		return lockFlag;
	}

	private static String unlockKey;
	private static String hint;

	public static String lock(String password, String hintString) throws NoSuchAlgorithmException {
		if (!lockFlag) {
			unlockKey = hashPassword(password);
			lockFlag = true;
			hint = hintString;
			return unlockKey;
		}
		return hint;
	}

	public static String unlock(String key) {
		if (unlockKey.equals(key)) {
			lockFlag = false;
			unlockKey = "";
			hint = "";
			return "unlocked";
		}
		return hint;
	}

	private static String hashPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public static void initLockList() {
		LockUtil.lockList.put("/directQuery", false);
		LockUtil.lockList.put("/saveNestedObject", false);
		LockUtil.lockList.put("/nativeQueryJson_POST", false);
		LockUtil.lockList.put("/nativeQueryJson_GET", false);
		LockUtil.lockList.put("/nativeQueryTransaction_GET", false);
		LockUtil.lockList.put("/nativeQueryTransaction_POST", false);
		LockUtil.lockList.put("/saveQueryList", false);
		LockUtil.lockList.put("/updateQueryList", false);
		LockUtil.lockList.put("/getQueryList", false);
		LockUtil.lockList.put("/customQuery", false);
		LockUtil.lockList.put("/tableFieldsToResultset", false);
		LockUtil.lockList.put("/directQueryToQueryObject", false);
		LockUtil.lockList.put("/getEntityMapDetail", false);
		LockUtil.lockList.put("/getEntityMap", false);
		LockUtil.lockList.put("/initObject", false);
		LockUtil.lockList.put("/saveObject", false);
		LockUtil.lockList.put("/linkObject", false);
		LockUtil.lockList.put("/updateObject", false);
		LockUtil.lockList.put("/deleteObject", false);
	}
}
