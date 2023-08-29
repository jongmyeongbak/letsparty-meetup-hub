package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

	private long no;
	private long roomNo;
	private int type;
	private int userNo;
	private LocalDateTime createAt;
	private Integer unreadCnt;
	private String text;
	
	@Builder
	public ChatMessage(long roomNo, int type, int userNo, LocalDateTime createAt, Integer unreadCnt, String text) {
		super();
		this.roomNo = roomNo;
		this.type = type;
		this.userNo = userNo;
		this.createAt = createAt;
		this.unreadCnt = unreadCnt;
		this.text = text;
	}
}
