package com.ratata.nativeQueryRest.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LockUtil {
	private static boolean lockFlag = false;

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
}
