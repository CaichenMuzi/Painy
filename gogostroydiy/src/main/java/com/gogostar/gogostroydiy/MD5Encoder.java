package com.gogostar.gogostroydiy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/6/26.
 */

public class MD5Encoder {

	public static String encode(String pwd) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(pwd.getBytes());
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String s = Integer.toHexString(0xff & bytes[i]);
				if (s.length() == 1) {
					stringBuffer.append("0" + s);
				} else {
					stringBuffer.append(s);
				}
			}

			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("MD5工具出错");
		}
	}
}
