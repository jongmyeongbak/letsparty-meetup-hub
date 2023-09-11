package com.letsparty.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfile {

	private Integer no;
	private String id;
	private String nickname;
	private String filename;
	private Boolean isDefault;
	private Boolean isUrl;
	
	public UserProfile() {
	}
	
	@Builder
	public UserProfile(Integer no, String id, String nickname, String filename, Boolean isDefault, Boolean isUrl) {
		this.no = no;
		this.id = id;
		this.nickname = nickname;
		this.filename = filename;
		this.isDefault = isDefault;
		this.isUrl = isUrl;
	}
}
