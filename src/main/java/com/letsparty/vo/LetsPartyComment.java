package com.letsparty.vo;

import java.time.LocalDateTime;

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
	
}
