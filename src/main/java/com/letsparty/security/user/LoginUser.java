package com.letsparty.security.user;

import com.letsparty.vo.User;

import lombok.Getter;

@Getter
public class LoginUser {

	private String id;
	private String nickname;
	private String email;
	
	public LoginUser(User user) {
		super();
		this.id = user.getId();
		this.nickname = user.getName();
		this.email = user.getEmail();
	}
	
	
}
