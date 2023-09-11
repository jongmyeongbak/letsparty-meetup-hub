package com.letsparty.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessage {

	private long no;
	@JsonIgnore
	private long roomNo;
	private int type;
	private int userNo;
	private LocalDateTime createdAt;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long unreadCnt; // last_read_message_no를 담기 위하여 Integer 대신 Long을 사용함
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String text;
	
	@Builder
	public ChatMessage(long no, long roomNo, int type, int userNo, LocalDateTime createdAt, Long unreadCnt, String text) {
		super();
		this.no = no;
		this.roomNo = roomNo;
		this.type = type;
		this.userNo = userNo;
		this.createdAt = createdAt;
		this.unreadCnt = unreadCnt;
		this.text = text;
	}
}
