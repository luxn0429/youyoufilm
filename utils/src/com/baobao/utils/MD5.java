package com.baobao.utils;

import java.security.MessageDigest;

public class MD5 {
	
	public static String getSignature(String param,String key){
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', 
							'9', 'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(param.getBytes());
			messageDigest.update(key.getBytes());
			byte[] md5 = messageDigest.digest();
			int length = md5.length;
			char str[] = new char[length * 2];
			int k = 0;
			for (int i = 0; i < length; i++) {
				byte byte0 = md5[i];
				str[k++] = hexDigits[byte0 >>>4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args){
		System.out.println(MD5.getSignature(System.currentTimeMillis()+"luanru&1252048636749","123123123"));
		System.out.println(MD5.getSignature("luanru&1252048636749","123123123").equals(MD5.getSignature("luanru&1252048636749","123123123")));
	}
	
}
