package com.letsparty.web.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.letsparty.vo.Post;
import com.letsparty.vo.User;

import groovy.transform.ToString;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class CommentForm {
	
	private long postId;
	
	private Post post;
	
	private User user;
	
	@NotNull
	private String userId;
	
	@NotNull
	@Size(max = 500, message = "댓글의 내용은 500자를 넘길 수 없습니다.")
	private String content;
}
