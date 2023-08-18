package com.letsparty.web.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileForm {

	private String id;
	private String nickname;
	private String filename;
	private boolean isDefault;
	
	@Builder
	public UserProfileForm(String id, String nickname, String filename, boolean isDefault) {
		this.id = id;
		this.nickname = nickname;
		this.filename = filename;
		this.isDefault = isDefault;
	}
}
