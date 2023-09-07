package com.letsparty.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Media {

	private int no;
	private int partyNo;
	private User user;
	private String contentType;
	private Date createdAt;
	private boolean isDeleted;
	private String name;
	private String path;
	private Integer size;
	private Long postId;
}
