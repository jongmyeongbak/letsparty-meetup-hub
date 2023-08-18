package com.letsparty.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

	private int no;
	private String id;
	private String nickname;
	private String filename;
	private boolean isDefault;
}
