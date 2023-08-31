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
	private Party party;
	private Integer creatorNo;
	private boolean isPublic;
	private boolean isEssential;
	private String title;
	private String description;
	
	public ChatRoom() {}
	
	@Builder
	public ChatRoom(String id, int chattersCnt, Party party, Integer creatorNo, boolean isPublic, boolean isEssential, String title,
			String description) {
		super();
		this.id = id;
		this.chattersCnt = chattersCnt;
		this.party = party;
		this.creatorNo = creatorNo;
		this.isPublic = isPublic;
		this.isEssential = isEssential;
		this.title = title;
		this.description = description;
	}
}
