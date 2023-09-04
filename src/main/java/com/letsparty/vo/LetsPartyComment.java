package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LetsPartyComment {

	private long no;
	private LetsPartyPost post;
	private Party party;
	private User user;
	private String content;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;

	public LetsPartyComment() {}
	
	@Builder
	public LetsPartyComment(LetsPartyPost post, Party party, User user, String content, LocalDateTime updatedAt,
			LocalDateTime createdAt) {
		super();
		this.post = post;
		this.party = party;
		this.user = user;
		this.content = content;
		this.updatedAt = updatedAt;
		this.createdAt = createdAt;
	}
	
	
	
}
