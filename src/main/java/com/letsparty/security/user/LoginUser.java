package com.letsparty.security.user;

import java.util.Date;

import com.letsparty.vo.User;

import lombok.Getter;

@Getter
public class LoginUser {

	private String id;
	private int no;
	private String name;
	private String email;
	private Date birthday;
	private String gender;
	private String tel;
	private String imageName;
	
	
	public LoginUser(User user) {
		super();
		this.id = user.getId();
		this.no = user.getNo();
		this.name = user.getName();
		this.email = user.getEmail();
		this.birthday = user.getBirthday();
		this.gender = user.getGender();
		this.tel = user.getTel();
	}
	
	public String getRealname() {
		return name;
	}
	
	public String getImageName() {
		return imageName;
	}
	
}
