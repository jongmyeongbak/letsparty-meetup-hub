package com.letsparty.web.websocket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageDto {

	private long no;
	private int type;
	private int userNo;
	private int unreadCnt;
	private String time;
	private String text;

	@Builder
	public ChatMessageDto(long no, int type, int userNo, int unreadCnt, String time, String text) {
		super();
		this.no = no;
		this.type = type;
		this.userNo = userNo;
		this.unreadCnt = unreadCnt;
		this.time = time;
		this.text = text;
	}
}
