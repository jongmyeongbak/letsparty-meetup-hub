package com.letsparty.web.websocket.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageCon {

	private int type;
	private long unreadCnt;
	
	public ChatMessageCon(int type, long unreadCnt) {
		super();
		this.type = type;
		this.unreadCnt = unreadCnt;
	}
}
