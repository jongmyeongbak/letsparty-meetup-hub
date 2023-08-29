package com.letsparty.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatUser {

	private String roomId;
	private int userNo;
	private long joinMessageNo;
	private long lastReadMessageNo;
	
	@Builder
	public ChatUser(String roomId, int userNo, long joinMessageNo, long lastReadMessageNo) {
		super();
		this.roomId = roomId;
		this.userNo = userNo;
		this.joinMessageNo = joinMessageNo;
		this.lastReadMessageNo = lastReadMessageNo;
	}
}
