package com.letsparty.dto;

import java.sql.Date;

import com.letsparty.vo.Post;
import com.letsparty.vo.User;
import com.letsparty.vo.UserProfile;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class PartyCommentDto {

	private int no;
	private Post post;
	private User user;
	private String content;
	private Date createdAt;
	private Date updatedAt;
}
