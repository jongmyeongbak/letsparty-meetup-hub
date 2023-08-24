package com.letsparty.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatUser {

	private String roomId;
	private int userNo;
	private long lastReadMessageNo;
	
	@Builder
	public ChatUser(String roomId, int userNo) {
		super();
		this.roomId = roomId;
		this.userNo = userNo;
	}
}
