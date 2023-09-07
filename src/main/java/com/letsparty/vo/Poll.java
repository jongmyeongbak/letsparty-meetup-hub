package com.letsparty.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Poll {
	
	private int no;
	private Party party;
	private User user;
	private String title;
	private String description;
	private boolean isAnonymous;
	private boolean isDuplicable;
	private Date createdAt;
	private Date endedAt;
	private Post post;
	

}
