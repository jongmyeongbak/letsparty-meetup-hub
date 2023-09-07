package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class LetsPartyPost {

	private long no;
	private Category category;
	private Party party;
	private User user;
	private String title;
	private String content;
	private int readCnt;
	private int commentCnt;
	private int likeCnt;
	private boolean isDeleted;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
	
	public LetsPartyPost() {}
	
	@Builder
	public LetsPartyPost(Category category, Party party, User user, String title, String content, int readCnt,
			int commentCnt, int likeCnt, boolean isDeleted, LocalDateTime updatedAt, LocalDateTime createdAt) {
		super();
		this.category = category;
		this.party = party;
		this.user = user;
		this.title = title;
		this.content = content;
		this.readCnt = readCnt;
		this.commentCnt = commentCnt;
		this.likeCnt = likeCnt;
		this.isDeleted = isDeleted;
		this.updatedAt = updatedAt;
		this.createdAt = createdAt;
	}
	
}
