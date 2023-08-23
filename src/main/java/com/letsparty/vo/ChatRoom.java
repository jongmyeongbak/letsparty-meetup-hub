package com.letsparty.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatRoom {

	private int no;
	private String id;
	private int chattersCnt;
	private Integer partyNo;
	private Integer creatorNo;
	private boolean isPublic;
	private String title;
	private String description;
	
	@Builder
	public ChatRoom(String id, int chattersCnt, Integer partyNo, Integer creatorNo, boolean isPublic, String title,
			String description) {
		super();
		this.id = id;
		this.chattersCnt = chattersCnt;
		this.partyNo = partyNo;
		this.creatorNo = creatorNo;
		this.isPublic = isPublic;
		this.title = title;
		this.description = description;
	}
	
	
}
