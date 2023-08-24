package com.letsparty.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPartyApplication {

	private long no;
	private int partyNo;
	private String userId;
	private int roleNo;
	private UserProfile userProfile;
	private String status;
	private LocalDateTime createdAt;
	private String userRemark;
}
