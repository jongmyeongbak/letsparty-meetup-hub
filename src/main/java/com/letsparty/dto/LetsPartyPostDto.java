package com.letsparty.dto;

import java.time.LocalDateTime;

import com.letsparty.vo.Category;
import com.letsparty.vo.Party;
import com.letsparty.vo.User;
import com.letsparty.vo.UserProfile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LetsPartyPostDto {

	private long no;
	private Category category;
	private Party party;
	private User user;
	private UserProfile profile;
	private String title;
	private String content;
	private int readCnt;
	private int commentCnt;
	private int likeCnt;
	private boolean isDeleted;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
	
	public LetsPartyPostDto() {}

	@Builder
	public LetsPartyPostDto(Category category, Party party, User user, UserProfile profile, String title,
			String content, int readCnt, int commentCnt, int likeCnt, boolean isDeleted, LocalDateTime updatedAt,
			LocalDateTime createdAt) {
		super();
		this.category = category;
		this.party = party;
		this.user = user;
		this.profile = profile;
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
