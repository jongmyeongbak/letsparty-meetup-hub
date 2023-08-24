package com.letsparty.web.websocket.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageDto {

	private String no;
	private int type;
	private int userNo;
	private int unreadCnt;
	private String time;
	private String text;
}
