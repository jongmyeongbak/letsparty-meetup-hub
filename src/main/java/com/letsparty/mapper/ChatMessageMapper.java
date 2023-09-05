package com.letsparty.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.letsparty.vo.ChatMessage;

@Mapper
public interface ChatMessageMapper {

	void insertChatMessage(ChatMessage chatMessage);
	
	void decreaseUnreadCntByRoomNoAndLastReadMessageNo(@Param("roomNo") long roomNo, @Param("lastReadMessageNo") long lastReadMessageNo);
}
