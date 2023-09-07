package com.letsparty.web.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfileForm {

	private Integer no;
	private String id;
	private String nickname;
	private String filename;
	private Boolean isDefault;
	private Boolean isUrl;
	
	@Builder
	public UserProfileForm(String id, String nickname, String filename, Boolean isDefault, Boolean isUrl) {
		this.id = id;
		this.nickname = nickname;
		this.filename = filename;
		this.isDefault = isDefault;
		this.isUrl = isUrl;
	}
}
