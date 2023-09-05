package com.letsparty.dto;

import java.time.LocalDateTime;

import com.letsparty.vo.LetsPartyPost;
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
public class LetsPartyCommentDto {
	
	private long no;
	private LetsPartyPost post;
	private Party party;
	private User user;
	private UserProfile profile;
	private String content;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;

	public LetsPartyCommentDto() {}
	
	@Builder
	public LetsPartyCommentDto(LetsPartyPost post, Party party, User user, UserProfile profile, String content, LocalDateTime updatedAt,
			LocalDateTime createdAt) {
		super();
		this.post = post;
		this.party = party;
		this.user = user;
		this.profile= profile;
		this.content = content;
		this.updatedAt = updatedAt;
		this.createdAt = createdAt;
	}

}
