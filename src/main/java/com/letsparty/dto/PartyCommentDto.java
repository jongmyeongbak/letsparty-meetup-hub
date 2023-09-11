package com.letsparty.dto;

import java.time.LocalDateTime;

import com.letsparty.vo.UserProfile;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class PartyCommentDto {

	private long no;
	private UserProfile profile;
	private String content;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
}
