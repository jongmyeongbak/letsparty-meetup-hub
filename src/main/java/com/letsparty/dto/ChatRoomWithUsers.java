package com.letsparty.dto;

import java.util.List;

import com.letsparty.vo.Party;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatRoomWithUsers {

	private int no;
	private String id;
	private int chattersCnt;
	private Party party;
	private List<ChatUserResponse> chatUsers;
	private boolean isPublic;
	private boolean isEssential;
	private String title;
	private String description;
	private String chatUsersText;
}
