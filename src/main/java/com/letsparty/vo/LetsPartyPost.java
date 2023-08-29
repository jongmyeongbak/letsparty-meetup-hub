package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LetsPartyPost {

	private long no;
	private Category category;	// category_no
	private Party party;		// party_no
	private User user;			// user_id
	private String title;
	private String content;
	private int readCnt;
	private int commentCnt;
	private int likeCnt;
	private boolean isDeleted;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
}
