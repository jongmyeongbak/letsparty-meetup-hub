package com.letsparty.web.websocket.dto;

public enum MessageType {

	TALK(0), // 대화 메시지
	JOIN(1), // 입장 메시지
	EXIT(2), // 퇴장 메시지
	CON(3); // 접속 메시지. 읽은최종메시지번호(접속 직전)를 전달하기 위한 구분
	
	private final int value;
	
	MessageType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
