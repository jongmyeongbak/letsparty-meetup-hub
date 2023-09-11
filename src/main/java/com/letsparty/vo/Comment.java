package com.letsparty.vo;

import java.sql.Date;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class Comment {
		private int no;
		private Post post;
		private User user;
		private String content;
		private Date createdAt;
		private Date updatedAt;
}
