package com.letsparty.web.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SignupForm {

	private String id;
	
	private String password;
	
	private String name;
	
	private String email;
	
	private String tel;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	private String gender;
}
