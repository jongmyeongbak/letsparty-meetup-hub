package com.letsparty.web.websocket.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageJoin {

	private long no;
	private int type;
	private LocalDateTime createdAt;
	private int userNo;
	private String nickname;
	private String filename;
	private boolean isUrl;
	
	public ChatMessageJoin(long no, int type, LocalDateTime createdAt) {
		super();
		this.no = no;
		this.type = type;
		this.createdAt = createdAt;
	}
}
