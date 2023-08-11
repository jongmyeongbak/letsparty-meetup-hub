package com.letsparty.web.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class AddUserForm {

	private String id;
	private String name;
	private Date birthday;
	private char gender;
	private String email;
	private String tel;
	private String password;
}
