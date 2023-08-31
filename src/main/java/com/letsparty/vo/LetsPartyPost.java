package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	
}
