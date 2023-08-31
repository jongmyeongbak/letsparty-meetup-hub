package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessage {

	private long no;
	private long roomNo;
	private int type;
	private int userNo;
	private LocalDateTime createdAt;
	private Long unreadCnt;
	private String text;
	
	@Builder
	public ChatMessage(long roomNo, int type, int userNo, LocalDateTime createdAt, Long unreadCnt, String text) {
		super();
		this.roomNo = roomNo;
		this.type = type;
		this.userNo = userNo;
		this.createdAt = createdAt;
		this.unreadCnt = unreadCnt;
		this.text = text;
	}
}
