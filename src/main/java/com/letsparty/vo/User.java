package com.letsparty.vo;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
	
	private String id;
	private String name;
	private Date birthday;
	private char gender;
	private String email;
	private String tel;
	private boolean disabled;
	private LocalDateTime updatedAt;
	private LocalDateTime createdAt;
	private String providerType;
	private String password;
	
}
