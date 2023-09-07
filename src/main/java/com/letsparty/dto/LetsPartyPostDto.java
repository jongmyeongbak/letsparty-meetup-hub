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
	
}
