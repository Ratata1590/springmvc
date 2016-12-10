package com.ratata.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {
	public static String MD5HashPassword(String username, String password) {

		MessageDigest messageDigest=null;
		StringBuffer stringBuffer = new StringBuffer();

		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		messageDigest.update((Constants.System_key + username + password).getBytes());

		byte byteData[] = messageDigest.digest();

		for (int i = 0; i < byteData.length; i++) {
			stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}
}
