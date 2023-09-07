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
	private boolean isAuthor;

}
