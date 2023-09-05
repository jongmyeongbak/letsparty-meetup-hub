package com.letsparty.util;

public class PhoneNumberFormatter {
	
	public static String formatPhoneNumber(String tel) {
		if (tel == null || tel.length() != 13 || !tel.matches("\\d{3}-\\d{4}-\\d{4}")) {
			return tel;
		}
		
		return tel.substring(0, 6) + "**" + tel.substring(8, 11) + "**";
	}
}