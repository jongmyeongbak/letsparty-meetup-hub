package com.letsparty.util;

public class TimeConverter {

	public static String convertToAmPm(String time) {
		if (time == null) {
			return "하루 종일";
		}
		String[] parts = time.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("유효한 시간이 아닙니다.");
		}
		
		int hour = Integer.parseInt(parts[0]);
		
		if (hour < 12) {
			if (hour == 0) {
				return "오전 12:" + parts[1];
			}
			else {
				return "오전 " + hour + ":" + parts[1];
			}
		} else {
			if (hour == 12) {
				return "오후 " + hour + ":" + parts[1];
			} else {
				return "오후 " + (hour - 12) + ":" + parts[1];
			}
		}
		
	}
}
