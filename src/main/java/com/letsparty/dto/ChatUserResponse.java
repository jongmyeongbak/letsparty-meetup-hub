package com.letsparty.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatUserResponse {

	private int userNo;
	private String nickname;
	private String filename;
	private boolean isUrl;
}
