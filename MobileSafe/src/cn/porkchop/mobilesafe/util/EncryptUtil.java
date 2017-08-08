package cn.porkchop.mobilesafe.util;

import java.io.UnsupportedEncodingException;

public class EncryptUtil {
	public static String encrypt(String s,byte seed) {
		try {
			byte[] bytes = s.getBytes("utf-8");
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] ^= seed;
			}
			//如果再转换成utf-8,会变成不可转换的乱码
			return new String(bytes, "ISO-8859-1");
		} catch (Exception e) {
			return "";
		}
	}
	public static String decrypt(String s,byte seed){
		try {
			byte[] bytes = s.getBytes("ISO-8859-1");
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] ^= seed;
			}
			return new String(bytes, "utf-8");
		} catch (Exception e) {
			return "";
		}
	}	
	
}
