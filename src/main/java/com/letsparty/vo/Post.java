package com.letsparty.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Post {

	private long id;
	private int no;
	private int partyNo;
	private String userId;
	private String title;
	private String content;
	
}
