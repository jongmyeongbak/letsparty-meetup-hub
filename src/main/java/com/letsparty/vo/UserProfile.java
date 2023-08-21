package com.letsparty.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfile {

	private int no;
	private String id;
	private String nickname;
	private String filename;
	private Boolean isDefault;
	
	public UserProfile() {
	}
	
	@Builder
	public UserProfile(String id, String nickname, String filename, Boolean isDefault) {
		this.id = id;
		this.nickname = nickname;
		this.filename = filename;
		this.isDefault = isDefault;
	}
}
