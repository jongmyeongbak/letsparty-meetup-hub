package com.letsparty.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChatSummaryDto {

	private String roomId;
	private int chattersCnt;
	private String partyName;
	private String displayName;
	private String partyFilename;
	private long messageCount;
	private int messageType;
	private String messageText;
	private String latestMessageNickname;
}
