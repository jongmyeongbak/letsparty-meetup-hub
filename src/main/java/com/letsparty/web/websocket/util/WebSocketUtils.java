package com.letsparty.web.websocket.util;

public class WebSocketUtils {

	private WebSocketUtils() {
	}
	
	public static String getLastVariableFromDestination(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return ""; // 올바른 형식이 아닌 경우에는 ""을 반환합니다.
        }
	}

//	public static String getRoomIdFromDestination(String destination) {
//		// 예시: destination이 "/chat/123456"와 같은 형식으로 주어진다고 가정합니다.
//		// "/chat/" 다음에 오는 6자리 숫자를 roomId로 추출합니다.	
//		String[] parts = destination.split("/");
//		if (parts.length >= 3) {
//			return parts[2]; // 두 번째 요소가 roomId입니다.
//		} else {
//			return null; // 올바른 형식이 아닌 경우에는 null을 반환합니다.
//		}
//	}
}
