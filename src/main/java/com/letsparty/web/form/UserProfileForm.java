package com.letsparty.web.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileForm {

	private int no;
	private String id;
	private String nickname;
	private String filename;
	private Boolean isDefault;
	
	@Builder
	public UserProfileForm(String id, String nickname, String filename, Boolean isDefault) {
		this.id = id;
		this.nickname = nickname;
		this.filename = filename;
		this.isDefault = isDefault;
	}
}
